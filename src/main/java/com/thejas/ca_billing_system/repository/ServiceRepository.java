package com.thejas.ca_billing_system.repository;

import com.thejas.ca_billing_system.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    Optional<Service> findByServiceNameIgnoreCase(String serviceName);

    boolean existsByServiceNameIgnoreCase(String serviceName);

    List<Service> findByActiveTrueOrderByDisplayOrderAsc();
}