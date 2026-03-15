package com.sapotest.flashsale.repository;

import com.sapotest.flashsale.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing User entities.
 * Provides essential methods for authentication and registration checks.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Finds a user by their unique email address.
     * Used primarily during the login process.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if an email is already registered in the system.
     * Used during registration to prevent duplicate accounts.
     */
    boolean existsByEmail(String email);

}