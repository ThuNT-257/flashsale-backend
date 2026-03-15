package com.sapotest.flashsale.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entity representing an individual line item within an Order.
 * Captures the snapshot of price and flash sale context at the time of purchase.
 */
@Entity
@Table(name = "order_items")
public class OrderItem {

    // Using a consistent null-representation for non-flash-sale items
    private static final UUID DEFAULT_FLASH_SALE_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID productId;

    @Column(name = "flash_sale_product_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID flashSaleProductId = DEFAULT_FLASH_SALE_ID;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    /**
     * Default constructor for JPA.
     */
    public OrderItem() {}

    /**
     * Parameterized constructor.
     */
    public OrderItem(Order order, UUID productId, UUID flashSaleProductId, int quantity, BigDecimal price) {
        this.order = order;
        this.productId = productId;
        this.flashSaleProductId = (flashSaleProductId != null) ? flashSaleProductId : DEFAULT_FLASH_SALE_ID;
        this.quantity = quantity;
        this.price = price;
    }

    // --- Getters and Setters ---

    public UUID getId() { return id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }

    public UUID getFlashSaleProductId() { return flashSaleProductId; }
    public void setFlashSaleProductId(UUID flashSaleProductId) {
        this.flashSaleProductId = (flashSaleProductId != null) ? flashSaleProductId : DEFAULT_FLASH_SALE_ID;
    }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Long getVersion() { return version; }
}