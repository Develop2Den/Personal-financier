package com.D2D.personal_financier.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.D2D.personal_financier.entity.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long> {
    List<Account> findByOwnerId(Long ownerId);
    Optional<Account> findByIdAndOwnerId(Long id, Long ownerId);
}
