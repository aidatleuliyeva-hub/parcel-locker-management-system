# Parcel Locker Management System

[![Java CI](https://github.com/aidatleuliyeva-hub/parcel-locker-management-system/actions/workflows/ci.yml/badge.svg)](https://github.com/aidatleuliyeva-hub/parcel-locker-management-system/actions/workflows/ci.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=aidatleuliyeva-hub_parcel-locker-management-system&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=aidatleuliyeva-hub_parcel-locker-management-system)
[![Coverage Status](https://coveralls.io/repos/github/aidatleuliyeva-hub/parcel-locker-management-system/badge.svg?branch=main)](https://coveralls.io/github/aidatleuliyeva-hub/parcel-locker-management-system?branch=main)

A Java Swing desktop application for managing parcel lockers and parcel deliveries.

The project was developed for the Automated Software Testing exam.
It focuses on business logic, persistence with JPA/Hibernate, and different testing techniques.

## Project idea

The application allows the user to:

* register customers;
* create locker cells;
* create parcels for customers;
* assign parcels to available locker cells;
* generate pickup codes;
* collect parcels using a valid pickup code;
* view customers, locker cells, and parcels in the Swing user interface.

## Domain model

The main domain entities are:

* `Customer`
* `Parcel`
* `LockerCell`

A customer can have multiple parcels.
A parcel belongs to one customer.
A parcel can be assigned to one locker cell and later collected by the customer.

## Business rules

The application implements the following business rules:

* a parcel can only be assigned to an available locker cell;
* a parcel size must match the locker cell size;
* an already assigned parcel cannot be assigned again;
* a collected parcel cannot be assigned again;
* a parcel can only be collected with a valid pickup code;
* a parcel that has not been assigned cannot be collected;
* after parcel collection, the locker cell becomes available again;
* customer phone numbers must be unique;
* locker cell numbers must be unique;
* parcel tracking numbers must be unique.

## Technology stack

* Java 17
* Maven
* Java Swing
* JPA / Hibernate
* H2 database for local desktop execution
* PostgreSQL for integration testing
* Testcontainers
* JUnit 5
* AssertJ
* AssertJ Swing
* JaCoCo
* PIT mutation testing

## Project structure

```text
src/main/java/com/example/parcellocker
│
├── Main.java
│
├── application
│   ├── ParcelLockerApplicationService.java
│   └── dto
│       ├── CustomerView.java
│       ├── LockerCellView.java
│       └── ParcelView.java
│
├── domain
│   ├── Customer.java
│   ├── LockerCell.java
│   ├── Parcel.java
│   ├── ParcelStatus.java
│   └── Size.java
│
├── persistence
│   ├── JpaUtil.java
│   ├── TestJpaFactory.java
│   └── TransactionManager.java
│
├── repository
│   ├── CustomerRepository.java
│   ├── LockerCellRepository.java
│   └── ParcelRepository.java
│
├── service
│   ├── BusinessException.java
│   ├── ParcelLockerService.java
│   └── PickupCodeGenerator.java
│
└── ui
    └── MainFrame.java
```

## Architecture

The project uses a layered architecture.

### Domain layer

The domain layer contains the core entities and business state:

* `Customer`
* `Parcel`
* `LockerCell`
* `ParcelStatus`
* `Size`

The entities validate their own internal state.
For example, a locker cell cannot have a blank cell number, and a parcel cannot be created without a customer.

### Service layer

The service layer contains the main business rules.

`ParcelLockerService` is responsible for:

* assigning a parcel to a locker cell;
* checking parcel and locker cell compatibility;
* validating pickup codes;
* collecting parcels;
* releasing locker cells after collection.

### Repository layer

The repository layer encapsulates database access using JPA `EntityManager`.

Repositories include:

* `CustomerRepository`
* `LockerCellRepository`
* `ParcelRepository`

### Application layer

`ParcelLockerApplicationService` coordinates transactions, repositories, and domain services.

The Swing UI does not directly work with repositories.
It communicates with the application service.

### UI layer

The UI is implemented with Java Swing in `MainFrame`.

The interface contains tabs for:

* Customers
* Locker Cells
* Parcels
* Actions

## How to run the application

From the project root:

```powershell
mvn exec:java
```

Alternative command:

```powershell
mvn exec:java "-Dexec.mainClass=com.example.parcellocker.Main"
```

The application uses an H2 local database for desktop execution.

## How to run tests

Run unit tests and fast integration tests:

```powershell
mvn clean test
```

Run the full verification lifecycle:

```powershell
mvn clean verify
```

This command runs:

* unit tests;
* integration tests;
* Swing E2E tests;
* PostgreSQL integration tests with Testcontainers;
* JaCoCo coverage report;
* JaCoCo coverage check.

## Test types

The project includes several types of tests.

### Unit tests

Unit tests verify domain and service logic without a real database.

Examples:

* parcel assignment rules;
* pickup code validation;
* parcel collection rules;
* entity validation.

### Integration tests

Integration tests verify repository behavior with JPA/Hibernate.

The project uses:

* H2 in-memory database for fast repository tests;
* PostgreSQL through Testcontainers for real database integration tests.

### End-to-end UI test

The project includes a Swing E2E test using AssertJ Swing.

The test verifies that the user can interact with the desktop UI.

### Code coverage

JaCoCo is used for code coverage.

The strict JaCoCo check is configured for the business and persistence-related parts of the application.

The following classes are excluded from the strict line coverage check:

* application bootstrap class;
* Swing UI classes;
* DTO records;
* production JPA bootstrap utility.

The Swing UI is tested separately with AssertJ Swing.

### Mutation testing

PIT is used for mutation testing.

Run mutation testing with:

```powershell
mvn -Pmutation-testing -U org.pitest:pitest-maven:1.15.8:mutationCoverage
```

The report is generated here:

```text
target/pit-reports/index.html
```

The mutation testing scope is focused on the domain, service, application, and repository code.

`TransactionManager` is excluded from PIT because it is infrastructure code around JPA transactions. Its behavior is verified through integration tests with JPA/Hibernate, while mutation testing is focused on project-specific business and application logic.

Current local PIT results:

* Line coverage: 100%
* Generated mutations: 129
* Killed mutations: 129
* Mutation coverage: 100%
* Survived mutations: 0
* Mutations with no coverage: 0
* Test strength: 100%

## Reports

After running:

```powershell
mvn clean verify
```

the JaCoCo report is available at:

```text
target/site/jacoco/index.html
```

After running:

```powershell
mvn -Pmutation-testing -U org.pitest:pitest-maven:1.15.8:mutationCoverage
```

the PIT report is available at:

```text
target/pit-reports/index.html
```

## Main testing results

Current local results:

* Maven verification: successful
* Unit tests: successful
* Integration tests: successful
* PostgreSQL integration tests with Testcontainers: successful
* Swing E2E test: successful
* JaCoCo coverage check: successful
* PIT mutation testing: 100% mutation coverage
* PIT killed mutations: 129 / 129
* PIT survived mutations: 0
* PIT mutations with no coverage: 0
* PIT test strength: 100%

## Notes

Docker Desktop must be running before executing the full verification lifecycle because PostgreSQL integration tests use Testcontainers.

If Docker is not running, the PostgreSQL integration test will fail because Testcontainers cannot start the PostgreSQL container.

## Author

[Tleuliyeva Aida]
