package com.sapotest.flashsale.worker;

import com.sapotest.flashsale.model.entity.Order;
import com.sapotest.flashsale.repository.OrderRepository;
import com.sapotest.flashsale.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled task for cleaning up expired orders and stale inventory reservations.
 * Acts as a secondary safety net for the system to ensure inventory is always released.
 */
@Service
public class InventoryCleanupTask {

    private static final Logger log = LoggerFactory.getLogger(InventoryCleanupTask.class);
    private final ReservationRepository reservationRepository;
    private final OrderRepository orderRepository;

    public InventoryCleanupTask(ReservationRepository reservationRepository, OrderRepository orderRepository) {
        this.reservationRepository = reservationRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Periodically scans for PENDING orders that have timed out (exceeded 30s-40s).
     * Automatically cancels these orders and releases the reserved inventory back to the pool.
     */
    @Scheduled(fixedRate = 30000) // Executes every 30 seconds
    @Transactional
    public void releaseExpiredOrdersAndReservations() {
        LocalDateTime now = LocalDateTime.now();

        /*
         * Orders older than 40 seconds are considered expired.
         * This threshold aligns with the 30-second Captcha expiration window.
         */
        LocalDateTime timeout = now.minusSeconds(40);

        List<Order> expiredOrders = orderRepository.findAllByStatusAndCreatedAtBefore(
                Order.OrderStatus.PENDING,
                timeout
        );

        if (!expiredOrders.isEmpty()) {
            log.info("Cleanup task found {} expired PENDING orders.", expiredOrders.size());

            for (Order order : expiredOrders) {
                // Transition order status to CANCELLED
                order.setStatus(Order.OrderStatus.CANCELLED);

                // Release inventory by removing the corresponding reservation
                reservationRepository.deleteByOrderId(order.getId());

                log.info("CLEANUP_SYSTEM: Cancelled Order ID: {}", order.getId());
            }
            // Batch update all expired orders
            orderRepository.saveAll(expiredOrders);
        }

        /*
         * Cleanup stray reservations that might not have a direct order link
         * but have surpassed their individual expiration time.
         */
        int deletedReservations = reservationRepository.deleteByExpiresAtBefore(now);

        if (deletedReservations > 0) {
            log.info("CLEANUP_SYSTEM: Cleared {} stray reservations.", deletedReservations);
        }
    }
}