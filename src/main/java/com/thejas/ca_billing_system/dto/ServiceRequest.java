package com.thejas.ca_billing_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequest {

    @NotBlank(message = "Service name is required")
    private String serviceName;

    private Boolean active = true;

    private Integer displayOrder = 0;
}