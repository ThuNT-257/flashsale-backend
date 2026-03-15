package com.sapotest.flashsale.model.dto;

import java.util.UUID;

/**
 * Data Transfer Object for login response details,
 * including the JWT token and basic user information.
 */
public class LoginResponse {
    private String token;
    private UUID userId;
    private String email;

    /**
     * Default constructor for JSON serialization.
     */
    public LoginResponse() {
    }

    /**
     * Constructor with all fields.
     * @param token  The generated JWT token.
     * @param userId Unique identifier of the user.
     * @param email  Registered email address.
     */
    public LoginResponse(String token, UUID userId, String email) {
        this.token = token;
        this.userId = userId;
        this.email = email;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}