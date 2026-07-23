package com.thejas.ca_billing_system.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceResponse {

    private Long serviceId;

    private String serviceName;

    private Boolean active;

    private Integer displayOrder;
}