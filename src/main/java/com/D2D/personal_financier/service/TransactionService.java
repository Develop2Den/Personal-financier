package com.D2D.personal_financier.service;

import com.D2D.personal_financier.config.security.utils.HtmlSanitizerService;
import com.D2D.personal_financier.config.security.utils.SecurityUtils;
import com.D2D.personal_financier.dto.transactionDTO.TransactionRequestDto;
import com.D2D.personal_financier.dto.transactionDTO.TransactionResponseDto;
import com.D2D.personal_financier.entity.Account;
import com.D2D.personal_financier.entity.Category;
import com.D2D.personal_financier.entity.Transaction;
import com.D2D.personal_financier.entity.User;
import com.D2D.personal_financier.entity.enums.TransactionType;
import com.D2D.personal_financier.exception.AccountNotFoundException;
import com.D2D.personal_financier.exception.CategoryNotFoundException;
import com.D2D.personal_financier.exception.InsufficientBalanceException;
import com.D2D.personal_financier.exception.TransactionNotFoundException;
import com.D2D.personal_financier.mapper.TransactionMapper;
import com.D2D.personal_financier.repository.AccountRepository;
import com.D2D.personal_financier.repository.CategoryRepository;
import com.D2D.personal_financier.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final HtmlSanitizerService sanitizer;
    private final TransactionMapper transactionMapper;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final SecurityUtils securityUtils;

    public TransactionResponseDto createTransaction(TransactionRequestDto dto) {

        User user = securityUtils.getCurrentUser();

        Account account = accountRepository.findByIdAndOwnerId(dto.accountId(), user.getId())
                .orElseThrow(() -> new AccountNotFoundException(dto.accountId()));

        Category category = categoryRepository.findByIdAndOwnerId(dto.categoryId(), user.getId())
                .orElseThrow(() -> new CategoryNotFoundException(dto.categoryId()));

        Transaction transaction = transactionMapper.toEntity(dto);

        transaction.setDescription(
            sanitizer.sanitize(dto.description())
        );

        transaction.setOwner(user);

        transaction.setAccount(account);
        transaction.setCategory(category);

        if (transaction.getDate() == null) {
            transaction.setDate(LocalDateTime.now());
        }

        if (transaction.getType() == TransactionType.EXPENSE) {

            if (account.getBalance().compareTo(transaction.getAmount()) < 0) {
                throw new InsufficientBalanceException(account.getId());
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
        User user = securityUtils.getCurrentUser();

        return transactionRepository.findByOwnerId(user.getId()).stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    public TransactionResponseDto getTransactionById(Long id) {
        User user = securityUtils.getCurrentUser();

        Transaction transaction = transactionRepository.findByIdAndOwnerId(id, user.getId())
                .orElseThrow(() -> new TransactionNotFoundException(id));

        return transactionMapper.toDto(transaction);
    }

    public void deleteTransaction(Long id) {
        User user = securityUtils.getCurrentUser();

        Transaction transaction = transactionRepository.findByIdAndOwnerId(id, user.getId())
                .orElseThrow(() -> new TransactionNotFoundException(id));

        transactionRepository.delete(transaction);
    }
}


