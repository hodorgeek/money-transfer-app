package com.hodorgeek.mt.dao;

import com.hodorgeek.mt.entity.Account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountDao {
    List<Account> getAccounts(UUID customerId);

    Optional<Account> getAccount(UUID customerId, Long accountId);

    Optional<Account> getAccount(Long accountId);

    void update(Account... accounts);
}
