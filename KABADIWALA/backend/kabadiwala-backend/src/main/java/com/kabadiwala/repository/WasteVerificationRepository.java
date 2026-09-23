package com.kabadiwala.repository;

import com.kabadiwala.entity.WasteVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WasteVerificationRepository extends JpaRepository<WasteVerification, Long> {
    Optional<WasteVerification> findByPickupId(Long pickupId);
    boolean existsByPickupId(Long pickupId);
}
