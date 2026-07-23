package com.thejas.ca_billing_system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;

    @Column(nullable = false, unique = true)
    private String serviceName;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Integer displayOrder = 0;
}