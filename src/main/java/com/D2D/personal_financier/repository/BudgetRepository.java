package com.D2D.personal_financier.repository;

import com.D2D.personal_financier.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByOwnerId(Long ownerId);
    Optional<Budget> findByIdAndOwnerId(Long id, Long ownerId);
}
