package com.hodorgeek.mt.dao;

import com.hodorgeek.mt.entity.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerDao {

    List<Customer> getCustomers();

    Optional<Customer> getCustomer(UUID customerId);
}
