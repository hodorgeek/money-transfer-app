[![Build Status](https://travis-ci.org/hodorgeek/money-transfer-app.svg?branch=master)](https://travis-ci.org/hodorgeek/money-transfer-app)

# Money Transfer API

API for money transfer

## Overview
A simple REST web services for transferring money between two internal bank accounts   

## Task
Design and implement a RESTful API (including data model and the backing implementation) for money transfers between accounts.

### Explicit requirements:
1. You can use Java or Kotlin.
1. Keep it simple and to the point (e.g. no need to implement any authentication).
1. Assume the API is invoked by multiple systems and services on behalf of end users.
1. You can use frameworks/libraries if you like (except Spring), but don't forget about requirement #2 – keep it simple and avoid heavy frameworks.
1. The data store should run in-memory for the sake of this test.
1. The final result should be executable as a standalone program (should not require a pre-installed container/server).
1. Demonstrate with tests that the API works as expected.

## Technology stack
- Java 8
- [Gradle](https://docs.gradle.org)
- [Lombok](https://projectlombok.org)
- Juice
- H2
- JPA over Hibernate
- Spark
- [JUnit 5](https://junit.org/junit5/)
- Cucumber


## Unit Tests

- To execute unit test(junit5) use: ```./gradlew test```

## Feature Tests / API Acceptance Test:
- To run api acceptance test(cucmber test) use, ``` ./gradlew test -P acceptanceTest``` 

### Real life test scenarios for endpoints:
* `/api/customers` - [see here](src/test/resources/features/customers_accounts.feature)
* `/api/transfer` - [see here](src/test/resources/features/transfer.feature)

Building and running application
--------------------------------


Please note: If you're on Windows, use `gradlew.bat` instead of `./gradlew` script

To build application, execute:

```
./gradlew build
```
To run application execute:

```
./gradlew run
```
Generate sample data on application startup
```
./gradlew run -P loadData 
        OR
./gradlew run --args='true'
```

In the both runs, the application starts on following host:port - [http://localhost:4567](http://localhost:4567)


### Fat jar

To generate fat jar file with all dependencies, execute:

```
./gradlew shadowJar
```
Assuming you have executed command above, to run server as a standalone fat jar, execute:

```
java -jar build/libs/money-transfer-app-1.0.jar
        OR
java -jar build/libs/money-transfer-app-1.0.jar true
```
Both will start server on `4567`. The second will load some default data on application startup.


## API Documentation: 

 TODO: Please check the Swagger Documentation:
