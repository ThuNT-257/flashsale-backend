package com.sapotest.flashsale.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

/**
 * Data Transfer Object representing an individual item within an order request.
 */
public class OrderItemRequest {

    private UUID productId;
    private int quantity;

    @JsonProperty("isFlashSale")
    private boolean isFlashSale;

    /**
     * Default constructor for JSON deserialization.
     */
    public OrderItemRequest() {}

    /**
     * Parameterized constructor.
     * @param productId   The unique ID of the product.
     * @param quantity    Number of units requested.
     * @param isFlashSale Flag indicating if this is a flash sale item.
     */
    public OrderItemRequest(UUID productId, int quantity, boolean isFlashSale) {
        this.productId = productId;
        this.quantity = quantity;
        this.isFlashSale = isFlashSale;
    }

    // Standard Getters and Setters
    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    // Explicitly naming Getter/Setter to match JsonProperty for consistency
    @JsonProperty("isFlashSale")
    public boolean isFlashSale() { return isFlashSale; }

    @JsonProperty("isFlashSale")
    public void setFlashSale(boolean flashSale) { isFlashSale = flashSale; }
}