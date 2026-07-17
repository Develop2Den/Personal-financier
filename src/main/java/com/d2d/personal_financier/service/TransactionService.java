package com.d2d.personal_financier.service;

import com.d2d.personal_financier.config.security.utils.HtmlSanitizerService;
import com.d2d.personal_financier.config.security.utils.SecurityUtils;
import com.d2d.personal_financier.dto.currency_dto.CurrencyConversionRequestDto;
import com.d2d.personal_financier.dto.currency_dto.CurrencyConversionResponseDto;
import com.d2d.personal_financier.dto.transaction_dto.TransactionRequestDto;
import com.d2d.personal_financier.dto.transaction_dto.TransactionResponseDto;
import com.d2d.personal_financier.dto.transaction_dto.TransferRequestDto;
import com.d2d.personal_financier.dto.transaction_dto.TransferResponseDto;
import com.d2d.personal_financier.entity.Account;
import com.d2d.personal_financier.entity.Category;
import com.d2d.personal_financier.entity.Transaction;
import com.d2d.personal_financier.entity.User;
import com.d2d.personal_financier.entity.enums.ExchangeRateSource;
import com.d2d.personal_financier.entity.enums.TransactionType;
import com.d2d.personal_financier.entity.enums.TransferDirection;
import com.d2d.personal_financier.exception.*;
import com.d2d.personal_financier.mapper.TransactionMapper;
import com.d2d.personal_financier.repository.AccountRepository;
import com.d2d.personal_financier.repository.CategoryRepository;
import com.d2d.personal_financier.repository.TransactionRepository;
import com.d2d.personal_financier.service.interfaces.CurrencyConversionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    private final CurrencyConversionService currencyConversionService;

    @Value("${app.currency.exchange-rate-source}")
    private ExchangeRateSource exchangeRateSource;

    private record TransferContext(

        String transferReference,

        String sanitizedDescription,

        LocalDateTime transferDate

    ) {
    }

    private record TransferTransactions(

        Transaction outgoingTransaction,

        Transaction incomingTransaction

    ) {
    }

    private record TransferAmounts(

        BigDecimal sourceAmount,

        BigDecimal targetAmount

    ) {
    }

    public TransactionResponseDto createTransaction(TransactionRequestDto dto) {

        if (dto.type() == TransactionType.TRANSFER) {
            throw new InvalidTransferException(
                "Transfers must be created using /api/transactions/transfer."
            );
        }

        User user = securityUtils.getCurrentUser();

        Account account = accountRepository.findByIdAndOwnerIdAndActiveTrue(dto.accountId(), user.getId())
            .orElseThrow(() -> new AccountNotFoundException(dto.accountId()));

        Category category = categoryRepository.findByIdAndOwnerIdAndActiveTrue(dto.categoryId(), user.getId())
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

        validateDifferentAccounts(dto);

        Account fromAccount = findActiveAccount(dto.fromAccountId(), user);
        Account toAccount = findActiveAccount(dto.toAccountId(), user);

        ensureBalanceAvailable(fromAccount, dto.amount());

        TransferAmounts transferAmounts = resolveTransferAmounts(
            dto,
            fromAccount,
            toAccount
        );

        TransferContext transferContext = buildTransferContext(dto);

        applyTransferBalanceChanges(
            fromAccount,
            toAccount,
            transferAmounts
        );

        TransferTransactions transferTransactions = buildTransferTransactions(
            transferAmounts,
            user,
            fromAccount,
            toAccount,
            transferContext
        );

        saveTransfer(
            fromAccount,
            toAccount,
            transferTransactions
        );

        return buildTransferResponse(
            dto,
            fromAccount,
            toAccount,
            transferContext,
            transferTransactions
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

    private void validateDifferentAccounts(TransferRequestDto dto) {
        if (dto.fromAccountId().equals(dto.toAccountId())) {
            throw new InvalidTransferException("Source and destination accounts must be different");
        }
    }

    private Account findActiveAccount(Long accountId, User user) {
        return accountRepository.findByIdAndOwnerIdAndActiveTrue(accountId, user.getId())
            .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    private LocalDateTime resolveTransferDate(TransferRequestDto dto) {
        return dto.date() == null ? LocalDateTime.now() : dto.date();
    }

    private TransferContext buildTransferContext(TransferRequestDto dto) {
        return new TransferContext(
            UUID.randomUUID().toString(),
            sanitizer.sanitize(dto.description()),
            resolveTransferDate(dto)
        );
    }

    private void applyTransferBalanceChanges(
        Account fromAccount,
        Account toAccount,
        TransferAmounts transferAmounts
    ) {
        fromAccount.setBalance(fromAccount.getBalance().subtract(transferAmounts.sourceAmount()));
        toAccount.setBalance(toAccount.getBalance().add(transferAmounts.targetAmount()));
    }

    private void saveTransfer(
        Account fromAccount,
        Account toAccount,
        TransferTransactions transferTransactions
    ) {
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        transactionRepository.save(transferTransactions.outgoingTransaction());
        transactionRepository.save(transferTransactions.incomingTransaction());
    }

    private TransferResponseDto buildTransferResponse(
        TransferRequestDto dto,
        Account fromAccount,
        Account toAccount,
        TransferContext transferContext,
        TransferTransactions transferTransactions
    ) {
        return new TransferResponseDto(
            transferContext.transferReference(),
            dto.amount(),
            fromAccount.getCurrency(),
            transferContext.sanitizedDescription(),
            transferContext.transferDate(),
            fromAccount.getId(),
            toAccount.getId(),
            transferTransactions.outgoingTransaction().getId(),
            transferTransactions.incomingTransaction().getId(),
            fromAccount.getBalance(),
            toAccount.getBalance()
        );
    }

    private TransferTransactions buildTransferTransactions(
        TransferAmounts transferAmounts,
        User user,
        Account fromAccount,
        Account toAccount,
        TransferContext transferContext
    ) {
        Transaction outgoingTransaction = buildTransferTransaction(
            transferAmounts.sourceAmount(),
            user,
            fromAccount,
            transferContext,
            TransferDirection.OUTGOING
        );

        Transaction incomingTransaction = buildTransferTransaction(
            transferAmounts.targetAmount(),
            user,
            toAccount,
            transferContext,
            TransferDirection.INCOMING
        );

        return new TransferTransactions(
            outgoingTransaction,
            incomingTransaction
        );
    }

    private TransferAmounts resolveTransferAmounts(
        TransferRequestDto dto,
        Account fromAccount,
        Account toAccount
    ) {
        if (fromAccount.getCurrency() == toAccount.getCurrency()) {
            return new TransferAmounts(
                dto.amount(),
                dto.amount()
            );
        }

        CurrencyConversionResponseDto conversion = currencyConversionService.convert(
            new CurrencyConversionRequestDto(
                dto.amount(),
                fromAccount.getCurrency(),
                toAccount.getCurrency(),
                exchangeRateSource
            )
        );

        return new TransferAmounts(
            dto.amount(),
            conversion.targetAmount()
        );
    }

    private Transaction buildTransferTransaction(
        BigDecimal amount,
        User user,
        Account account,
        TransferContext transferContext,
        TransferDirection transferDirection) {

        return Transaction.builder()
            .amount(amount)
            .description(transferContext.sanitizedDescription())
            .date(transferContext.transferDate())
            .type(TransactionType.TRANSFER)
            .owner(user)
            .account(account)
            .category(null)
            .transferReference(transferContext.transferReference())
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

    private void ensureBalanceAvailable(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(account.getId());
        }
    }
}
