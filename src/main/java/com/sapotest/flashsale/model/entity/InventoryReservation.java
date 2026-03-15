package com.sapotest.flashsale.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity to handle temporary stock reservations during the checkout process.
 * Ensures stock is held for a user until payment is completed or the reservation expires.
 */
@Entity
@Table(name = "inventory_reservations")
public class InventoryReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "product_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID productId;

    @Column(name = "order_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID orderId;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "is_flash_sale", nullable = false)
    private boolean isFlashSale;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Optimistic locking to prevent race conditions during reservation management.
     */
    @Version
    @Column(nullable = false)
    private Long version = 0L;

    /**
     * Default constructor for JPA.
     */
    public InventoryReservation() {}

    public InventoryReservation(UUID productId, UUID orderId, int quantity, boolean isFlashSale, LocalDateTime expiresAt) {
        this.productId = productId;
        this.orderId = orderId;
        this.quantity = quantity;
        this.isFlashSale = isFlashSale;
        this.expiresAt = expiresAt;
    }

    // --- Getters and Setters ---

    public UUID getId() { return id; }

    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }

    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public boolean isFlashSale() { return isFlashSale; }
    public void setFlashSale(boolean flashSale) { isFlashSale = flashSale; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public Long getVersion() { return version; }
}