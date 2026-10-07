package com.thejas.ca_billing_system.controller;

import com.thejas.ca_billing_system.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final UserRepository userRepository;

    public HealthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/api/health")
    public ResponseEntity<String> health() {
        long count = userRepository.count();
        return ResponseEntity.ok("OK - " + count + " users");
    }
}