package com.kabadiwala.repository;

import com.kabadiwala.entity.EPRRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EPRRecordRepository extends JpaRepository<EPRRecord, Long> {
    List<EPRRecord> findByRecyclerId(Long recyclerId);
    Optional<EPRRecord> findByCertificateNo(String certificateNo);
    List<EPRRecord> findByVerified(Boolean verified);
}
