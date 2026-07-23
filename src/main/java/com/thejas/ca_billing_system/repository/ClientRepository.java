package com.thejas.ca_billing_system.repository;

import com.thejas.ca_billing_system.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByMobileNumber(String mobileNumber);

    Optional<Client> findByGstin(String gstin);

}