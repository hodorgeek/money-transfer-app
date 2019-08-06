package com.hodorgeek.mt.service.impl;

import com.google.inject.Inject;
import com.hodorgeek.mt.dao.AccountDao;
import com.hodorgeek.mt.dto.TransferRequestPayload;
import com.hodorgeek.mt.entity.Account;
import com.hodorgeek.mt.exception.AccountNotFoundException;
import com.hodorgeek.mt.exception.InvalidAmountValueException;
import com.hodorgeek.mt.service.TransferService;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class InternalTransferService implements TransferService {

    private static final Random rnd = new Random();
    private static final long FIXED_DELAY = 1;
    private static final long RANDOM_DELAY = 2;
    private static final long TIMEOUT = TimeUnit.SECONDS.toNanos(2);

    private static final String ACCOUNT_NOT_FOUND_MESSAGE = "Account with id %d (%s) cannot be found";

    private final AccountDao accountDao;

    @Inject
    public InternalTransferService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public boolean transfer(TransferRequestPayload transferRequestPayload) {
        validateAmount(transferRequestPayload.getAmount());

        BigDecimal amount = BigDecimal.valueOf(transferRequestPayload.getAmount());

        long stopTime = System.nanoTime() + TIMEOUT;

        while (true) {
            Account fromAccount;
            Account toAccount;

            synchronized (this) {
                fromAccount = this.getAccount(transferRequestPayload.getFromAccount(), "fromAccount");
                toAccount = this.getAccount(transferRequestPayload.getToAccount(), "toAccount");
            }

            if (fromAccount.lock.tryLock()) {
                try {
                    if (toAccount.lock.tryLock()) {
                        try {
                            fromAccount.withdraw(amount);
                            toAccount.deposit(amount);

                            accountDao.update(fromAccount, toAccount);

                            return true;
                        } finally {
                            toAccount.lock.unlock();
                        }
                    }
                } finally {
                    fromAccount.lock.unlock();
                }
            }
            if (System.nanoTime() > stopTime) {
                return false;
            }
            try {
                TimeUnit.NANOSECONDS.sleep(FIXED_DELAY + rnd.nextLong() % RANDOM_DELAY);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }

    private void validateAmount(float amount) {
        if (amount <= 0) {
            throw new InvalidAmountValueException("Amount value has to be greater than 0");
        }
    }

    private Account getAccount(final Long accountNo, final String whichAccount) {
        final Optional<Account> accountOptional = accountDao.getAccount(accountNo);
        return accountOptional.orElseThrow(() -> new AccountNotFoundException(String.format(ACCOUNT_NOT_FOUND_MESSAGE, accountNo, whichAccount)));
    }
}
