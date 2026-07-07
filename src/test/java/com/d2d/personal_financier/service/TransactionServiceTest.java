package com.d2d.personal_financier.service;

import com.d2d.personal_financier.config.security.utils.HtmlSanitizerService;
import com.d2d.personal_financier.config.security.utils.SecurityUtils;
import com.d2d.personal_financier.dto.transaction_dto.TransactionRequestDto;
import com.d2d.personal_financier.dto.transaction_dto.TransferRequestDto;
import com.d2d.personal_financier.dto.transaction_dto.TransferResponseDto;
import com.d2d.personal_financier.entity.Account;
import com.d2d.personal_financier.entity.Category;
import com.d2d.personal_financier.entity.Transaction;
import com.d2d.personal_financier.entity.User;
import com.d2d.personal_financier.entity.enums.Currency;
import com.d2d.personal_financier.entity.enums.TransactionType;
import com.d2d.personal_financier.exception.InsufficientBalanceException;
import com.d2d.personal_financier.exception.InvalidTransferException;
import com.d2d.personal_financier.exception.AccountNotFoundException;
import com.d2d.personal_financier.exception.CategoryNotFoundException;
import com.d2d.personal_financier.mapper.TransactionMapper;
import com.d2d.personal_financier.repository.AccountRepository;
import com.d2d.personal_financier.repository.CategoryRepository;
import com.d2d.personal_financier.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private HtmlSanitizerService sanitizer;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void transferBetweenAccountsShouldMoveMoneyAndCreateTwoTransactions() {
        User user = User.builder().id(1L).build();
        Account fromAccount = buildAccount(10L, Currency.USD, "1000.00", user);
        Account toAccount = buildAccount(11L, Currency.USD, "100.00", user);
        LocalDateTime transferDate = LocalDateTime.of(2026, 4, 27, 11, 0);

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(sanitizer.sanitize("Card to cash")).thenReturn("Card to cash");
        when(accountRepository.findByIdAndOwnerIdAndActiveTrue(10L, 1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIdAndOwnerIdAndActiveTrue(11L, 1L)).thenReturn(Optional.of(toAccount));
        doAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);
            if (transaction.getId() == null) {
                transaction.setId(transaction.getTransferDirection().name().equals("OUTGOING") ? 101L : 102L);
            }
            return transaction;
        }).when(transactionRepository).save(any(Transaction.class));

        TransferResponseDto response = transactionService.transferBetweenAccounts(
                new TransferRequestDto(10L, 11L, new BigDecimal("150.00"), "Card to cash", transferDate)
        );

        assertEquals(new BigDecimal("850.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("250.00"), toAccount.getBalance());
        assertEquals(10L, response.fromAccountId());
        assertEquals(11L, response.toAccountId());
        assertEquals(101L, response.outgoingTransactionId());
        assertEquals(102L, response.incomingTransactionId());
        assertNotNull(response.transferReference());

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, org.mockito.Mockito.times(2)).save(transactionCaptor.capture());
        assertEquals(2, transactionCaptor.getAllValues().size());
        assertEquals("Card to cash", transactionCaptor.getAllValues().get(0).getDescription());
    }

    @Test
    void transferBetweenAccountsShouldRejectSameAccount() {
        TransferRequestDto request =
                new TransferRequestDto(10L, 10L, new BigDecimal("150.00"), "Same account", LocalDateTime.now());

        InvalidTransferException exception = assertThrows(
                InvalidTransferException.class,
                () -> transactionService.transferBetweenAccounts(request)
        );
        assertEquals("Source and destination accounts must be different", exception.getMessage());

        verify(accountRepository, never()).findByIdAndOwnerIdAndActiveTrue(any(), any());
    }

    @Test
    void transferBetweenAccountsShouldRejectInsufficientBalance() {
        User user = User.builder().id(1L).build();
        Account fromAccount = buildAccount(10L, Currency.USD, "50.00", user);
        Account toAccount = buildAccount(11L, Currency.USD, "100.00", user);

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(accountRepository.findByIdAndOwnerIdAndActiveTrue(10L, 1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIdAndOwnerIdAndActiveTrue(11L, 1L)).thenReturn(Optional.of(toAccount));

        TransferRequestDto request =
                new TransferRequestDto(10L, 11L, new BigDecimal("150.00"), "Card to cash", LocalDateTime.now());

        InsufficientBalanceException exception = assertThrows(
                InsufficientBalanceException.class,
                () -> transactionService.transferBetweenAccounts(request)
        );
        assertEquals("Insufficient balance for account id: 10", exception.getMessage());
    }

    @Test
    void createTransactionShouldRejectArchivedAccount() {
        User user = User.builder().id(1L).build();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(accountRepository.findByIdAndOwnerIdAndActiveTrue(10L, 1L)).thenReturn(Optional.empty());

        TransactionRequestDto request = new TransactionRequestDto(
            new BigDecimal("10.00"),
            TransactionType.EXPENSE,
            "Archived account",
            LocalDateTime.now(),
            10L,
            20L
        );

        assertThrows(AccountNotFoundException.class, () -> transactionService.createTransaction(request));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void createTransactionShouldRejectArchivedCategory() {
        User user = User.builder().id(1L).build();
        Account account = buildAccount(10L, Currency.USD, "100.00", user);

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(accountRepository.findByIdAndOwnerIdAndActiveTrue(10L, 1L)).thenReturn(Optional.of(account));
        when(categoryRepository.findByIdAndOwnerIdAndActiveTrue(20L, 1L)).thenReturn(Optional.empty());

        TransactionRequestDto request = new TransactionRequestDto(
            new BigDecimal("10.00"),
            TransactionType.EXPENSE,
            "Archived category",
            LocalDateTime.now(),
            10L,
            20L
        );

        assertThrows(CategoryNotFoundException.class, () -> transactionService.createTransaction(request));
        verify(transactionRepository, never()).save(any());
    }

    private Account buildAccount(Long id, Currency currency, String balance, User user) {
        Account account = new Account();
        account.setId(id);
        account.setCurrency(currency);
        account.setBalance(new BigDecimal(balance));
        account.setOwner(user);
        return account;
    }
}
