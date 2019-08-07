package com.hodorgeek.mt.service.impl;

import com.google.common.collect.Lists;
import com.hodorgeek.mt.dao.GenericDaoIntegrationTest;
import com.hodorgeek.mt.dao.impl.AccountDaoImpl;
import com.hodorgeek.mt.dto.TransferRequestPayload;
import com.hodorgeek.mt.entity.Account;
import com.hodorgeek.mt.entity.Customer;
import com.hodorgeek.mt.service.TransferService;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import static com.hodorgeek.mt.app.DataLoader.AccountBuilder.anAccount;
import static com.hodorgeek.mt.app.DataLoader.CustomerBuilder.aCustomer;
import static org.assertj.core.api.Assertions.assertThat;

public class InternalTransferServiceConcurrencyTest extends GenericDaoIntegrationTest {

    private static final int NUM_OF_THREADS = 1000;

    private AccountDaoImpl accountDao;

    private TransferService underTest;

    @BeforeAll
    public static void init() {
        GenericDaoIntegrationTest.init();
    }

    @AfterAll
    public static void tearDown() {
        GenericDaoIntegrationTest.tearDown();
    }

    @BeforeEach
    public void setUp() {
        accountDao = new AccountDaoImpl(GenericDaoIntegrationTest::getEntityManager);
        underTest = new InternalTransferService(accountDao);
    }

    @AfterEach
    public void clearDB() {
        cleanDB();
    }

    @Test
    public void ensuresThreadSafetyForConcurrentTransfers() throws InterruptedException {
        Account fromAccount = anAccount()
                .withBalance(BigDecimal.valueOf(1000000.55))
                .build();
        Account toAccount = anAccount()
                .withBalance(BigDecimal.valueOf(500000.55))
                .build();
        Customer customer = aCustomer()
                .withFirstName("Sham")
                .withLastName("Bhand")
                .withAccounts(fromAccount, toAccount)
                .build();
        storeCustomers(Lists.newArrayList(customer));

        Collection<Callable<Boolean>> tasks = new ArrayList<>(NUM_OF_THREADS);

        for (int i = 0; i < NUM_OF_THREADS; i++) {
            tasks.add(() -> underTest.transfer(createTransferRequest(fromAccount, toAccount, 1.00F)));
            tasks.add(() -> underTest.transfer(createTransferRequest(toAccount, fromAccount, 2.00F)));
        }

        Executors.newFixedThreadPool(4).invokeAll(tasks);

        assertThat(fromAccount.getBalance()).isEqualTo(BigDecimal.valueOf(1001000.55));
        assertThat(toAccount.getBalance()).isEqualTo(BigDecimal.valueOf(499000.55));
        assertThat(accountDao.getAccount(fromAccount.getId()).get().getBalance()).isEqualTo(BigDecimal.valueOf(1001000.55));
        assertThat(accountDao.getAccount(toAccount.getId()).get().getBalance()).isEqualTo(BigDecimal.valueOf(499000.55));
    }

    private TransferRequestPayload createTransferRequest(Account fromAccount, Account toAccount, float amount) {
        return TransferRequestPayload.builder()
                .fromAccount(fromAccount.getId())
                .toAccount(toAccount.getId())
                .amount(amount)
                .build();
    }
}
