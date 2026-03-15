package com.sapotest.flashsale.service;

import java.util.UUID;

/**
 * Service interface for managing Captcha challenges.
 * Used to prevent automated bots from completing flash sale orders.
 */
public interface CaptchaService {

    /**
     * Generates a unique captcha code and associates it with a specific order.
     * * @param orderId The unique identifier of the pending order
     * @return A randomly generated captcha string (e.g., a 4-digit code)
     */
    String generateCaptcha(UUID orderId);

    /**
     * Validates the user-provided captcha against the stored value for an order.
     * * @param orderId   The unique identifier of the order being validated
     * @param inputCode The captcha code entered by the user
     * @return true if the code matches and is still valid, false otherwise
     */
    boolean validateCaptcha(UUID orderId, String inputCode);
}