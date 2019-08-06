package com.hodorgeek.mt.dao.impl;

import com.google.inject.Provider;
import com.hodorgeek.mt.dao.CustomerDao;
import com.hodorgeek.mt.entity.Customer;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class CustomerDaoImpl implements CustomerDao {

    private final Provider<EntityManager> entityManager;

    @Inject
    public CustomerDaoImpl(final Provider<EntityManager> entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Customer> getCustomers() {
        TypedQuery<Customer> query = entityManager.get().createQuery("select c from customer c", Customer.class);

        return query.getResultList();
    }

    @Override
    public Optional<Customer> getCustomer(UUID id) {
        TypedQuery<Customer> query = entityManager.get()
                .createQuery("select c from customer c where c.id = :id", Customer.class)
                .setParameter("id", id);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            log.warn("No customer with id: {}", id);
        }
        return Optional.empty();
    }
}
