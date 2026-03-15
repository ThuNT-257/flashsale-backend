package com.sapotest.flashsale.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.sapotest.flashsale.model.entity.Order;
import com.sapotest.flashsale.repository.OrderRepository;
import com.sapotest.flashsale.repository.ReservationRepository;
import com.sapotest.flashsale.service.CaptchaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of CaptchaService using Caffeine for in-memory storage.
 * Automatically triggers order cancellation upon captcha expiration.
 */
@Service
public class CaptchaServiceImpl implements CaptchaService {

    private static final Logger log = LoggerFactory.getLogger(CaptchaServiceImpl.class);

    private static final SecureRandom secureRandom = new SecureRandom();

    private final OrderRepository orderRepository;
    private final ReservationRepository reservationRepository;
    private final Cache<UUID, String> captchaStore;

    public CaptchaServiceImpl(OrderRepository orderRepository, ReservationRepository reservationRepository) {
        this.orderRepository = orderRepository;
        this.reservationRepository = reservationRepository;

        this.captchaStore = Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .maximumSize(10000)
                .removalListener((UUID orderId, String code, RemovalCause cause) -> {
                    if (cause == RemovalCause.EXPIRED && orderId != null) {
                        // Note: RemovalListener of Cafferine run on a separate thread pool
                        handleAutoCancelOrder(orderId);
                    }
                })
                .build();
    }

    @Override
    public String generateCaptcha(UUID orderId) {
        String code = String.format("%04d", secureRandom.nextInt(10000));
        captchaStore.put(orderId, code);
        return code;
    }

    @Override
    public boolean validateCaptcha(UUID orderId, String inputCode) {
        String validCode = captchaStore.getIfPresent(orderId);
        if (validCode == null) return false;

        boolean isMatch = validCode.equals(inputCode);
        if (isMatch) {
            captchaStore.invalidate(orderId); // Single-use captcha
        }
        return isMatch;
    }

    /**
     * Logic to cancel order and release inventory when time is up.
     * Annotated with @Transactional to ensure data integrity.
     */
    @Transactional
    public void handleAutoCancelOrder(UUID orderId) {
        orderRepository.findById(orderId).ifPresent(order -> {
            if (order.getStatus() == Order.OrderStatus.PENDING) {
                order.setStatus(Order.OrderStatus.CANCELLED);
                orderRepository.save(order);

                //Note: Release reservation for others to buy the product
                reservationRepository.deleteByOrderId(orderId);

                log.info("AUTO-CANCEL: Captcha expired for Order {}. Inventory released.", orderId);
            }
        });
    }
}