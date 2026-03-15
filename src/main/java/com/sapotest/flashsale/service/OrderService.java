package com.sapotest.flashsale.service;

import com.sapotest.flashsale.model.dto.OrderRequest;
import java.util.UUID;

/**
 * Service interface for managing the complete order lifecycle.
 * Defines methods for placing, confirming, and cancelling orders
 * with integrated inventory reservation logic.
 */
public interface OrderService {

    /**
     * Initializes a new order in PENDING status.
     * Validates purchase limits and triggers the inventory reservation process.
     * * @param request Data transfer object containing user and product details
     * @return The unique identifier (UUID) of the created order
     */
    UUID createOrder(OrderRequest request);

    /**
     * Entry point for placing an order.
     * Typically orchestrates the initial order creation and pre-processing tasks.
     * * @param request The order placement request data
     */
    void placeOrder(OrderRequest request);

    /**
     * Confirms a pending order, converting reserved inventory into final stock deductions.
     * Transitions the order status to SUCCESS.
     * * @param orderId The unique identifier of the order to confirm
     */
    void confirmOrder(UUID orderId);

    /**
     * Cancels an existing order and releases any reserved inventory back to the pool.
     * Transitions the order status to CANCELLED.
     * * @param orderId The unique identifier of the order to cancel
     */
    void cancelOrder(UUID orderId);
}