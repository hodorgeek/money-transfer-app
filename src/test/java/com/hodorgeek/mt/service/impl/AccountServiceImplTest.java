package com.hodorgeek.mt.service.impl;

import com.google.common.collect.ImmutableList;
import com.hodorgeek.mt.dao.AccountDao;
import com.hodorgeek.mt.entity.Account;
import com.hodorgeek.mt.exception.AccountNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.hodorgeek.mt.DataLoader.AccountBuilder.anAccount;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountDao accountDao;

    @InjectMocks
    private AccountServiceImpl accountService;

    private static final String ACCOUNT_NOT_FOUND_EXCEPTION_MESSAGE = "Account with id: %s cannot be found";

    @Test
    public void returnsEmptyAccountListWhenNoAccountsForGivenCustomer() {
        // given
        UUID clientId = UUID.randomUUID();
        when(accountDao.getAccounts(clientId)).thenReturn(Collections.emptyList());

        // when
        List<Account> actual = accountService.getAccounts(clientId);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    public void returnsAllAccountsForGivenCustomer() {
        // given
        UUID clientId = UUID.randomUUID();
        Account a1 = anAccount()
                .withId(1L)
                .withBalance(BigDecimal.ZERO)
                .build();
        Account a2 = anAccount()
                .withId(2L)
                .withBalance(BigDecimal.TEN)
                .build();
        List<Account> accounts = ImmutableList.of(a1, a2);
        when(accountDao.getAccounts(clientId)).thenReturn(accounts);

        // when
        List<Account> actual = accountService.getAccounts(clientId);

        // then
        assertThat(actual).containsExactlyInAnyOrder(a1, a2);
    }

    @Test
    public void returnsSingleAccountForGivenCustomer() {
        // given
        UUID clientId = UUID.randomUUID();
        long accountId = 1L;
        Account a = anAccount()
                .withId(accountId)
                .withBalance(BigDecimal.TEN)
                .build();
        when(accountDao.getAccount(clientId, 1L)).thenReturn(Optional.of(a));

        // when
        Account actual = accountService.getAccount(clientId, accountId);

        // then
        assertThat(actual).isEqualTo(a);
    }

    @Test
    public void throwsExceptionWhenAccountNotFoundForGivenUser() {
        // given
        long nonExistingAccountId = 1L;
        UUID customerId = UUID.randomUUID();
        when(accountDao.getAccount(customerId, nonExistingAccountId)).thenReturn(Optional.empty());
        final AccountNotFoundException expectedException = new AccountNotFoundException(String.format(ACCOUNT_NOT_FOUND_EXCEPTION_MESSAGE, nonExistingAccountId));

        // when
        final AccountNotFoundException accountNotFoundException = assertThrows(AccountNotFoundException.class, () -> {
            accountService.getAccount(customerId, nonExistingAccountId);
        });

        // then
        assertThat(accountNotFoundException.getMessage()).isEqualTo(expectedException.getMessage());
    }
}
