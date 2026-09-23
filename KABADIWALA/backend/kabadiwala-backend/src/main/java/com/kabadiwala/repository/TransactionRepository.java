package com.kabadiwala.repository;

import com.kabadiwala.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Transaction> findByCollectorIdOrderByCreatedAtDesc(Long collectorId);
    Optional<Transaction> findByPickupId(Long pickupId);
    Optional<Transaction> findByTransactionId(String transactionId);
    boolean existsByPickupId(Long pickupId);

    // Analytics aggregates
    @Query("SELECT COALESCE(SUM(t.finalAmount), 0) FROM Transaction t WHERE t.user.id = :userId")
    BigDecimal sumFinalAmountByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(t.actualWeight), 0) FROM Transaction t WHERE t.user.id = :userId")
    BigDecimal sumActualWeightByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(t.actualWeight), 0) FROM Transaction t WHERE t.collector.id = :collectorId")
    BigDecimal sumActualWeightByCollectorId(@Param("collectorId") Long collectorId);

    @Query("SELECT COALESCE(SUM(t.finalAmount), 0) FROM Transaction t")
    BigDecimal sumAllFinalAmounts();

    @Query("SELECT COALESCE(SUM(t.actualWeight), 0) FROM Transaction t")
    BigDecimal sumAllActualWeights();
}
