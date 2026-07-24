package com.thejas.ca_billing_system.repository;

import com.thejas.ca_billing_system.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByUserUsernameOrderByDisplayOrderAsc(String username);
    List<Service> findByUserUsernameAndActiveTrueOrderByDisplayOrderAsc(String username);
    boolean existsByServiceNameIgnoreCaseAndUserUsername(String name, String username);
    Optional<Service> findByServiceNameIgnoreCaseAndUserUsername(String name, String username);
}
