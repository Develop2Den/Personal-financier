package com.D2D.personal_financier.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.D2D.personal_financier.entity.Account;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account,Long> {
    List<Account> findByOwnerId(Long ownerId);
}
