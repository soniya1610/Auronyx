package com.kabadiwala.repository;

import com.kabadiwala.entity.Pickup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PickupRepository extends JpaRepository<Pickup, Long> {
    List<Pickup> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Pickup> findByAssignedCollectorIdOrderByCreatedAtDesc(Long collectorId);
    List<Pickup> findByStatusOrderByCreatedAtDesc(Pickup.Status status);
    List<Pickup> findByStatusInAndAssignedCollectorIsNull(List<Pickup.Status> statuses);
    List<Pickup> findByCityIgnoreCaseAndStatusIn(String city, List<Pickup.Status> statuses);
    boolean existsByIdAndUserId(Long id, Long userId);
    boolean existsByIdAndAssignedCollectorId(Long id, Long collectorId);

    // Analytics queries
    long countByUserId(Long userId);
    long countByUserIdAndStatus(Long userId, Pickup.Status status);
    long countByAssignedCollectorId(Long collectorId);
    long countByStatus(Pickup.Status status);

    @Query("SELECT COUNT(p) FROM Pickup p WHERE p.user.id = :userId AND p.scheduledDate = :date")
    long countByUserIdAndScheduledDate(@Param("userId") Long userId, @Param("date") LocalDate date);
}
