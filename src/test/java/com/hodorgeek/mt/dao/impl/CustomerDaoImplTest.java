package com.hodorgeek.mt.dao.impl;

import com.hodorgeek.mt.dao.CustomerDao;
import com.hodorgeek.mt.dao.GenericDaoIntegrationTest;
import com.hodorgeek.mt.entity.Customer;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.hodorgeek.mt.DataLoader.CustomerBuilder.aCustomer;
import static org.assertj.core.api.Assertions.assertThat;

class CustomerDaoImplTest extends GenericDaoIntegrationTest {

    private CustomerDao underTest;

    @BeforeAll
    public static void init() {
        GenericDaoIntegrationTest.init();
    }

    @AfterAll
    public static void tearDown() {
        GenericDaoIntegrationTest.tearDown();
    }

    @BeforeEach
    public void setUp() {
        underTest = new CustomerDaoImpl(GenericDaoIntegrationTest::getEntityManager);
    }

    @AfterEach
    public void clearDB() {
        cleanDB();
    }

    @Test
    public void returnsEmptyListWhenNoCustomers() {
        // when
        List<Customer> actual = underTest.getCustomers();

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    public void returnsAllCustomers() {
        // given
        Customer c1 = aCustomer()
                .withFirstName("KC")
                .withLastName("Pandey")
                .build();
        Customer c2 = aCustomer()
                .withFirstName("Rushi")
                .withLastName("Sonawane")
                .build();
        storeCustomers(Lists.newArrayList(c1, c2));

        // when
        List<Customer> actual = underTest.getCustomers();

        // then
        assertThat(actual).containsExactlyInAnyOrder(c1, c2);
    }

    @Test
    public void returnsOptionalWithSingleCustomer() {
        // given
        Customer c1 = aCustomer()
                .withFirstName("KC")
                .withLastName("Pandey")
                .build();
        storeCustomers(Lists.newArrayList(c1));

        // when
        Optional<Customer> actual = underTest.getCustomer(c1.getId());

        // then
        assertThat(actual.get()).isEqualTo(c1);
    }

    @Test
    public void returnsEmptyOptionalWhenCustomerNotFound() {
        // given
        UUID nonExistingCustomerId = UUID.randomUUID();

        // when
        Optional<Customer> actual = underTest.getCustomer(nonExistingCustomerId);

        // then
        assertThat(actual.isPresent()).isFalse();
    }
}
