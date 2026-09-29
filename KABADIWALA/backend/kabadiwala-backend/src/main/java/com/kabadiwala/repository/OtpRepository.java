package com.kabadiwala.repository;

import com.kabadiwala.entity.OtpPurpose;
import com.kabadiwala.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findTopByTargetAndPurposeOrderByCreatedAtDesc(String target, OtpPurpose purpose);
}
