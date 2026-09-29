package com.kabadiwala.repository;

import com.kabadiwala.entity.UserReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRewardRepository extends JpaRepository<UserReward, Long> {
    List<UserReward> findByUserIdOrderByRedeemedAtDesc(Long userId);
}
