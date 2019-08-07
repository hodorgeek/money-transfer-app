package com.hodorgeek.mt.dto.mapper;

import com.hodorgeek.mt.dto.AccountDTO;
import com.hodorgeek.mt.dto.CustomerDTO;
import com.hodorgeek.mt.entity.Account;
import com.hodorgeek.mt.entity.Customer;

import java.util.stream.Collectors;

public class CustomerAccountMapper {

    public static CustomerDTO toDTO(final Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .accounts(customer.getAccounts()
                        .stream()
                        .map(account -> CustomerAccountMapper.toDTO(account))
                        .collect(Collectors.toList()))
                .build();
    }

    public static AccountDTO toDTO(final Account account) {
        return AccountDTO.builder()
                .id(account.getId())
                .balance(account.getBalance())
                .build();

    }
}
