package com.sapotest.flashsale.service.impl;

import com.sapotest.flashsale.exception.ResourceNotFoundException;
import com.sapotest.flashsale.model.dto.OrderItemRequest;
import com.sapotest.flashsale.model.dto.OrderRequest;
import com.sapotest.flashsale.model.entity.*;
import com.sapotest.flashsale.repository.*;
import com.sapotest.flashsale.service.OrderService;
import com.sapotest.flashsale.worker.InventoryWorker;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation for managing the order lifecycle.
 * Handles order placement, inventory reservation, and final confirmation.
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final ReservationRepository reservationRepository;
    private final ProductRepository productRepository;
    private final FlashSaleProductRepository flashSaleProductRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryWorker inventoryWorker;
    private final FlashSaleUserLimitRepository userLimitRepository;

    public OrderServiceImpl(ReservationRepository reservationRepository,
                            ProductRepository productRepository,
                            FlashSaleProductRepository flashSaleProductRepository,
                            OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            InventoryWorker inventoryWorker,
                            FlashSaleUserLimitRepository userLimitRepository) {
        this.reservationRepository = reservationRepository;
        this.productRepository = productRepository;
        this.flashSaleProductRepository = flashSaleProductRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.inventoryWorker = inventoryWorker;
        this.userLimitRepository = userLimitRepository;
    }

    @Override
    @Transactional
    public void placeOrder(OrderRequest request) {
        createOrder(request);
    }

    /**
     * Initializes an order with PENDING status and triggers inventory reservation.
     * * @param request Data transfer object containing order details
     * @return UUID of the newly created order
     * @throws ValidationException if purchase limits are exceeded
     */
    @Transactional
    public UUID createOrder(OrderRequest request) {
        /*
         * 1. Validate Flash Sale quantity constraints
         */
        int currentRequestFSQuantity = request.getItems().stream()
                .filter(OrderItemRequest::isFlashSale)
                .mapToInt(OrderItemRequest::getQuantity)
                .sum();

        if (currentRequestFSQuantity > 2) {
            throw new ValidationException("Maximum of 2 Flash Sale items per order!");
        }

        /*
         * 2. Atomic check & update for purchase limits
         * Prevents Race Conditions at the database level using native queries.
         */
        if (currentRequestFSQuantity > 0) {
            for (OrderItemRequest item : request.getItems()) {
                if (item.isFlashSale()) {
                    int updatedRows = userLimitRepository.incrementQuantity(
                            request.getUserId(),
                            item.getProductId()
                    );

                    if (updatedRows == 0) {
                        throw new ValidationException("Flash sale limit reached or session invalid.");
                    }
                }
            }
        }

        try {
            BigDecimal calculatedTotal = BigDecimal.ZERO;
            int totalQuantity = 0;

            /*
             * 3. Calculate total price using BigDecimal for financial accuracy
             */
            for (OrderItemRequest item : request.getItems()) {
                if (item.isFlashSale()) {
                    FlashSaleProduct fsp = flashSaleProductRepository
                            .findByProductIdAndFlashSaleId(item.getProductId(), request.getFlashSaleId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not in Flash Sale!"));

                    BigDecimal itemPrice = fsp.getSalePrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    calculatedTotal = calculatedTotal.add(itemPrice);
                } else {
                    Product p = productRepository.findById(item.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found!"));

                    BigDecimal itemPrice = p.getBasePrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    calculatedTotal = calculatedTotal.add(itemPrice);
                }
                totalQuantity += item.getQuantity();
            }

            Order order = new Order(
                    request.getUserId(),
                    request.getFlashSaleId(),
                    totalQuantity,
                    calculatedTotal,
                    Order.OrderStatus.PENDING
            );

            Order savedOrder = orderRepository.save(order);

            /*
             * 4. Asynchronous inventory reservation via worker
             */
            inventoryWorker.process(savedOrder, request.getItems());

            return savedOrder.getId();

        } catch (Exception e) {
            // Spring @Transactional will handle the rollback automatically
            throw e;
        }
    }

    /**
     * Finalizes the order by converting temporary reservations into actual stock deductions.
     * * @param orderId ID of the order to confirm
     */
    @Transactional
    public void confirmOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (order.getStatus() == Order.OrderStatus.SUCCESS) return;

        List<InventoryReservation> reservations = reservationRepository.findByOrderId(orderId);
        if (reservations.isEmpty()) {
            throw new ResourceNotFoundException("No active reservations found for this order.");
        }

        /*
         * Deduct stock from the appropriate source (Flash Sale vs Standard)
         */
        for (InventoryReservation res : reservations) {
            if (res.isFlashSale()) {
                FlashSaleProduct fsProduct = flashSaleProductRepository
                        .findByProductIdAndFlashSaleId(res.getProductId(), order.getFlashSaleProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Flash Sale product record missing."));

                if (fsProduct.getStock() < res.getQuantity()) {
                    throw new IllegalStateException("Insufficient stock for Flash Sale item.");
                }
                fsProduct.setStock(fsProduct.getStock() - res.getQuantity());
                flashSaleProductRepository.save(fsProduct);
            } else {
                Product product = productRepository.findById(res.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product record missing."));

                if (product.getStock() < res.getQuantity()) {
                    throw new IllegalStateException("Insufficient standard stock.");
                }
                product.setStock(product.getStock() - res.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(Order.OrderStatus.SUCCESS);
        orderRepository.save(order);

        /*
         * Cleanup: Reservations are no longer needed once stock is deducted
         */
        reservationRepository.deleteByOrderId(orderId);
    }

    /**
     * Cancels a pending order and releases any reserved inventory back to the pool.
     * * @param orderId ID of the order to cancel
     */
    @Transactional
    public void cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found."));

        if (order.getStatus() == Order.OrderStatus.PENDING) {
            order.setStatus(Order.OrderStatus.CANCELLED);
            orderRepository.save(order);
            reservationRepository.deleteByOrderId(orderId);
        }
    }
}