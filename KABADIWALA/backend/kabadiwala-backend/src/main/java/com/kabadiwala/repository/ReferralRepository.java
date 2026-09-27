package com.kabadiwala.repository;

import com.kabadiwala.entity.Referral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long> {
    Optional<Referral> findByReferredId(Long referredUserId);
    List<Referral> findByReferrerId(Long referrerUserId);
    long countByReferrerId(Long referrerUserId);
    boolean existsByReferredId(Long referredUserId);
}
