package com.hodorgeek.mt.service;

import com.hodorgeek.mt.entity.Account;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    List<Account> getAccounts(UUID clientId);

    Account getAccount(UUID clientId, Long accountId);

}
