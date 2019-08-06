package com.hodorgeek.mt.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ToString
@EqualsAndHashCode
@Entity(name = "customer")
public class Customer {

    @Id
    @Column
    @GeneratedValue
    @Getter
    @Setter
    private UUID id;

    @Column
    @Setter
    @Getter
    private String firstName;

    @Column
    @Setter
    @Getter
    private String lastName;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    private List<Account> accounts = new ArrayList<>();

    public void addAccount(Account account) {
        account.setCustomer(this);
        accounts.add(account);
    }

}

