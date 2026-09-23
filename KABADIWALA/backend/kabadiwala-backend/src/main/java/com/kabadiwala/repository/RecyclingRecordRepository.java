package com.kabadiwala.repository;

import com.kabadiwala.entity.RecyclingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecyclingRecordRepository extends JpaRepository<RecyclingRecord, Long> {
    Optional<RecyclingRecord> findByPickupId(Long pickupId);
    List<RecyclingRecord> findByRecyclerId(Long recyclerId);
    boolean existsByPickupId(Long pickupId);
}
