package com.kabadiwala.repository;

import com.kabadiwala.entity.FraudAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FraudAlertRepository extends JpaRepository<FraudAlert, Long> {
    List<FraudAlert> findByStatus(FraudAlert.AlertStatus status);
    List<FraudAlert> findAllByOrderByCreatedAtDesc();
    List<FraudAlert> findByUserId(Long userId);
    List<FraudAlert> findByPickupId(Long pickupId);
    long countByStatus(FraudAlert.AlertStatus status);
}
