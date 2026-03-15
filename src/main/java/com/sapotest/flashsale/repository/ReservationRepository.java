package com.sapotest.flashsale.repository;

import com.sapotest.flashsale.model.entity.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for managing inventory reservations.
 * Handles calculation of pending stock and cleanup of expired locks.
 */
@Repository
public interface ReservationRepository extends JpaRepository<InventoryReservation, UUID> {

    /**
     * Calculates the total quantity currently locked by active reservations.
     * Available Stock = Total Stock - sumReservedQuantityByProduct.
     */
    @Query("SELECT COALESCE(SUM(r.quantity), 0) FROM InventoryReservation r " +
            "WHERE r.productId = :productId AND r.expiresAt > :now")
    int sumReservedQuantityByProduct(@Param("productId") UUID productId, @Param("now") LocalDateTime now);

    /**
     * Deletes reservations that have surpassed their expiration time.
     * This releases the locked stock back to the public pool.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM InventoryReservation r WHERE r.expiresAt < :now")
    int deleteByExpiresAtBefore(@Param("now") LocalDateTime now);

    /**
     * Finds all reservations linked to a specific order.
     */
    List<InventoryReservation> findByOrderId(UUID orderId);

    /**
     * Explicitly releases reservations when an order is completed or manually cancelled.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM InventoryReservation r WHERE r.orderId = :orderId")
    void deleteByOrderId(@Param("orderId") UUID orderId);
}