package com.D2D.personal_financier.repository;

import com.D2D.personal_financier.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal,Long> {
}
