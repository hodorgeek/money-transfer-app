package com.hodorgeek.mt.entity;

import com.hodorgeek.mt.exception.InsufficientBalanceException;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Entity(name = "account")
@Getter
@Setter
@ToString(exclude = {"customer", "lock"})
@EqualsAndHashCode(exclude = {"customer", "lock"})
public class Account {

    @Transient
    public final Lock lock = new ReentrantLock();

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    @SequenceGenerator(name = "seq-gen", sequenceName = "user_sequence", initialValue = 236476251)
    private Long id;

    @Column
    private BigDecimal balance = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "fk_customer", updatable = false, nullable = false)
    private Customer customer;

    public void withdraw(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException(String.format("Insufficient balance to perform withdraw from account: %d", id));
        }
        balance = balance.subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }
}
