package com.sapotest.flashsale.worker;

import com.sapotest.flashsale.exception.ResourceNotFoundException;
import com.sapotest.flashsale.model.dto.OrderItemRequest;
import com.sapotest.flashsale.model.entity.*;
import com.sapotest.flashsale.repository.*;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Worker service responsible for inventory validation and reservation.
 * Orchesrates the bridge between real-time stock and pending order reservations.
 */
@Service
public class InventoryWorker {

    private final ProductRepository productRepository;
    private final FlashSaleProductRepository flashSaleProductRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReservationRepository reservationRepository;

    public InventoryWorker(ProductRepository productRepository,
                           FlashSaleProductRepository flashSaleProductRepository,
                           OrderItemRepository orderItemRepository,
                           ReservationRepository reservationRepository) {
        this.productRepository = productRepository;
        this.flashSaleProductRepository = flashSaleProductRepository;
        this.orderItemRepository = orderItemRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Processes order items by validating purchase limits and reserving stock.
     * * @param order The current pending order
     * @param items List of requested items from the customer
     * @throws ValidationException if stock is insufficient or user limits are exceeded
     */
    @Transactional
    public void process(Order order, List<OrderItemRequest> items) {

        // Filter out items that are part of a Flash Sale
        List<OrderItemRequest> flashSaleItems = items.stream()
                .filter(OrderItemRequest::isFlashSale)
                .toList();

        if (!flashSaleItems.isEmpty()) {
            /*
             * 1. Validate Purchase History
             * Only counts orders that are either PENDING (being processed) or SUCCESS (completed).
             */
            List<Order.OrderStatus> validStatuses = Arrays.asList(
                    Order.OrderStatus.PENDING,
                    Order.OrderStatus.SUCCESS
            );

            Integer currentPurchasedInDB = orderItemRepository.sumQuantityByUserAndStatus(
                    order.getUserId(),
                    order.getFlashSaleProductId(),
                    validStatuses
            );

            int historyCount = (currentPurchasedInDB != null) ? currentPurchasedInDB : 0;

            /*
             * 2. Calculate requested quantity in the current order
             */
            int currentRequestCount = flashSaleItems.stream()
                    .mapToInt(OrderItemRequest::getQuantity)
                    .sum();

            /*
             * 3. RULE: Total (History + Current) must not exceed the limit of 2 items
             */
            if (historyCount + currentRequestCount > 2) {
                throw new ValidationException(String.format(
                        "Flash Sale Limit Exceeded! Maximum allowed: 2. (Previous: %d, Current: %d)",
                        historyCount, currentRequestCount
                ));
            }
        }

        /*
         * 4. Iterate through items to check availability and create reservations
         */
        for (OrderItemRequest itemReq : items) {
            int currentStock;
            BigDecimal price;
            UUID actualFlashSaleProductId = null;

            if (itemReq.isFlashSale()) {
                // Fetch specific Flash Sale configuration for this event
                FlashSaleProduct fsProduct = flashSaleProductRepository
                        .findByProductIdAndFlashSaleId(itemReq.getProductId(), order.getFlashSaleProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product is not configured for this Flash Sale event."));

                currentStock = fsProduct.getStock();
                price = fsProduct.getSalePrice();
                actualFlashSaleProductId = fsProduct.getId();
            } else {
                // Standard product handling with pessimistic locking (select for update)
                Product product = productRepository.findByIdForUpdate(itemReq.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Standard product not found."));
                currentStock = product.getStock();
                price = product.getBasePrice();
            }

            /*
             * 5. Inventory Availability Check
             * Includes stock currently held by other users' active reservations.
             */
            int reserved = reservationRepository.sumReservedQuantityByProduct(itemReq.getProductId(), LocalDateTime.now());
            if (currentStock - reserved < itemReq.getQuantity()) {
                throw new ValidationException("Insufficient available stock for product: " + itemReq.getProductId());
            }

            /*
             * 6. Create Reservation (Holds stock for 35 seconds to allow Captcha completion)
             */
            InventoryReservation res = new InventoryReservation(
                    itemReq.getProductId(),
                    order.getId(),
                    itemReq.getQuantity(),
                    itemReq.isFlashSale(),
                    LocalDateTime.now().plusSeconds(35)
            );
            reservationRepository.save(res);

            /*
             * 7. Map to OrderItem and persist
             */
            OrderItem orderItem = new OrderItem(
                    order,
                    itemReq.getProductId(),
                    actualFlashSaleProductId,
                    itemReq.getQuantity(),
                    price
            );
            orderItemRepository.save(orderItem);
        }
    }
}