Feature: Customers / Accounts REST API

  Scenario: Retrieving all customers
    Given system contains following customers
      | firstName | lastName |
      | Sham      | Bhand    |
      | Abhijeet  | Gulve    |
    When customer requests GET /customers
    Then response status is 200
    And response contains 2 customers
    And response includes the following customers
      | order | firstName | lastName |
      | 0     | Sham      | Bhand    |
      | 1     | Abhijeet  | Gulve    |

  Scenario: Retrieving single customer
    Given system contains following customers
      | firstName | lastName |
      | Sham      | Bhand    |
      | Abhijeet  | Gulve    |
    When customer requests GET /customers/<ShamBhandId>
    Then response status is 200
    And response includes the following customer
      | firstName | Sham     |
      | lastName  | Bhand    |

  Scenario: Retrieving non existing customer
    Given system contains following customers
      | firstName | lastName |
      | Sham      | Bhand    |
      | Abhijeet  | Gulve    |
    When customer requests GET /customers/<NonExistingId>
    Then response status is 404
    And response includes the following message
      | statusCode | 404                                             |
      | message    | customer with id: <NonExistingId> cannot be found |

  Scenario: Retrieving all accounts for given customer
    Given system contains following customers
      | firstName | lastName |
      | Sham      | Bhand    |
    And customers have following accounts
      | firstName | lastName | accountBalance |
      | Sham      | Bhand    | 0.00           |
      | Sham      | Bhand    | 10000.00       |
    When customer requests GET /customers/<ShamBhandId>/accounts
    Then response status is 200
    And response includes the following accounts
      | order | balance  |
      | 0     | 0.00     |
      | 1     | 10000.00 |

  Scenario: Retrieving single account for given customer
    Given system contains following customers
      | firstName | lastName |
      | Sham      | Bhand    |
    And customers have following accounts
      | firstName | lastName | accountBalance |
      | Sham      | Bhand    | 10000.00       |
    When customer requests GET /customers/<ShamBhandId>/accounts/<ShamBhandAccountId>
    Then response status is 200
    And response includes the following account
      | balance   | 10000.00 |

  Scenario: Retrieving non existing account for given customer
    Given system contains following customers
      | firstName | lastName |
      | Sham      | Bhand    |
    When customer requests GET /customers/<ShamBhandId>/accounts/<NonExistingAccountId>
    Then response status is 404
    And response includes the following message
      | statusCode | 404                                                     |
      | message    | Account with id: <NonExistingAccountId> cannot be found |
