package com.d2d.personal_financier.service;

import com.d2d.personal_financier.config.security.utils.HtmlSanitizerService;
import com.d2d.personal_financier.config.security.utils.SecurityUtils;
import com.d2d.personal_financier.dto.transaction_dto.TransactionRequestDto;
import com.d2d.personal_financier.dto.transaction_dto.TransactionResponseDto;
import com.d2d.personal_financier.dto.transaction_dto.TransferRequestDto;
import com.d2d.personal_financier.dto.transaction_dto.TransferResponseDto;
import com.d2d.personal_financier.entity.Account;
import com.d2d.personal_financier.entity.Category;
import com.d2d.personal_financier.entity.Transaction;
import com.d2d.personal_financier.entity.User;
import com.d2d.personal_financier.entity.enums.TransactionType;
import com.d2d.personal_financier.entity.enums.TransferDirection;
import com.d2d.personal_financier.exception.AccountNotFoundException;
import com.d2d.personal_financier.exception.CategoryNotFoundException;
import com.d2d.personal_financier.exception.InvalidTransferException;
import com.d2d.personal_financier.exception.InsufficientBalanceException;
import com.d2d.personal_financier.exception.TransactionNotFoundException;
import com.d2d.personal_financier.mapper.TransactionMapper;
import com.d2d.personal_financier.repository.AccountRepository;
import com.d2d.personal_financier.repository.CategoryRepository;
import com.d2d.personal_financier.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    public TransferResponseDto transferBetweenAccounts(TransferRequestDto dto) {

        User user = securityUtils.getCurrentUser();

        if (dto.fromAccountId().equals(dto.toAccountId())) {
            throw new InvalidTransferException("Source and destination accounts must be different");
        }

        Account fromAccount = accountRepository.findByIdAndOwnerId(dto.fromAccountId(), user.getId())
                .orElseThrow(() -> new AccountNotFoundException(dto.fromAccountId()));

        Account toAccount = accountRepository.findByIdAndOwnerId(dto.toAccountId(), user.getId())
                .orElseThrow(() -> new AccountNotFoundException(dto.toAccountId()));

        if (!fromAccount.getCurrency().equals(toAccount.getCurrency())) {
            throw new InvalidTransferException("Transfer between accounts with different currencies is not supported");
        }

        if (fromAccount.getBalance().compareTo(dto.amount()) < 0) {
            throw new InsufficientBalanceException(fromAccount.getId());
        }

        LocalDateTime transferDate = dto.date() == null ? LocalDateTime.now() : dto.date();
        String transferReference = UUID.randomUUID().toString();
        String sanitizedDescription = sanitizer.sanitize(dto.description());

        fromAccount.setBalance(fromAccount.getBalance().subtract(dto.amount()));
        toAccount.setBalance(toAccount.getBalance().add(dto.amount()));

        Transaction outgoingTransaction = buildTransferTransaction(
                dto.amount(),
                sanitizedDescription,
                transferDate,
                user,
                fromAccount,
                transferReference,
                TransferDirection.OUTGOING
        );

        Transaction incomingTransaction = buildTransferTransaction(
                dto.amount(),
                sanitizedDescription,
                transferDate,
                user,
                toAccount,
                transferReference,
                TransferDirection.INCOMING
        );

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        transactionRepository.save(outgoingTransaction);
        transactionRepository.save(incomingTransaction);

        return new TransferResponseDto(
                transferReference,
                dto.amount(),
                fromAccount.getCurrency(),
                sanitizedDescription,
                transferDate,
                fromAccount.getId(),
                toAccount.getId(),
                outgoingTransaction.getId(),
                incomingTransaction.getId(),
                fromAccount.getBalance(),
                toAccount.getBalance()
        );
    }

    public Page<TransactionResponseDto> getAllTransactions(Pageable pageable) {
        User user = securityUtils.getCurrentUser();

        return transactionRepository.findByOwnerId(user.getId(), pageable)
                .map(transactionMapper::toDto);
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

        if (transaction.getType() == TransactionType.TRANSFER && transaction.getTransferReference() != null) {
            deleteTransferTransactions(transaction.getTransferReference(), user.getId());
            return;
        }

        rollbackTransactionImpact(transaction);
        accountRepository.save(transaction.getAccount());
        transactionRepository.delete(transaction);
    }

    private Transaction buildTransferTransaction(
            java.math.BigDecimal amount,
            String description,
            LocalDateTime transferDate,
            User user,
            Account account,
            String transferReference,
            TransferDirection transferDirection) {

        return Transaction.builder()
                .amount(amount)
                .description(description)
                .date(transferDate)
                .type(TransactionType.TRANSFER)
                .owner(user)
                .account(account)
                .category(null)
                .transferReference(transferReference)
                .transferDirection(transferDirection)
                .build();
    }

    private void deleteTransferTransactions(String transferReference, Long userId) {
        List<Transaction> transferTransactions =
                transactionRepository.findByTransferReferenceAndOwnerId(transferReference, userId);

        transferTransactions.forEach(this::rollbackTransactionImpact);
        transferTransactions.stream()
                .map(Transaction::getAccount)
                .distinct()
                .forEach(accountRepository::save);
        transactionRepository.deleteAll(transferTransactions);
    }

    private void rollbackTransactionImpact(Transaction transaction) {
        Account account = transaction.getAccount();

        switch (transaction.getType()) {
            case EXPENSE -> account.setBalance(account.getBalance().add(transaction.getAmount()));
            case INCOME -> {
                ensureBalanceAvailable(account, transaction.getAmount());
                account.setBalance(account.getBalance().subtract(transaction.getAmount()));
            }
            case TRANSFER -> rollbackTransferImpact(transaction, account);
        }
    }

    private void rollbackTransferImpact(Transaction transaction, Account account) {
        if (transaction.getTransferDirection() == TransferDirection.OUTGOING) {
            account.setBalance(account.getBalance().add(transaction.getAmount()));
            return;
        }

        if (transaction.getTransferDirection() == TransferDirection.INCOMING) {
            ensureBalanceAvailable(account, transaction.getAmount());
            account.setBalance(account.getBalance().subtract(transaction.getAmount()));
            return;
        }

        throw new InvalidTransferException("Transfer transaction is missing direction metadata");
    }

    private void ensureBalanceAvailable(Account account, java.math.BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(account.getId());
        }
    }
}


