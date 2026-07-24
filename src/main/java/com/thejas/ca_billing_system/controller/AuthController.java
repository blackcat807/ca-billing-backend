package com.thejas.ca_billing_system.controller;

import com.thejas.ca_billing_system.dto.AuthRequest;
import com.thejas.ca_billing_system.dto.AuthResponse;
import com.thejas.ca_billing_system.entity.User;
import com.thejas.ca_billing_system.repository.UserRepository;
import com.thejas.ca_billing_system.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository        userRepository;
    private final PasswordEncoder       passwordEncoder;
    private final JwtUtil               jwtUtil;
    private final AuthenticationManager authManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {

        if (request.getUsername() == null || request.getUsername().isBlank())
            return ResponseEntity.badRequest().body("Username is required.");

        if (request.getPassword() == null || request.getPassword().length() < 6)
            return ResponseEntity.badRequest().body("Password must be at least 6 characters.");

        if (userRepository.existsByUsername(request.getUsername()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken.");

        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered.");

        User user = User.builder()
                .username(request.getUsername().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail() != null && !request.getEmail().isBlank() ? request.getEmail().trim() : null)
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(token, user.getUsername()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(), request.getPassword()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password.");
        }

        String token = jwtUtil.generateToken(request.getUsername());
        return ResponseEntity.ok(new AuthResponse(token, request.getUsername()));
    }
}

