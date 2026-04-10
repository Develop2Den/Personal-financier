package com.D2D.personal_financier.repository;

import com.D2D.personal_financier.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal,Long> {
    List<Goal> findByOwnerId(Long ownerId);
}
