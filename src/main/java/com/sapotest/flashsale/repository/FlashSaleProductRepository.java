package com.sapotest.flashsale.repository;

import com.sapotest.flashsale.model.entity.FlashSaleProduct;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing FlashSaleProduct entities with support for
 * optimistic locking and custom queries for active sales.
 */
@Repository
public interface FlashSaleProductRepository extends JpaRepository<FlashSaleProduct, UUID> {

    /**
     * Finds a product in a specific flash sale with an optimistic lock.
     * Essential for preventing stock overselling during high concurrency.
     */
    @Lock(LockModeType.OPTIMISTIC)
    Optional<FlashSaleProduct> findByProductIdAndFlashSaleId(UUID productId, UUID flashSaleId);

    /**
     * Retrieves all products belonging to active flash sale sessions.
     * Uses JOIN FETCH to avoid N+1 select problems.
     */
    @Query("SELECT fsp FROM FlashSaleProduct fsp " +
            "JOIN FETCH fsp.flashSale fs " +
            "WHERE fs.isActive = true")
    List<FlashSaleProduct> findAllActiveFlashSaleProducts();

    /**
     * Check for stock existence.
     */
    @Query("SELECT fsp.stock FROM FlashSaleProduct fsp WHERE fsp.id = :id")
    Integer getStockById(@Param("id") UUID id);
}