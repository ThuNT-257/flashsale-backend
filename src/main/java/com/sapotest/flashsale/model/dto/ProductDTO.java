package com.sapotest.flashsale.model.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Data Transfer Object for displaying product details,
 * including flash sale pricing and stock information.
 */
public class ProductDTO {
    private UUID id;
    private String name;
    private BigDecimal basePrice;
    private BigDecimal salePrice;
    private Integer stock;
    private UUID flashSaleId;

    /**
     * Default constructor for JSON serialization.
     */
    public ProductDTO() {}

    /**
     * Parameterized constructor.
     * @param id          Product unique identifier.
     * @param name        Product name.
     * @param basePrice   Original price.
     * @param salePrice   Discounted price during flash sale.
     * @param stock       Available inventory count.
     * @param flashSaleId The associated flash sale session ID.
     */
    public ProductDTO(UUID id, String name, BigDecimal basePrice, BigDecimal salePrice, Integer stock, UUID flashSaleId) {
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
        this.salePrice = salePrice;
        this.stock = stock;
        this.flashSaleId = flashSaleId;
    }

    // Standard Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public UUID getFlashSaleId() { return flashSaleId; }
    public void setFlashSaleId(UUID flashSaleId) { this.flashSaleId = flashSaleId; }
}