package com.D2D.personal_financier.service;

import com.D2D.personal_financier.config.security.SecurityUtils;
import com.D2D.personal_financier.dto.transactionDTO.TransactionRequestDto;
import com.D2D.personal_financier.dto.transactionDTO.TransactionResponseDto;
import com.D2D.personal_financier.entity.Account;
import com.D2D.personal_financier.entity.Category;
import com.D2D.personal_financier.entity.Transaction;
import com.D2D.personal_financier.entity.User;
import com.D2D.personal_financier.entity.enums.TransactionType;
import com.D2D.personal_financier.mapper.TransactionMapper;
import com.D2D.personal_financier.repository.AccountRepository;
import com.D2D.personal_financier.repository.CategoryRepository;
import com.D2D.personal_financier.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final SecurityUtils securityUtils;

    public TransactionResponseDto createTransaction(TransactionRequestDto dto) {

        Account account = accountRepository.findById(dto.accountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Transaction transaction = transactionMapper.toEntity(dto);

        User user = securityUtils.getCurrentUser();

        transaction.setOwner(user);

        transaction.setAccount(account);
        transaction.setCategory(category);

        if (transaction.getDate() == null) {
            transaction.setDate(LocalDateTime.now());
        }

        if (transaction.getType() == TransactionType.EXPENSE) {

            if (account.getBalance().compareTo(transaction.getAmount()) < 0) {
                throw new RuntimeException("Insufficient balance");
            }

            account.setBalance(
                    account.getBalance().subtract(transaction.getAmount())
            );
        }

        if (transaction.getType() == TransactionType.INCOME) {

            account.setBalance(
                    account.getBalance().add(transaction.getAmount())
            );
        }

        accountRepository.save(account);

        transactionRepository.save(transaction);

        return transactionMapper.toDto(transaction);
    }

    public List<TransactionResponseDto> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    public TransactionResponseDto getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        return transactionMapper.toDto(transaction);
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }
}


