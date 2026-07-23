package com.thejas.ca_billing_system.controller;

import com.thejas.ca_billing_system.dto.ServiceRequest;
import com.thejas.ca_billing_system.dto.ServiceResponse;
import com.thejas.ca_billing_system.service.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @PostMapping
    public ResponseEntity<ServiceResponse> createService(
            @Valid @RequestBody ServiceRequest request) {

        return new ResponseEntity<>(
                serviceService.createService(request),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<ServiceResponse>> getAllServices() {
        return ResponseEntity.ok(serviceService.getAllServices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> getServiceById(
            @PathVariable Long id) {

        return ResponseEntity.ok(serviceService.getServiceById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateService(
            @PathVariable Long id,
            @Valid @RequestBody ServiceRequest request) {

        return ResponseEntity.ok(
                serviceService.updateService(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteService(
            @PathVariable Long id) {

        serviceService.deleteService(id);

        return ResponseEntity.ok("Service deleted successfully.");
    }
}