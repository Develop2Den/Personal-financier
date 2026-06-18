package com.d2d.personal_financier.service;

import com.d2d.personal_financier.config.security.utils.HtmlSanitizerService;
import com.d2d.personal_financier.config.security.utils.SecurityUtils;
import com.d2d.personal_financier.dto.account_dto.AccountRequestDto;
import com.d2d.personal_financier.dto.account_dto.AccountResponseDto;
import com.d2d.personal_financier.entity.Account;
import com.d2d.personal_financier.entity.User;
import com.d2d.personal_financier.entity.enums.AccountType;
import com.d2d.personal_financier.exception.AccountAlreadyExistsException;
import com.d2d.personal_financier.mapper.AccountMapper;
import com.d2d.personal_financier.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private HtmlSanitizerService sanitizer;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;

    @Test
    void deleteAccountShouldArchiveWithoutPhysicalDelete() {
        User user = User.builder().id(1L).build();
        Account account = new Account();
        account.setId(10L);
        account.setOwner(user);
        account.setActive(true);

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(accountRepository.findByIdAndOwnerIdAndActiveTrue(10L, 1L)).thenReturn(Optional.of(account));

        accountService.deleteAccount(10L);

        assertEquals(false, account.getActive());
        verify(accountRepository).save(account);
        verify(accountRepository, never()).delete(any());
    }

    @Test
    void createAccountShouldRestoreArchivedAccount() {
        User user = User.builder().id(1L).build();
        Account archivedAccount = new Account();
        archivedAccount.setId(10L);
        archivedAccount.setName("Main Card");
        archivedAccount.setOwner(user);
        archivedAccount.setActive(false);

        AccountRequestDto request =
            new AccountRequestDto("Main Card", "USD", new BigDecimal("100.00"), AccountType.CARD);
        AccountResponseDto expected =
            new AccountResponseDto(10L, "Main Card", "USD", new BigDecimal("100.00"), AccountType.CARD, true);

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(sanitizer.sanitize("Main Card")).thenReturn("Main Card");
        when(accountRepository.findByNameAndOwnerId("Main Card", 1L)).thenReturn(Optional.of(archivedAccount));
        when(accountMapper.toDto(archivedAccount)).thenReturn(expected);

        AccountResponseDto response = accountService.createAccount(request);

        assertEquals(true, archivedAccount.getActive());
        assertSame(expected, response);
        verify(accountRepository).save(archivedAccount);
    }

    @Test
    void createAccountShouldNotCreateNewEntityWhenRestoringArchivedAccount() {
        User user = User.builder().id(1L).build();
        Account archivedAccount = new Account();
        archivedAccount.setId(10L);
        archivedAccount.setName("Main Card");
        archivedAccount.setOwner(user);
        archivedAccount.setActive(false);

        AccountRequestDto request =
            new AccountRequestDto("Main Card", "USD", new BigDecimal("100.00"), AccountType.CARD);

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(sanitizer.sanitize("Main Card")).thenReturn("Main Card");
        when(accountRepository.findByNameAndOwnerId("Main Card", 1L)).thenReturn(Optional.of(archivedAccount));

        accountService.createAccount(request);

        verify(accountMapper, never()).toEntity(any(AccountRequestDto.class));
        verify(accountRepository).save(archivedAccount);
    }

    @Test
    void createAccountShouldRejectExistingActiveAccount() {
        User user = User.builder().id(1L).build();
        Account activeAccount = new Account();
        activeAccount.setId(10L);
        activeAccount.setName("Main Card");
        activeAccount.setOwner(user);
        activeAccount.setActive(true);

        AccountRequestDto request =
            new AccountRequestDto("Main Card", "USD", new BigDecimal("100.00"), AccountType.CARD);

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(sanitizer.sanitize("Main Card")).thenReturn("Main Card");
        when(accountRepository.findByNameAndOwnerId("Main Card", 1L)).thenReturn(Optional.of(activeAccount));

        assertThrows(AccountAlreadyExistsException.class, () -> accountService.createAccount(request));
        verify(accountRepository, never()).save(any());
        verify(accountMapper, never()).toEntity(any(AccountRequestDto.class));
    }
}
