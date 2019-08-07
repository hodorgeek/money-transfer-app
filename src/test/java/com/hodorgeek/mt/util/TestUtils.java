package com.hodorgeek.mt.util;

import com.hodorgeek.mt.entity.Account;
import com.hodorgeek.mt.entity.Customer;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class TestUtils {

    public static Customer getCustomerByFirstAndLastName(List<Customer> customers, String firstName, String lastName) {
        return customers.stream()
                .filter(customer -> firstName.equals(customer.getFirstName()) && lastName.equals(customer.getLastName()))
                .findFirst()
                .orElse(null);
    }

    public static UUID extractCustomerId(Customer customer) {
        return customer.getId();
    }

    public static Account extractAccount(Customer customer) {
        return customer.getAccounts().stream().findFirst().orElse(null);
    }

    public static BigDecimal toBigDecimal(String balance) {
        return new BigDecimal(balance);
    }
}
