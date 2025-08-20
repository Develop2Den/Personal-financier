package com.D2D.personal_financier.service;

import com.D2D.personal_financier.dto.accountDTO.AccountRequestDto;
import com.D2D.personal_financier.dto.accountDTO.AccountResponseDto;
import com.D2D.personal_financier.entity.Account;
import com.D2D.personal_financier.mapper.AccountMapper;
import com.D2D.personal_financier.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountResponseDto createAccount(AccountRequestDto dto) {
        Account account = accountMapper.toEntity(dto);
        accountRepository.save(account);
        return accountMapper.toDto(account);
    }

    public List<AccountResponseDto> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(accountMapper::toDto)
                .collect(Collectors.toList());
    }

    public AccountResponseDto getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return accountMapper.toDto(account);
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
}

