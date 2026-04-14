package com.D2D.personal_financier.repository;

import com.D2D.personal_financier.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal,Long> {
    List<Goal> findByOwnerId(Long ownerId);
    Optional<Goal> findByIdAndOwnerId(Long id, Long ownerId);
}
