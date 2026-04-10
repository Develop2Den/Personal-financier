package com.D2D.personal_financier.service;

import com.D2D.personal_financier.config.security.SecurityUtils;
import com.D2D.personal_financier.dto.accountDTO.AccountRequestDto;
import com.D2D.personal_financier.dto.accountDTO.AccountResponseDto;
import com.D2D.personal_financier.entity.Account;
import com.D2D.personal_financier.entity.User;
import com.D2D.personal_financier.mapper.AccountMapper;
import com.D2D.personal_financier.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final SecurityUtils securityUtils;
    private final AccountMapper accountMapper;

    public AccountResponseDto createAccount(AccountRequestDto dto) {

        Account account = accountMapper.toEntity(dto);

        User user = securityUtils.getCurrentUser();

        account.setOwner(user);

        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }

        accountRepository.save(account);

        return accountMapper.toDto(account);
    }

    public List<AccountResponseDto> getAllAccounts() {

        User user = securityUtils.getCurrentUser();

        return accountRepository.findByOwnerId(user.getId())
                .stream()
                .map(accountMapper::toDto)
                .toList();
    }

    public AccountResponseDto getAccountById(Long id) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return accountMapper.toDto(account);
    }

    public void deleteAccount(Long id) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        accountRepository.delete(account);
    }
}

