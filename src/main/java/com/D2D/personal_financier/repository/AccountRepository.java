package com.D2D.personal_financier.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.D2D.personal_financier.entity.Account;

public interface AccountRepository extends JpaRepository<Account,Long> {

}
