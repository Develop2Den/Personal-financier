package com.d2d.personal_financier.service;

import com.d2d.personal_financier.config.security.utils.HtmlSanitizerService;
import com.d2d.personal_financier.config.security.utils.SecurityUtils;
import com.d2d.personal_financier.dto.account_dto.AccountRequestDto;
import com.d2d.personal_financier.dto.account_dto.AccountResponseDto;
import com.d2d.personal_financier.entity.Account;
import com.d2d.personal_financier.entity.User;
import com.d2d.personal_financier.exception.AccountNotFoundException;
import com.d2d.personal_financier.mapper.AccountMapper;
import com.d2d.personal_financier.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final HtmlSanitizerService sanitizer;
    private final SecurityUtils securityUtils;
    private final AccountMapper accountMapper;

    public AccountResponseDto createAccount(AccountRequestDto dto) {

        Account account = accountMapper.toEntity(dto);

        account.setName(
            sanitizer.sanitize(dto.name())
        );

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

        User user = securityUtils.getCurrentUser();

        Account account = accountRepository.findByIdAndOwnerId(id, user.getId())
                .orElseThrow(() -> new AccountNotFoundException(id));

        return accountMapper.toDto(account);
    }

    public void deleteAccount(Long id) {

        User user = securityUtils.getCurrentUser();

        Account account = accountRepository.findByIdAndOwnerId(id, user.getId())
                .orElseThrow(() -> new AccountNotFoundException(id));

        accountRepository.delete(account);
    }
}

