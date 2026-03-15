package com.sapotest.flashsale.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a customer order.
 * Includes indexing for performance and optimistic locking for data integrity.
 */
@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(name = "flash_sale_product_id", columnDefinition = "BINARY(16)")
    private UUID flashSaleProductId;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "total_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    /**
     * Default constructor for JPA.
     */
    public Order() {}

    /**
     * Parameterized constructor for placing a new order.
     */
    public Order(UUID userId, UUID flashSaleProductId, int quantity, BigDecimal totalPrice, OrderStatus status) {
        this.userId = userId;
        this.flashSaleProductId = flashSaleProductId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = OrderStatus.PENDING;
        }
    }

    // --- Getters and Setters ---

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getFlashSaleProductId() { return flashSaleProductId; }
    public void setFlashSaleProductId(UUID flashSaleProductId) { this.flashSaleProductId = flashSaleProductId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public Long getVersion() { return version; }

    /**
     * Enumeration for order statuses.
     */
    public enum OrderStatus {
        PENDING, SUCCESS, CANCELLED, FAILED
    }
}