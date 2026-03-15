package com.sapotest.flashsale.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for user registration requests.
 * Includes validation for email format and password length.
 */
public class RegisterRequest {

    @NotBlank(message = "Please enter email.")
    @Email(message = "Please enter valid email.")
    private String email;

    @NotBlank(message = "Please enter password.")
    @Size(min = 6, message = "Password have minimum length of 6.")
    private String password;

    @NotBlank(message = "Please enter confirm password.")
    private String confirmPassword;

    @NotBlank(message = "Please enter name.")
    private String name;

    /**
     * Default constructor for JSON deserialization.
     */
    public RegisterRequest() {
    }

    /**
     * Parameterized constructor for testing purposes.
     */
    public RegisterRequest(String email, String password, String confirmPassword, String name) {
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.name = name;
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}