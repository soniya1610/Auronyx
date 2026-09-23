package com.kabadiwala.repository;

import com.kabadiwala.entity.TraceabilityRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TraceabilityRecordRepository extends JpaRepository<TraceabilityRecord, Long> {

    Optional<TraceabilityRecord> findByQrCode(String qrCode);
    boolean existsByQrCode(String qrCode);

    @Query("SELECT t FROM TraceabilityRecord t WHERE t.transaction.pickup.id = :pickupId")
    Optional<TraceabilityRecord> findByPickupId(@Param("pickupId") Long pickupId);

    List<TraceabilityRecord> findByRecyclingRecordId(Long recyclingRecordId);
}
