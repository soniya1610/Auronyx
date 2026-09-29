package com.kabadiwala.repository;

import com.kabadiwala.entity.Pickup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PickupRepository extends JpaRepository<Pickup, Long> {
    List<Pickup> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Pickup> findByCollectorIdOrderByCreatedAtDesc(Long collectorId);
    List<Pickup> findByStatusOrderByCreatedAtDesc(String status);
    List<Pickup> findByStatusInOrderByCreatedAtDesc(List<String> statuses);
}
