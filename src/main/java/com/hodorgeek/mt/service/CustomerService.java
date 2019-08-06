package com.hodorgeek.mt.service;

import com.hodorgeek.mt.entity.Customer;

import java.util.List;
import java.util.UUID;

public interface CustomerService {

    List<Customer> getCustomers();

    Customer getCustomer(UUID customerId);

}
