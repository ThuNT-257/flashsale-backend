package com.sapotest.flashsale.repository;

import com.sapotest.flashsale.model.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for standard Product entities.
 * Includes optimistic locking support for safe inventory adjustments.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    /**
     * Finds a product by ID and applies an optimistic lock.
     * This ensures the 'version' field is checked during the transaction commit
     * to prevent lost updates in a high-concurrency environment.
     */
    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") UUID id);

    /**
     * Simple check for product existence and current stock level.
     */
    @Query("SELECT p.stock FROM Product p WHERE p.id = :id")
    Integer getStockLevel(@Param("id") UUID id);
}