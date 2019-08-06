package com.hodorgeek.mt.service.impl;

import com.hodorgeek.mt.dao.AccountDao;
import com.hodorgeek.mt.dto.TransferRequestPayload;
import com.hodorgeek.mt.entity.Account;
import com.hodorgeek.mt.exception.AccountNotFoundException;
import com.hodorgeek.mt.exception.InsufficientBalanceException;
import com.hodorgeek.mt.exception.InvalidAmountValueException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static com.hodorgeek.mt.DataLoader.AccountBuilder.anAccount;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InternalTransferServiceTest {

    private static final String ACCOUNT_NOT_FOUND_EXCEPTION_MESSAGE = "Account with id %d (%s) cannot be found";

    @Mock
    private AccountDao accountDao;

    @InjectMocks
    private InternalTransferService internalTransferService;

    @Test
    public void transfersMoneyBetweenTwoAccounts() {
        // given
        Account fromAccount = anAccount()
                .withId(0L)
                .withBalance(new BigDecimal("900.00"))
                .build();
        Account toAccount = anAccount()
                .withId(1L)
                .withBalance(new BigDecimal("600.00"))
                .build();

        TransferRequestPayload request = TransferRequestPayload.builder()
                .fromAccount(0L)
                .toAccount(1L)
                .amount(200.00f)
                .build();


        when(accountDao.getAccount(0L)).thenReturn(Optional.of(fromAccount));
        when(accountDao.getAccount(1L)).thenReturn(Optional.of(toAccount));

        // when
        internalTransferService.transfer(request);

        // then
        assertThat(fromAccount.getBalance()).isEqualTo(new BigDecimal("700.00"));
        assertThat(toAccount.getBalance()).isEqualTo(new BigDecimal("800.00"));
    }

    @Test
    public void throwsExceptionWhenFromAccountNotFound() {
        // given
        long nonExistingAccountId = -9999L;
        TransferRequestPayload request = TransferRequestPayload.builder()
                .fromAccount(nonExistingAccountId)
                .amount(1F)
                .build();
        final AccountNotFoundException expectedException = new AccountNotFoundException(String.format(ACCOUNT_NOT_FOUND_EXCEPTION_MESSAGE, nonExistingAccountId, "fromAccount"));

        // when
        final AccountNotFoundException accountNotFoundException = assertThrows(AccountNotFoundException.class, () -> {
            internalTransferService.transfer(request);
        });

        // then
        assertThat(accountNotFoundException.getMessage()).isEqualTo(expectedException.getMessage());
    }


    @Test
    public void throwsExceptionWhenToAccountNotFound() {
        // given
        long nonExistingAccountId = -9999L;
        when(accountDao.getAccount(0L)).thenReturn(Optional.of(anAccount().withId(0L).build()));
        TransferRequestPayload requestPayload = TransferRequestPayload.builder()
                .toAccount(nonExistingAccountId)
                .fromAccount(0L)
                .amount(1F)
                .build();

        final AccountNotFoundException expectedException = new AccountNotFoundException(String.format(ACCOUNT_NOT_FOUND_EXCEPTION_MESSAGE, nonExistingAccountId, "toAccount"));

        // when
        final AccountNotFoundException accountNotFoundException = assertThrows(AccountNotFoundException.class, () -> {
            internalTransferService.transfer(requestPayload);
        });

        // then
        assertThat(accountNotFoundException.getMessage()).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void throwsExceptionWhenAmountNotHigherThanZero() {
        // given
        TransferRequestPayload requestPayload = TransferRequestPayload.builder()
                .amount(0)
                .build();
        final InvalidAmountValueException expectedException = new InvalidAmountValueException("Amount value has to be greater than 0");

        // when
        final InvalidAmountValueException invalidAmountValueException = assertThrows(InvalidAmountValueException.class, () -> {
            internalTransferService.transfer(requestPayload);
        });

        // then:
        assertThat(invalidAmountValueException.getMessage()).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void throwsExceptionWhenInsufficientFunds() {
        // given
        TransferRequestPayload requestPayload = TransferRequestPayload.builder()
                .fromAccount(1L)
                .toAccount(0L)
                .amount(1000)
                .build();

        Account fromAccount = anAccount()
                .withId(0L)
                .withBalance(new BigDecimal("900.00"))
                .build();
        Account toAccount = anAccount()
                .withId(1L)
                .withBalance(new BigDecimal("600.00"))
                .build();

        when(accountDao.getAccount(0L)).thenReturn(Optional.of(fromAccount));
        when(accountDao.getAccount(1L)).thenReturn(Optional.of(toAccount));

        final InsufficientBalanceException expectedException = new InsufficientBalanceException(String.format("Insufficient balance to perform withdraw from account: %d", 1L));


        // when
        final InsufficientBalanceException insufficientBalanceException = assertThrows(InsufficientBalanceException.class, () -> {
            internalTransferService.transfer(requestPayload);
        });

        // then
        assertThat(insufficientBalanceException.getMessage()).isEqualTo(expectedException.getMessage());
    }
}
