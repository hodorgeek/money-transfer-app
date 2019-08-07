package com.hodorgeek.mt.app;

import com.google.inject.persist.Transactional;
import com.hodorgeek.mt.entity.Account;
import com.hodorgeek.mt.entity.Customer;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.hodorgeek.mt.app.DataLoader.AccountBuilder.*;
import static com.hodorgeek.mt.app.DataLoader.CustomerBuilder.*;

public class DataLoader {

    @Inject
    public DataLoader(EntityManager entityManager) {
        init(entityManager);
    }

    @Transactional
    void init(EntityManager entityManager) {
        entityManager.persist(aCustomer()
                .withFirstName("Abhijeet")
                .withLastName("Gulve")
                .withAccounts(
                        anAccount()
                                .withBalance(BigDecimal.TEN)
                                .build()
                ).build()
        );
        entityManager.persist(aCustomer()
                .withFirstName("Sham")
                .withLastName("Bhand")
                .withAccounts(
                        anAccount()
                                .withBalance(BigDecimal.valueOf(10000))
                                .build()
                ).build()
        );
        entityManager.persist(aCustomer()
                .withFirstName("Sham")
                .withLastName("Bhand")
                .withAccounts(
                        anAccount()
                                .withBalance(BigDecimal.ZERO)
                                .build(),
                        anAccount()
                                .withBalance(BigDecimal.valueOf(100000))
                                .build()
                ).build()
        );
    }

    public static class CustomerBuilder {

        private UUID id;
        private String firstName;
        private String lastName;
        private List<Account> accounts = new ArrayList();

        private CustomerBuilder() {
        }

        public static CustomerBuilder aCustomer() {
            return new CustomerBuilder();
        }

        public CustomerBuilder withId(UUID id) {
            this.id = id;
            return this;
        }

        public CustomerBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public CustomerBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public CustomerBuilder withAccounts(Account... accounts) {
            this.accounts.addAll(Arrays.asList(accounts));
            return this;
        }

        public Customer build() {
            Customer Customer = new Customer();
            Customer.setId(id);
            Customer.setFirstName(firstName);
            Customer.setLastName(lastName);
            accounts.forEach(Customer::addAccount);
            return Customer;
        }
    }

    public static class AccountBuilder {

        private Long id;
        private BigDecimal balance;

        private AccountBuilder() {
        }

        public static AccountBuilder anAccount() {
            return new AccountBuilder();
        }

        public AccountBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public AccountBuilder withBalance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public Account build() {
            Account account = new Account();
            account.setId(id);
            account.setBalance(balance);
            return account;
        }
    }
}
