package com.kabadiwala.repository;

import com.kabadiwala.entity.WastePricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WastePricingRepository extends JpaRepository<WastePricing, Long> {
    Optional<WastePricing> findTopByCategoryIdAndActiveTrueOrderByEffectiveFromDesc(Long categoryId);
    List<WastePricing> findByActiveTrue();
    List<WastePricing> findByCategoryId(Long categoryId);
}
