package com.hodorgeek.mt.service.impl;

import com.hodorgeek.mt.dao.CustomerDao;
import com.hodorgeek.mt.entity.Customer;
import com.hodorgeek.mt.exception.CustomerNotFoundException;
import com.hodorgeek.mt.service.CustomerService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CustomerServiceImpl implements CustomerService {

    private CustomerDao customerDao;

    @Override
    public List<Customer> getCustomers() {
        return customerDao.getCustomers();
    }

    @Override
    public Customer getCustomer(UUID customerId) {
        final Optional<Customer> customerOptional = customerDao.getCustomer(customerId);
        return customerOptional.orElseThrow(() -> new CustomerNotFoundException(String.format("customer with id: %s cannot be found", customerId)));
    }
}
