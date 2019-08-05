package com.hodorgeek.mt.dao;

import com.hodorgeek.mt.entity.Customer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;

public class GenericDaoIntegrationTest {

    private static EntityManagerFactory entityManagerFactory;
    private static EntityManager entityManager;
    private static List<Customer> customers = new ArrayList<>();

    public static void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("money-transfer");
        entityManager = entityManagerFactory.createEntityManager();
    }

    public static void tearDown() {
        entityManager.clear();
        entityManager.close();
        entityManagerFactory.close();
    }

    public static EntityManager getEntityManager() {
        return entityManager;
    }

    protected List<Customer> storeCustomers(List<Customer> toSave) {
        EntityTransaction tx = getEntityManager().getTransaction();
        tx.begin();
        toSave.forEach(getEntityManager()::persist);
        tx.commit();

        customers.addAll(toSave);

        return toSave;
    }

    protected void cleanDB() {
        EntityTransaction tx = getEntityManager().getTransaction();
        tx.begin();
        customers.stream().map(Customer::getId).forEach(
                id -> getEntityManager().remove(getEntityManager().find(Customer.class, id))
        );
        customers.clear();
        tx.commit();
    }
}
