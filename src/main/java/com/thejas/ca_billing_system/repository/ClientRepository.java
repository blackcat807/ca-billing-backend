package com.thejas.ca_billing_system.repository;

import com.thejas.ca_billing_system.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByUserUsername(String username);
    Optional<Client> findByMobileNumberAndUserUsername(String mobileNumber, String username);
    Optional<Client> findByGstinAndUserUsername(String gstin, String username);
}
