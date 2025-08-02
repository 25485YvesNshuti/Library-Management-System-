package com.Library.repository;

import com.Library.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // FIX: Use User.Role enum for role parameter!
    long countByRole(User.Role role);
}