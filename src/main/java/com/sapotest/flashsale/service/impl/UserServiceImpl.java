package com.sapotest.flashsale.service.impl;

import com.sapotest.flashsale.model.dto.*;
import com.sapotest.flashsale.model.entity.User;
import com.sapotest.flashsale.repository.UserRepository;
import com.sapotest.flashsale.security.JwtTokenProvider;
import com.sapotest.flashsale.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service implementation for user-related operations.
 * Handles user registration and authentication using JWT.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Registers a new user into the system.
     * Encrypts the password using BCrypt before saving.
     * * @param request The registration data transfer object
     * @return true if registration is successful, false if email already exists
     */
    @Override
    public boolean register(RegisterRequest request) {
        // Check for duplicate email before proceeding
        if (userRepository.existsByEmail(request.getEmail())) {
            return false;
        }

        // Securely hash the plain-text password
        String passwordHashed = passwordEncoder.encode(request.getPassword());

        User user = new User(request.getEmail(), passwordHashed, request.getName());
        userRepository.save(user);

        return true;
    }

    /**
     * Authenticates a user and generates a JWT token.
     * * @param request The login credentials (email and password)
     * @return LoginResponse containing the access token and basic user info
     * @throws RuntimeException if authentication fails
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        /*
         * 1. Locate user by email
         */
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password."));

        /*
         * 2. Verify password hash matches the provided password
         */
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password.");
        }

        /*
         * 3. Generate a secure JWT for the authenticated session
         */
        String token = jwtTokenProvider.generateToken(user.getEmail());
        UUID userId = user.getId();
        String email = user.getEmail();

        return new LoginResponse(token, userId, email);
    }
}