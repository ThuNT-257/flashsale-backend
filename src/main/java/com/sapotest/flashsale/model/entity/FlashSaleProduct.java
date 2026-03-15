package com.sapotest.flashsale.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entity mapping products to specific Flash Sale sessions.
 * Manages flash sale pricing, stock levels, and utilizes Optimistic Locking via @Version.
 */
@Entity
@Table(name = "flash_sale_products")
public class FlashSaleProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flash_sale_id", nullable = false)
    private FlashSale flashSale;

    @Column(name = "product_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID productId;

    @Column(name = "sale_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal salePrice;

    @Column(nullable = false)
    private int stock = 0;

    /**
     * Optimistic locking version field to prevent lost updates
     * when multiple threads attempt to decrement stock simultaneously.
     */
    @Version
    @Column(nullable = false)
    private Long version = 0L;

    /**
     * Default constructor for JPA.
     */
    public FlashSaleProduct() {}

    // --- Getters and Setters ---

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public FlashSale getFlashSale() { return flashSale; }
    public void setFlashSale(FlashSale flashSale) { this.flashSale = flashSale; }

    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }

    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}