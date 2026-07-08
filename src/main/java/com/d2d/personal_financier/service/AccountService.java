package com.d2d.personal_financier.service;

import com.d2d.personal_financier.config.security.utils.HtmlSanitizerService;
import com.d2d.personal_financier.config.security.utils.SecurityUtils;
import com.d2d.personal_financier.dto.account_dto.AccountRequestDto;
import com.d2d.personal_financier.dto.account_dto.AccountResponseDto;
import com.d2d.personal_financier.entity.Account;
import com.d2d.personal_financier.entity.User;
import com.d2d.personal_financier.exception.AccountAlreadyExistsException;
import com.d2d.personal_financier.exception.AccountHasBalanceException;
import com.d2d.personal_financier.exception.AccountNotFoundException;
import com.d2d.personal_financier.mapper.AccountMapper;
import com.d2d.personal_financier.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final HtmlSanitizerService sanitizer;
    private final SecurityUtils securityUtils;
    private final AccountMapper accountMapper;

    public AccountResponseDto createAccount(AccountRequestDto dto) {

        User user = securityUtils.getCurrentUser();
        String sanitizedName = sanitizer.sanitize(dto.name());

        Account existingAccount = accountRepository.findByNameAndOwnerId(sanitizedName, user.getId())
            .orElse(null);

        if (existingAccount != null) {
            if (Boolean.TRUE.equals(existingAccount.getActive())) {
                throw new AccountAlreadyExistsException(sanitizedName);
            }

            existingAccount.setActive(true);
            accountRepository.save(existingAccount);

            return accountMapper.toDto(existingAccount);
        }

        Account account = accountMapper.toEntity(dto);
        account.setName(sanitizedName);
        account.setOwner(user);

        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }

        accountRepository.save(account);

        return accountMapper.toDto(account);
    }

    public Page<AccountResponseDto> getAllAccounts(Pageable pageable) {

        User user = securityUtils.getCurrentUser();

        return accountRepository.findByOwnerIdAndActiveTrue(user.getId(), pageable)
                .map(accountMapper::toDto);
    }

    public AccountResponseDto getAccountById(Long id) {

        User user = securityUtils.getCurrentUser();

        Account account = accountRepository.findByIdAndOwnerIdAndActiveTrue(id, user.getId())
                .orElseThrow(() -> new AccountNotFoundException(id));

        return accountMapper.toDto(account);
    }

    public void deleteAccount(Long id) {

        User user = securityUtils.getCurrentUser();

        Account account = accountRepository.findByIdAndOwnerIdAndActiveTrue(id, user.getId())
                .orElseThrow(() -> new AccountNotFoundException(id));

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new AccountHasBalanceException();
        }

        account.setActive(false);
        accountRepository.save(account);
    }
}

