package featureTest;

import com.google.gson.JsonObject;
import com.hodorgeek.mt.dao.GenericDaoIntegrationTest;
import com.hodorgeek.mt.entity.Account;
import com.hodorgeek.mt.entity.Customer;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.After;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.hodorgeek.mt.app.DataLoader.CustomerBuilder.aCustomer;
import static com.hodorgeek.mt.util.TestUtils.*;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class StepDefinitions extends GenericDaoIntegrationTest {

    private static final String RANDOM_CUSTOMER_ID = "be8bdd99-d68e-48f9-89f4-ad2b066b4ae2";
    private static final Long RANDOM_ACCOUNT_ID = 236436253L;
    private static final String SHAM_BHAND_ID_PLACEHOLDER = "<ShamBhandId>";
    private static final String NON_EXISTING_ID_PLACEHOLDER = "<NonExistingId>";
    private static final String SHAM_BHAND_ACCOUNT_PLACEHOLDER = "<ShamBhandAccount>";
    private static final String SHAM_BHAND_ACCOUNT_ID_PLACEHOLDER = "<ShamBhandAccountId>";
    private static final String ABHIJEET_GULVE_ACCOUNT_PLACEHOLDER = "<AbhijeetGulveAccount>";
    private static final String ABHIJEET_GULVE_ACCOUNT_ID_PLACEHOLDER = "<AbhijeetGulveAccountId>";
    private static final String NON_EXISTING_ACCOUNT_ID_PLACEHOLDER = "<NonExistingAccountId>";

    static {
        RestAssured.baseURI = "http://localhost:4567";
        RestAssured.basePath = "/api";
    }

    private Response response;
    private ValidatableResponse json;

    private List<Customer> savedCustomers;
    private List<Customer> customers;

    @After
    public void clearDB() {
        cleanDB();
    }

    @Given("^system contains following customers$")
    public void setupCustomers(DataTable inputData) {
        customers = inputData.asMaps(String.class, String.class).stream()
                .map(row -> aCustomer()
                        .withFirstName(row.get("firstName"))
                        .withLastName(row.get("lastName"))
                        .build())
                .collect(Collectors.toList());
    }

    @Given("^customers have following accounts$")
    public void setupAccounts(DataTable inputData) {
        inputData.asMaps(String.class, String.class)
                .forEach(row -> {
                    Customer customer = getCustomerByFirstAndLastName(customers, row.get("firstName"), row.get("lastName"));
                    Account account = new Account();
                    account.setBalance(toBigDecimal(row.get("accountBalance")));
                    customer.addAccount(account);
                });
    }

    @When("^customer requests GET (.*)$")
    public void doGetRequest(String path) {
        savedCustomers = storeCustomers(customers);
        response = get(resolvePlaceholders(path));
    }

    @When("^customer requests POST /transfer with following body$")
    public void doPostRequest(DataTable inputData) {
        Map<String, String> input = inputData.asMap(String.class, String.class);
        savedCustomers = storeCustomers(customers);
        JsonObject json = new JsonObject();
        json.addProperty("fromAccount", resolvePlaceholder(input.get("fromAccount"), Long.class));
        json.addProperty("toAccount", resolvePlaceholder(input.get("toAccount"), Long.class));
        json.addProperty("amount", Float.valueOf(input.get("amount")));

        response = given().body(json).post("/transfer");
    }

    @Then("^response status is (\\d+)$")
    public void checkResponseStatus(int status) {
        json = response.then().statusCode(status);
    }

    @Then("^response contains (\\d+) customers$")
    public void checkResponseSize(int expectedSize) {
        json.body("$", hasSize(expectedSize));
    }

    @Then("^response includes the following customers$")
    public void checkResponseMultipleCustomers(DataTable expectedData) {
        expectedData.asMaps(String.class, String.class)
                .forEach(expected -> {
                    String firstName = expected.get("firstName");
                    String lastName = expected.get("lastName");
                    json.root(format("[%s]", expected.get("order")))
                            .body("id", equalTo(extractCustomerId(
                                    getCustomerByFirstAndLastName(savedCustomers, firstName, lastName)).toString()))
                            .body("firstName", equalTo(firstName))
                            .body("lastName", equalTo(lastName))
                            .body("accounts", hasSize(0));
                });
    }

    @Then("^response includes the following customer$")
    public void checkResponseSingleCustomer(DataTable expectedData) {
        Map<String, String> expected = expectedData.asMap(String.class, String.class);

        String firstName = expected.get("firstName");
        String lastName = expected.get("lastName");

        json.body("id", equalTo(extractCustomerId(
                getCustomerByFirstAndLastName(savedCustomers, firstName, lastName)).toString()))
                .body("firstName", equalTo(firstName))
                .body("lastName", equalTo(lastName))
                .body("accounts", hasSize(0));
    }

    @Then("^response includes the following message$")
    public void checkResponseException(DataTable expectedData) {
        Map<String, String> expected = expectedData.asMap(String.class, String.class);

        json.body("statusCode", equalTo(Integer.valueOf(expected.get("statusCode"))))
                .body("message", equalTo(resolvePlaceholders(expected.get("message"))));
    }

    @Then("^response includes the following transfer status$")
    public void checkResponseTransfer(DataTable expectedData) {
        Map<String, String> expected = expectedData.asMap(String.class, String.class);

        json.body("transferStatus", equalTo(expected.get("transferStatus")));
    }

    @Then("^response includes the following accounts$")
    public void checkResponseMultipleAccounts(DataTable expectedData) {
        expectedData.asMaps(String.class, String.class)
                .forEach(expected -> json.root(format("[%s]", expected.get("order")))
                        .body("balance", equalTo(Float.valueOf(expected.get("balance")))));
    }

    @Then("^response includes the following account$")
    public void checkResponseSingleAccount(DataTable expectedData) {
        Map<String, String> expected = expectedData.asMap(String.class, String.class);
        long expectedId = extractAccount(getCustomerByFirstAndLastName(savedCustomers, "Sham", "Bhand")).getId();
        json.body("id", equalTo((int) expectedId))
                .body("balance", equalTo(Float.valueOf(expected.get("balance"))));
    }

    @Then("^balance of accounts are following")
    public void checkBalanceOfAccounts(DataTable expectedData) {
        expectedData.asMaps(String.class, String.class).forEach(row -> {
            Account account = resolvePlaceholder(row.get("accountId"), Account.class);
            getEntityManager().refresh(account);
            assertThat(account.getBalance())
                    .isEqualTo(toBigDecimal(row.get("balance")));
        });
    }

    private String resolvePlaceholders(String str) {
        if (str.contains(SHAM_BHAND_ID_PLACEHOLDER)) {
            str = str.replace(SHAM_BHAND_ID_PLACEHOLDER,
                    resolvePlaceholder(SHAM_BHAND_ID_PLACEHOLDER, UUID.class).toString());
        }
        if (str.contains(NON_EXISTING_ID_PLACEHOLDER)) {
            str = str.replace(NON_EXISTING_ID_PLACEHOLDER,
                    resolvePlaceholder(NON_EXISTING_ID_PLACEHOLDER, String.class));
        }
        if (str.contains(SHAM_BHAND_ACCOUNT_ID_PLACEHOLDER)) {
            str = str.replace(SHAM_BHAND_ACCOUNT_ID_PLACEHOLDER,
                    String.valueOf(resolvePlaceholder(SHAM_BHAND_ACCOUNT_ID_PLACEHOLDER, Long.class)));
        }
        if (str.contains(NON_EXISTING_ACCOUNT_ID_PLACEHOLDER)) {
            str = str.replace(NON_EXISTING_ACCOUNT_ID_PLACEHOLDER,
                    String.valueOf(resolvePlaceholder(NON_EXISTING_ACCOUNT_ID_PLACEHOLDER, Long.class)));
        }
        return str;
    }

    private <T> T resolvePlaceholder(String placeholder, Class<T> type) {
        switch (placeholder) {
            case SHAM_BHAND_ID_PLACEHOLDER:
                return type.cast(extractCustomerId(getCustomerByFirstAndLastName(savedCustomers, "Sham", "Bhand")));
            case SHAM_BHAND_ACCOUNT_PLACEHOLDER:
                return type.cast(extractAccount(getCustomerByFirstAndLastName(savedCustomers, "Sham", "Bhand")));
            case SHAM_BHAND_ACCOUNT_ID_PLACEHOLDER:
                return type.cast(extractAccount(getCustomerByFirstAndLastName(savedCustomers, "Sham", "Bhand")).getId());
            case ABHIJEET_GULVE_ACCOUNT_PLACEHOLDER:
                return type.cast(extractAccount(getCustomerByFirstAndLastName(savedCustomers, "Abhijeet", "Gulve")));
            case ABHIJEET_GULVE_ACCOUNT_ID_PLACEHOLDER:
                return type.cast(extractAccount(getCustomerByFirstAndLastName(savedCustomers, "Abhijeet", "Gulve")).getId());
            case NON_EXISTING_ID_PLACEHOLDER:
                return type.cast(RANDOM_CUSTOMER_ID);
            case NON_EXISTING_ACCOUNT_ID_PLACEHOLDER:
                return type.cast(RANDOM_ACCOUNT_ID);
        }
        throw new IllegalStateException("Not known placeholder");
    }
}
