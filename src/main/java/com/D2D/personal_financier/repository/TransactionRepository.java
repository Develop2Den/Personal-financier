package com.D2D.personal_financier.repository;

import com.D2D.personal_financier.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByOwnerId(Long ownerId);
    Optional<Transaction> findByIdAndOwnerId(Long id, Long ownerId);
}

