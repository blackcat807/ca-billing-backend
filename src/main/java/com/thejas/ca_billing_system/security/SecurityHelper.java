package com.thejas.ca_billing_system.security;

import com.thejas.ca_billing_system.entity.User;
import com.thejas.ca_billing_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Convenience bean that returns the currently-authenticated User entity.
 * Used by all service impls so they never touch another user's data.
 */
@Component
@RequiredArgsConstructor
public class SecurityHelper {

    private final UserRepository userRepository;

    public String currentUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication().getName();
    }

    public User currentUser() {
        return userRepository.findByUsername(currentUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in DB"));
    }
}
