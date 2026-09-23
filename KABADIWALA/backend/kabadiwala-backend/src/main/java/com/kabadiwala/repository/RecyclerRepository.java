package com.kabadiwala.repository;

import com.kabadiwala.entity.Recycler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecyclerRepository extends JpaRepository<Recycler, Long> {
    Optional<Recycler> findByUserId(Long userId);
    List<Recycler> findByActiveTrue();
    List<Recycler> findByCityIgnoreCase(String city);
}
