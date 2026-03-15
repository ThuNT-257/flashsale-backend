package com.sapotest.flashsale.controller;

import com.sapotest.flashsale.model.dto.OrderRequest;
import com.sapotest.flashsale.service.CaptchaService;
import com.sapotest.flashsale.service.OrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Controller for handling order-related operations, including placement and confirmation.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final CaptchaService captchaService;

    @Value("${app.test.secret:SECRET_SMART_TEST}")
    private String testSecret;

    public OrderController(OrderService orderService, CaptchaService captchaService) {
        this.orderService = orderService;
        this.captchaService = captchaService;
    }

    /**
     * Initiate a new order placement.
     * Supports a "Smart Test" mode via a secret header for automated testing.
     */
    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(
            @RequestBody OrderRequest request,
            @RequestHeader(value = "X-Test-Key", required = false) String testKey
    ) {
        try {
            // Bypass captcha if the correct test secret is provided in the header
            if (testSecret.equals(testKey)) {
                UUID orderId = orderService.createOrder(request);
                orderService.confirmOrder(orderId);

                return ResponseEntity.status(201).body(Map.of(
                        "status", 201,
                        "orderId", orderId,
                        "message", "SMART TEST: Order confirmed immediately"
                ));
            }

            // Normal flow: Create order and generate Captcha/OTP
            UUID orderId = orderService.createOrder(request);
            String code = captchaService.generateCaptcha(orderId);

            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "orderId", orderId,
                    "captchaCode", code,
                    "message", "Order initiated successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Confirm an order by validating the provided OTP/Captcha.
     */
    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<?> confirmOrder(@PathVariable UUID orderId, @RequestBody Map<String, String> body) {
        String code = body.get("code");

        if (!captchaService.validateCaptcha(orderId, code)) {
            return ResponseEntity.status(400).body(Map.of("message", "OTP is invalid or expired!"));
        }

        orderService.confirmOrder(orderId);
        return ResponseEntity.ok(Map.of("message", "Order confirmed!"));
    }

    /**
     * Cancel an order (typically used for cleanup or timeout scenarios).
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable UUID orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(Map.of("message", "Order cancelled"));
    }
}