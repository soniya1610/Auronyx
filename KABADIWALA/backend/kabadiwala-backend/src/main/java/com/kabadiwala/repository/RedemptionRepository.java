package com.kabadiwala.repository;

import com.kabadiwala.entity.Redemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RedemptionRepository extends JpaRepository<Redemption, Long> {
    List<Redemption> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Redemption> findByReferenceNo(String referenceNo);
    List<Redemption> findByStatus(Redemption.RedemptionStatus status);
}
