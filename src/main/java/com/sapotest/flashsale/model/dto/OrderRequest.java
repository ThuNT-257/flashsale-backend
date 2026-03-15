package com.sapotest.flashsale.model.dto;

import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object representing a complete order placement request.
 * Contains user identification, flash sale session context, and a list of requested items.
 */
public class OrderRequest {

    private UUID userId;
    private UUID flashSaleId;
    private List<OrderItemRequest> items;

    /**
     * Default constructor for JSON deserialization.
     */
    public OrderRequest() {}

    /**
     * Parameterized constructor for convenience.
     * @param userId      The unique ID of the user placing the order.
     * @param flashSaleId The ID of the active flash sale session.
     * @param items       The list of items (products and quantities) in this order.
     */
    public OrderRequest(UUID userId, UUID flashSaleId, List<OrderItemRequest> items) {
        this.userId = userId;
        this.flashSaleId = flashSaleId;
        this.items = items;
    }

    // Getters and Setters
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getFlashSaleId() { return flashSaleId; }
    public void setFlashSaleId(UUID flashSaleId) { this.flashSaleId = flashSaleId; }

    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }
}