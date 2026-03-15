package com.sapotest.flashsale.controller;

import com.sapotest.flashsale.model.dto.*;
import com.sapotest.flashsale.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user authentication and account management.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    /**
     * Register a new user account.
     * * @param request Registration details including email and password.
     * @return ApiResponse with status and message.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {

        // Validate password confirmation
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(400, "Passwords do not match.", null));
        }

        boolean success = userService.register(request);

        if (!success) {
            // 409 Conflict for existing resources like email
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(409, "Email address already exists.", null));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, "Account created successfully!", null));
    }

    /**
     * Authenticate a user and return a JWT token.
     * * @param request Login credentials.
     * @return ApiResponse containing the login response with token.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse result = userService.login(request);
            return ResponseEntity.ok(new ApiResponse<>(200, "Login successful", result));
        } catch (RuntimeException e) {
            // 401 Unauthorized for failed login attempts
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(401, e.getMessage(), null));
        }
    }
}