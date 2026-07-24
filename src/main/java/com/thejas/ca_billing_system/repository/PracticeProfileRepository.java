package com.thejas.ca_billing_system.repository;

import com.thejas.ca_billing_system.entity.PracticeProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PracticeProfileRepository extends JpaRepository<PracticeProfile, Long> {
    Optional<PracticeProfile> findByUserId(Long userId);
}