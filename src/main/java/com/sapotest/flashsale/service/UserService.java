package com.sapotest.flashsale.service;

import com.sapotest.flashsale.model.dto.LoginRequest;
import com.sapotest.flashsale.model.dto.LoginResponse;
import com.sapotest.flashsale.model.dto.RegisterRequest;

/**
 * Service interface for handling user-related operations.
 * Focuses on secure identity management, including registration and authentication.
 */
public interface UserService {

    /**
     * Registers a new user account in the system.
     * Implementation should handle password hashing and check for existing emails.
     * * @param request Data transfer object containing registration details
     * @return true if account creation is successful, false if email already exists
     */
    boolean register(RegisterRequest request);

    /**
     * Authenticates a user based on credentials and issues an access token.
     * * @param request Data transfer object containing login credentials
     * @return LoginResponse containing the JWT token and user profile information
     * @throws RuntimeException if authentication fails (invalid email or password)
     */
    LoginResponse login(LoginRequest request);
}