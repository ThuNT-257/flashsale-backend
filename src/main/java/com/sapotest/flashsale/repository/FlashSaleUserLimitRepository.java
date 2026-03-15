package com.sapotest.flashsale.repository;

import com.sapotest.flashsale.model.entity.FlashSaleUserLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing user purchase limits.
 * Implements atomic updates to prevent double-spending in high-concurrency environments.
 */
@Repository
public interface FlashSaleUserLimitRepository extends JpaRepository<FlashSaleUserLimit, UUID> {

    /**
     * Finds the limit record for a specific user and flash sale product.
     */
    Optional<FlashSaleUserLimit> findByUserIdAndFlashSaleProductId(UUID userId, UUID flashSaleProductId);

    /**
     * Atomically increments the bought quantity only if it's below the allowed limit (2).
     * This native query uses database-level row locking to ensure thread safety.
     * * @return The number of rows affected (1 if successful, 0 if limit reached).
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE flash_sale_user_limits " +
            "SET bought_quantity = bought_quantity + 1, " +
            "    version = version + 1, " +
            "    updated_at = NOW() " +
            "WHERE user_id = :userId " +
            "AND flash_sale_product_id = :productId " +
            "AND bought_quantity < 2", nativeQuery = true)
    int tryIncrementBoughtQuantity(@Param("userId") byte[] userId, @Param("productId") byte[] productId);

    /**
     * Helper method to handle UUID to byte[] conversion for MySQL BINARY(16).
     */
    default int incrementQuantity(UUID userId, UUID productId) {
        return tryIncrementBoughtQuantity(convertUuidToBytes(userId), convertUuidToBytes(productId));
    }

    private byte[] convertUuidToBytes(UUID uuid) {
        java.nio.ByteBuffer bb = java.nio.ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
}