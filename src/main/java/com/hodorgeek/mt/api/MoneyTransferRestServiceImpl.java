package com.hodorgeek.mt.api;

import com.google.gson.Gson;
import com.hodorgeek.mt.dto.AccountDTO;
import com.hodorgeek.mt.dto.CustomerDTO;
import com.hodorgeek.mt.dto.TransferRequestPayload;
import com.hodorgeek.mt.exception.AccountNotFoundException;
import com.hodorgeek.mt.exception.CustomerNotFoundException;
import com.hodorgeek.mt.exception.InsufficientBalanceException;
import com.hodorgeek.mt.exception.InvalidAmountValueException;
import com.hodorgeek.mt.dto.mapper.CustomerAccountMapper;
import com.hodorgeek.mt.service.AccountService;
import com.hodorgeek.mt.service.CustomerService;
import com.hodorgeek.mt.service.TransferService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static spark.Spark.*;

@Slf4j
public class MoneyTransferRestServiceImpl implements MoneyTransferRestService {

    private static final String EMPTY = "";
    private static final String SUCCESSFUL_TRANSFER_BODY = "{\"transferStatus\": \"OK\"}";
    private static final String FAILED_TRANSFER_BODY = "{\"transferStatus\": \"FAILED\"}";
    private static final String EXCEPTION_BODY = "{\"statusCode\": %d, \"message\": \"%s\"}";

    private final Gson gson;
    private final CustomerService customerService;
    private final AccountService accountService;
    private final TransferService transferService;

    @Inject
    public MoneyTransferRestServiceImpl(Gson gson, CustomerService customerService, AccountService accountService, TransferService transferService) {
        this.gson = gson;
        this.customerService = customerService;
        this.accountService = accountService;
        this.transferService = transferService;
    }

    @Override
    public void handleRequests() {
        path("/api", () -> {
            before("/*", (req, res) -> {
                log.info("Received {} request to endpoint: {}", req.requestMethod(), req.uri());
                res.type("application/json");
            });

            path("/customers", () -> {

                get(EMPTY, (req, res) -> {
                    final List<CustomerDTO> customers = customerService.getCustomers()
                            .stream()
                            .map(customer -> CustomerAccountMapper.toDTO(customer))
                            .collect(Collectors.toList());
                    return gson.toJsonTree(customers);
                });

                path("/:customerId", () -> {

                    get(EMPTY, (req, res) -> {
                        final UUID customerId = UUID.fromString(req.params("customerId"));
                        final CustomerDTO customerDTO = CustomerAccountMapper.toDTO(customerService.getCustomer(customerId));
                        return gson.toJsonTree(customerDTO);
                    });

                    path("/accounts", () -> {

                        get(EMPTY, (req, res) -> {
                            final UUID customerId = UUID.fromString(req.params("customerId"));
                            final List<AccountDTO> accounts = accountService.getAccounts(customerId)
                                    .stream()
                                    .map(account -> CustomerAccountMapper.toDTO(account))
                                    .collect(Collectors.toList());
                            return gson.toJsonTree(accounts);
                        });

                        get("/:accountId", (req, res) -> {
                            final UUID customerId = UUID.fromString(req.params("customerId"));
                            final Long accountId = Long.valueOf(req.params("accountId"));
                            final AccountDTO accountDTO = CustomerAccountMapper.toDTO(accountService.getAccount(customerId, accountId));
                            return gson.toJsonTree(accountDTO);
                        });
                    });
                });
            });

            post("/transfer", (req, res) -> {
                TransferRequestPayload transferRequest = gson.fromJson(req.body(), TransferRequestPayload.class);
                return transferService.transfer(transferRequest) ? SUCCESSFUL_TRANSFER_BODY : FAILED_TRANSFER_BODY;
            });

            exception(CustomerNotFoundException.class, (ex, req, res) -> {
                res.status(404);
                res.body(String.format(EXCEPTION_BODY, 404, ex.getMessage()));
            });

            exception(AccountNotFoundException.class, (ex, req, res) -> {
                int statusCode = "post".equalsIgnoreCase(req.requestMethod()) ? 400 : 404;
                res.status(statusCode);
                res.body(String.format(EXCEPTION_BODY, statusCode, ex.getMessage()));
            });

            exception(InsufficientBalanceException.class, (ex, req, res) -> {
                res.status(400);
                res.body(String.format(EXCEPTION_BODY, 400, ex.getMessage()));
            });

            exception(InvalidAmountValueException.class, (ex, req, res) -> {
                res.status(400);
                res.body(String.format(EXCEPTION_BODY, 400, ex.getMessage()));
            });
        });
    }

}
