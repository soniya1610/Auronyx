package com.kabadiwala.repository;

import com.kabadiwala.entity.PickupStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PickupStatusRepository extends JpaRepository<PickupStatusHistory, Long> {
    List<PickupStatusHistory> findByPickupIdOrderByCreatedAtAsc(Long pickupId);
}
