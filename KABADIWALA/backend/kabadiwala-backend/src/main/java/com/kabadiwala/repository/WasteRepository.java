package com.kabadiwala.repository;

import com.kabadiwala.entity.Waste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WasteRepository extends JpaRepository<Waste, Long> {
    List<Waste> findByActiveTrue();
    List<Waste> findByCategoryIdAndActiveTrue(Long categoryId);
    Optional<Waste> findByNameIgnoreCase(String name);
}
