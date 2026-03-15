package com.sapotest.flashsale.service.impl;

import com.sapotest.flashsale.model.entity.*;
import com.sapotest.flashsale.repository.*;
import com.sapotest.flashsale.service.FlashSaleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service implementation for managing Flash Sale transactions.
 * Designed to handle high-concurrency environments and prevent overselling/double-spending.
 */
@Service
public class FlashSaleServiceImpl implements FlashSaleService {

    private final FlashSaleProductRepository flashSaleProductRepository;
    private final FlashSaleUserLimitRepository flashSaleUserLimitRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public FlashSaleServiceImpl(FlashSaleProductRepository fsProductRepo,
                                FlashSaleUserLimitRepository fsLimitRepo,
                                OrderRepository orderRepo,
                                OrderItemRepository orderItemRepo) {
        this.flashSaleProductRepository = fsProductRepo;
        this.flashSaleUserLimitRepository = fsLimitRepo;
        this.orderRepository = orderRepo;
        this.orderItemRepository = orderItemRepo;
    }

    /**
     * Executes a product purchase within a flash sale.
     * Utilizes Optimistic Locking and Atomic Database Updates to ensure data integrity.
     */
    @Override
    @Transactional
    public Order purchaseProduct(UUID userId, UUID flashSaleProductId, int quantity) {
        // 1. Fetch the flash sale product and check stock availability.
        // Optimistic locking via @Version ensures stock consistency during concurrent requests.
        FlashSaleProduct item = flashSaleProductRepository.findById(flashSaleProductId)
                .orElseThrow(() -> new RuntimeException("Flash sale product not found or session ended."));

        if (item.getStock() < quantity) {
            throw new RuntimeException("Product out of stock.");
        }

        // 2. Prevent Double-Spending using Atomic Database Update.
        // Increments quantity only if the user has not exceeded the purchase limit (e.g., max 2 items).
        int updatedRows = flashSaleUserLimitRepository.incrementQuantity(userId, flashSaleProductId);
        if (updatedRows == 0) {
            // Either the record doesn't exist yet, or the user reached the limit.
            handleUserPurchaseLimit(userId, flashSaleProductId, quantity);
        }

        // 3. Update stock. Hibernate will automatically check the version column for conflicts.
        item.setStock(item.getStock() - quantity);
        flashSaleProductRepository.save(item);

        // 4. Record the transaction.
        Order order = new Order(userId, flashSaleProductId, quantity, item.getSalePrice(), Order.OrderStatus.SUCCESS);
        Order savedOrder = orderRepository.save(order);

        OrderItem orderItem = new OrderItem(
                savedOrder,
                item.getProductId(),
                flashSaleProductId,
                quantity,
                item.getSalePrice()
        );
        orderItemRepository.save(orderItem);

        return savedOrder;
    }

    /**
     * Handles initial purchase record creation and prevents race conditions using Unique Constraints.
     */
    private void handleUserPurchaseLimit(UUID userId, UUID productId, int quantity) {
        if (quantity > 2) throw new RuntimeException("Purchase limit exceeded.");

        try {
            // Attempt to create a new limit record for the first-time buyer.
            FlashSaleUserLimit newLimit = new FlashSaleUserLimit(userId, productId, quantity);
            flashSaleUserLimitRepository.save(newLimit);
        } catch (Exception e) {
            // If insertion fails due to Unique Constraint, another thread created it first.
            // Retry atomic increment to ensure the count is still valid.
            int retryUpdate = flashSaleUserLimitRepository.incrementQuantity(userId, productId);
            if (retryUpdate == 0) {
                throw new RuntimeException("User has already reached the purchase limit for this product.");
            }
        }
    }
}