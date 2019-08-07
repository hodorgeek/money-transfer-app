package com.hodorgeek.mt.service.impl;

import com.hodorgeek.mt.dao.CustomerDao;
import com.hodorgeek.mt.entity.Customer;
import com.hodorgeek.mt.exception.CustomerNotFoundException;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.hodorgeek.mt.app.DataLoader.CustomerBuilder.aCustomer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {

    @Mock
    private CustomerDao customerDao;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private static final String CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE = "customer with id: %s cannot be found";

    @Test
    public void returnsAllCustomers() {
        // given
        Customer c1 = aCustomer()
                .withId(UUID.randomUUID())
                .withFirstName("Abc")
                .withLastName("Qaz")
                .build();
        Customer c2 = aCustomer()
                .withId(UUID.randomUUID())
                .withFirstName("Zxc")
                .withLastName("Qwe")
                .build();
        when(customerDao.getCustomers()).thenReturn(Lists.newArrayList(c1, c2));

        // when
        List<Customer> actual = customerService.getCustomers();

        // then
        assertThat(actual).containsExactlyInAnyOrder(c1, c2);
    }

    @Test
    public void returnsEmptyListWhenNoCustomers() {
        // given
        when(customerDao.getCustomers()).thenReturn(Lists.newArrayList());

        // when
        List<Customer> actual = customerService.getCustomers();

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    public void returnsSingleCustomer() {
        // given
        Customer c1 = aCustomer()
                .withId(UUID.randomUUID())
                .withFirstName("Abc")
                .withLastName("Qaz")
                .build();
        when(customerDao.getCustomer(c1.getId())).thenReturn(Optional.of(c1));

        // when
        Customer actual = customerService.getCustomer(c1.getId());

        // then
        assertThat(actual).isEqualTo(c1);
    }

    @Test
    public void throwsExceptionWhenCustomerNotFound() {
        // given
        final UUID nonExistingCustomerId = UUID.randomUUID();
        when(customerDao.getCustomer(any(UUID.class))).thenReturn(Optional.empty());
        final CustomerNotFoundException expectedException = new CustomerNotFoundException(String.format(CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE, nonExistingCustomerId));

        // when
        final CustomerNotFoundException customerNotFoundException = assertThrows(CustomerNotFoundException.class, () -> {
            customerService.getCustomer(nonExistingCustomerId);
        });

        // then
        assertThat(customerNotFoundException.getMessage()).isEqualTo(expectedException.getMessage());
    }
}
