package com.d2d.personal_financier.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.d2d.personal_financier.entity.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long> {
    List<Account> findByOwnerId(Long ownerId);
    Page<Account> findByOwnerId(Long ownerId, Pageable pageable);
    Optional<Account> findByIdAndOwnerId(Long id, Long ownerId);
}
