Feature: Transfer REST API

  Scenario: Transfer between two customers accounts
    Given system contains following customers
      | firstName | lastName |
      | Sham      | Bhand    |
      | Abhijeet  | Gulve    |
    And customers have following accounts
      | firstName | lastName | accountBalance |
      | Sham      | Bhand    | 10000.00       |
      | Abhijeet  | Gulve    | 20000.00       |
    When customer requests POST /transfer with following body
      | fromAccount | <ShamBhandAccountId>     |
      | toAccount   | <AbhijeetGulveAccountId> |
      | amount      | 5000.00                  |
    Then response status is 200
    And response includes the following transfer status
      | transferStatus | OK |
    And balance of accounts are following
      | accountId             | balance  |
      | <ShamBhandAccount>    | 5000.00  |
      | <AbhijeetGulveAccount>| 25000.00 |

  Scenario: Insufficient funds to perform transfer
    Given system contains following customers
      | firstName | lastName |
      | Sham      | Bhand    |
      | Abhijeet  | Gulve    |
    And customers have following accounts
      | firstName | lastName   | accountBalance |
      | Sham      | Bhand      | 800.00         |
      | Abhijeet  | Gulve      | 200.00         |
    When customer requests POST /transfer with following body
      | fromAccount | <ShamBhandAccountId>     |
      | toAccount   | <AbhijeetGulveAccountId> |
      | amount      | 1000.00                  |
    Then response status is 400
    And response includes the following message
      | statusCode | 400                                                                     |
      | message    | Insufficient balance to perform withdraw from account: <ShamBhandAccountId> |

  Scenario: Transfer to non existing account
    Given system contains following customers
      | firstName | lastName   |
      | Sham      | Bhand      |
    And customers have following accounts
      | firstName | lastName | accountBalance |
      | Sham      | Bhand    | 800.00         |
    When customer requests POST /transfer with following body
      | fromAccount | <ShamBhandAccountId>   |
      | toAccount   | <NonExistingAccountId> |
      | amount      | 100.00                 |
    Then response status is 400
    And response includes the following message
      | statusCode | 400                                                                |
      | message    | Account with id <NonExistingAccountId> (toAccount) cannot be found |

  Scenario: Transfer from non existing account
    Given system contains following customers
      | firstName | lastName |
      | Sham      | Bhand    |
    And customers have following accounts
      | firstName | lastName | accountBalance |
      | Sham      | Bhand    | 800.00         |
    When customer requests POST /transfer with following body
      | fromAccount | <NonExistingAccountId> |
      | toAccount   | <ShamBhandAccountId>   |
      | amount      | 100.00                 |
    Then response status is 400
    And response includes the following message
      | statusCode | 400                                                                  |
      | message    | Account with id <NonExistingAccountId> (fromAccount) cannot be found |

  Scenario: Transfer of negative amount
    Given system contains following customers
      | firstName | lastName   |
      | Sham      | Bhand      |
      | Abhijeet  | Gulve      |
    And customers have following accounts
      | firstName | lastName   | accountBalance |
      | Sham      | Bhand      | 800.00         |
      | Abhijeet  | Gulve      | 200.00         |
    When customer requests POST /transfer with following body
      | fromAccount | <ShamBhandAccountId>     |
      | toAccount   | <AbhijeetGulveAccountId> |
      | amount      | -100.00                  |
    Then response status is 400
    And response includes the following message
      | statusCode | 400                                  |
      | message    | Amount value has to be greater than 0 |
