package com.sapotest.flashsale.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity to track and limit the number of items a specific user
 * has purchased for a particular flash sale product.
 */
@Entity
@Table(name = "flash_sale_user_limits",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "flash_sale_product_id"})})
public class FlashSaleUserLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(name = "flash_sale_product_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID flashSaleProductId;

    @Column(name = "bought_quantity", nullable = false)
    private int boughtQuantity = 0;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Optimistic locking to handle concurrent updates to the user's purchase count.
     */
    @Version
    private Long version;

    /**
     * Default constructor for JPA.
     */
    public FlashSaleUserLimit() {}

    public FlashSaleUserLimit(UUID userId, UUID flashSaleProductId, int boughtQuantity) {
        this.userId = userId;
        this.flashSaleProductId = flashSaleProductId;
        this.boughtQuantity = boughtQuantity;
    }

    @PreUpdate
    @PrePersist
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // --- Getters and Setters ---

    public UUID getId() { return id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getFlashSaleProductId() { return flashSaleProductId; }
    public void setFlashSaleProductId(UUID flashSaleProductId) { this.flashSaleProductId = flashSaleProductId; }

    public int getBoughtQuantity() { return boughtQuantity; }
    public void setBoughtQuantity(int boughtQuantity) { this.boughtQuantity = boughtQuantity; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public Long getVersion() { return version; }
}