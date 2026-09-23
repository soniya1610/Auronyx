package com.kabadiwala.repository;

import com.kabadiwala.entity.WasteCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WasteCategoryRepository extends JpaRepository<WasteCategory, Long> {
    List<WasteCategory> findByActiveTrue();
    Optional<WasteCategory> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
