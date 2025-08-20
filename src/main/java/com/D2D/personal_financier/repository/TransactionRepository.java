package com.D2D.personal_financier.repository;

import com.D2D.personal_financier.entity.Transaction;
import com.D2D.personal_financier.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByOwner(User owner);
}

