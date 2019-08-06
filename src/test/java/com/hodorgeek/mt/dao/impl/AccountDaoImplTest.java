package com.hodorgeek.mt.dao.impl;

import com.google.common.collect.Lists;
import com.hodorgeek.mt.dao.AccountDao;
import com.hodorgeek.mt.dao.GenericDaoIntegrationTest;
import com.hodorgeek.mt.entity.Account;
import com.hodorgeek.mt.entity.Customer;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.hodorgeek.mt.DataLoader.AccountBuilder.anAccount;
import static com.hodorgeek.mt.DataLoader.CustomerBuilder.aCustomer;
import static org.assertj.core.api.Assertions.assertThat;


class AccountDaoImplTest extends GenericDaoIntegrationTest {

    private AccountDao underTest;

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
        underTest = new AccountDaoImpl(GenericDaoIntegrationTest::getEntityManager);
    }

    @AfterEach
    public void clearDB() {
        cleanDB();
    }

    @Test
    public void returnsAllAccountsForGivenCustomer() {
        // given
        Account a1 = anAccount().withBalance(BigDecimal.ZERO).build();
        Account a2 = anAccount().withBalance(BigDecimal.TEN).build();
        Customer c = aCustomer()
                .withFirstName("Abc")
                .withLastName("Zxc")
                .withAccounts(a1, a2)
                .build();
        storeCustomers(Lists.newArrayList(c));

        // when
        List<Account> actual = underTest.getAccounts(c.getId());

        // then
        assertThat(actual).containsExactlyInAnyOrder(a1, a2);
    }

    @Test
    public void returnsEmptyListWhenNoAccountsForGivenCustomer() {
        // when
        List<Account> accounts = underTest.getAccounts(UUID.randomUUID());

        // then
        assertThat(accounts).isEmpty();
    }

    @Test
    public void returnsOptionalWithSingleAccountForGivenCustomer() {
        // given
        Account a = anAccount()
                .withBalance(BigDecimal.ZERO)
                .build();
        Customer c = aCustomer()
                .withFirstName("Abc")
                .withLastName("Zxc")
                .withAccounts(a)
                .build();
        storeCustomers(Lists.newArrayList(c));

        // when
        Optional<Account> account = underTest.getAccount(c.getId(), a.getId());

        assertThat(account.get()).isEqualTo(a);
    }

    @Test
    public void returnsEmptyOptionalWhenAccountNotFound() {
        // given
        long nonExistingAccountId = 0L;
        UUID customerId = UUID.randomUUID();

        // when
        Optional<Account> actual = underTest.getAccount(customerId, nonExistingAccountId);

        // then
        assertThat(actual.isPresent()).isFalse();
    }

}
