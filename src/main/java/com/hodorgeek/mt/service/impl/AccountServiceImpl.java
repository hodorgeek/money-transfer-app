package com.hodorgeek.mt.service.impl;

import com.hodorgeek.mt.dao.AccountDao;
import com.hodorgeek.mt.entity.Account;
import com.hodorgeek.mt.exception.AccountNotFoundException;
import com.hodorgeek.mt.service.AccountService;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

public class AccountServiceImpl implements AccountService {

    private final AccountDao accountDao;

    @Inject
    private AccountServiceImpl(AccountDao accountDao) {
        this.accountDao = accountDao;
    }


    @Override
    public List<Account> getAccounts(UUID customerId) {
        return accountDao.getAccounts(customerId);
    }

    @Override
    public Account getAccount(UUID customerId, Long accountId) {
        return accountDao.getAccount(customerId, accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        String.format("Account with id: %s cannot be found", accountId)));
    }
}
