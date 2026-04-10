package com.D2D.personal_financier.repository;

import com.D2D.personal_financier.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByOwnerId(Long ownerId);
}
