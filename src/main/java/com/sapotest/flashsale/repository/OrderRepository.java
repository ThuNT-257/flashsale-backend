package com.sapotest.flashsale.repository;

import com.sapotest.flashsale.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Repository for managing Order entities.
 * Includes methods for order history retrieval and maintenance tasks.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    /**
     * Retrieves the order history for a specific user, sorted by creation time.
     */
    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Finds orders with a specific status created before a certain timestamp.
     * Essential for automated tasks to cancel expired PENDING orders.
     */
    List<Order> findAllByStatusAndCreatedAtBefore(Order.OrderStatus status, LocalDateTime dateTime);

    /**
     * Aggregates the total quantity of flash sale items for a user.
     */
    @Query("SELECT COALESCE(SUM(o.quantity), 0) FROM Order o " +
            "WHERE o.userId = :userId " +
            "AND o.flashSaleProductId IS NOT NULL " +
            "AND o.status IN :statuses")
    int sumTotalFlashSaleQuantityByUser(
            @Param("userId") UUID userId,
            @Param("statuses") Collection<Order.OrderStatus> statuses);
}