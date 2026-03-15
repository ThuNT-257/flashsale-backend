package com.sapotest.flashsale.repository;

import com.sapotest.flashsale.model.entity.Order;
import com.sapotest.flashsale.model.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

/**
 * Repository for OrderItem entities.
 * Provides aggregate queries to calculate total purchased quantities per user.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    /**
     * Calculates the sum of quantities for a specific product in a flash sale by a user.
     * Uses COALESCE to ensure a return value of 0 instead of null.
     */
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE o.userId = :userId " +
            "AND oi.flashSaleProductId = :flashSaleProductId " +
            "AND o.status IN :statuses")
    int sumQuantityByUserAndStatus(
            @Param("userId") UUID userId,
            @Param("flashSaleProductId") UUID flashSaleProductId,
            @Param("statuses") Collection<Order.OrderStatus> statuses);

    /**
     * Calculates the total quantity of all flash sale items purchased by a user.
     * Useful for global flash sale participation limits.
     */
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE o.userId = :userId " +
            "AND oi.flashSaleProductId != '00000000-0000-0000-0000-000000000000' " +
            "AND o.status IN :statuses")
    int sumTotalFlashSaleQuantityByUser(
            @Param("userId") UUID userId,
            @Param("statuses") Collection<Order.OrderStatus> statuses);
}