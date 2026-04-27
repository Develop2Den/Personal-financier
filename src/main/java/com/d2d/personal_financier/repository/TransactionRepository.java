package com.d2d.personal_financier.repository;

import com.d2d.personal_financier.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByOwnerId(Long ownerId);
    Page<Transaction> findByOwnerId(Long ownerId, Pageable pageable);
    Optional<Transaction> findByIdAndOwnerId(Long id, Long ownerId);
    List<Transaction> findByTransferReferenceAndOwnerId(String transferReference, Long ownerId);
}
