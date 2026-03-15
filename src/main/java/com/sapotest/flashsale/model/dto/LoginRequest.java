package com.sapotest.flashsale.model.dto;

import jakarta.validation.constraints.*;

/**
 * Data Transfer Object for user login requests.
 * Contains validation constraints for email and password.
 */
public class LoginRequest {

    @NotBlank(message = "Please enter email.")
    @Email(message = "Please enter valid email.")
    private String email;

    @NotBlank(message = "Please enter password.")
    private String password;

    /**
     * Default constructor for JSON deserialization.
     */
    public LoginRequest() {
    }

    /**
     * Parameterized constructor for manual instantiation or testing.
     */
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}