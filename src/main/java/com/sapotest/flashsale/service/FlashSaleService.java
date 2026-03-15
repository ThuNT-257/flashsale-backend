package com.sapotest.flashsale.service;

import com.sapotest.flashsale.model.entity.Order;
import java.util.UUID;

/**
 * Service interface for handling high-concurrency Flash Sale transactions.
 * Provides core business logic for processing purchases with strict quantity controls.
 */
public interface FlashSaleService {

    /**
     * Processes a product purchase during a flash sale event.
     * Implementation should handle stock deduction and enforce per-user purchase limits.
     * * @param userId             The unique identifier of the customer
     * @param flashSaleProductId The ID of the specific flash sale product entry
     * @param quantity           The number of items the user intends to buy
     * @return The created Order entity upon successful transaction
     * @throws RuntimeException if stock is insufficient or purchase limits are exceeded
     */
    Order purchaseProduct(UUID userId, UUID flashSaleProductId, int quantity);
}