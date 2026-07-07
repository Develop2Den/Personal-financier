package com.d2d.personal_financier.repository;

import com.d2d.personal_financier.entity.Transaction;
import com.d2d.personal_financier.entity.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByOwnerId(Long ownerId);

    @EntityGraph(attributePaths = {"account", "category"})
    Page<Transaction> findByOwnerId(Long ownerId, Pageable pageable);

    Optional<Transaction> findByIdAndOwnerId(Long id, Long ownerId);

    List<Transaction> findByTransferReferenceAndOwnerId(
        String transferReference,
        Long ownerId
    );

    List<Transaction> findByOwnerIdAndType(Long ownerId, TransactionType type);
}
