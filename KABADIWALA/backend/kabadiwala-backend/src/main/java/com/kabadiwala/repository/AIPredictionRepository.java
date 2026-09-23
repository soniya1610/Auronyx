package com.kabadiwala.repository;

import com.kabadiwala.entity.AIPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIPredictionRepository extends JpaRepository<AIPrediction, Long> {
    List<AIPrediction> findByUserIdOrderByCreatedAtDesc(Long userId);
}
