package com.kabadiwala.repository;

import com.kabadiwala.entity.PointLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointLedgerRepository extends JpaRepository<PointLedger, Long> {
    List<PointLedger> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT COALESCE(SUM(p.points), 0) FROM PointLedger p WHERE p.user.id = :userId AND p.type = 'EARNED'")
    Integer sumEarnedPointsByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(p.points), 0) FROM PointLedger p WHERE p.user.id = :userId AND p.type = 'REDEEMED'")
    Integer sumRedeemedPointsByUserId(@Param("userId") Long userId);
}
