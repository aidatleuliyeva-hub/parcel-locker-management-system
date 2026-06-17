\# Architecture



\# Parcel Locker Management System



\## 1. Purpose of This Document



This document describes the architecture of the \*\*Parcel Locker Management System\*\* project.



The goal of the architecture is to keep the application simple, understandable, and testable.



The project uses a layered architecture where each layer has a clear responsibility.



\---



\## 2. High-Level Architecture



The application is structured into the following layers:



```text

Swing UI

&#x20;  ↓

Application Service

&#x20;  ↓

Domain Service

&#x20;  ↓

Repositories

&#x20;  ↓

JPA / Hibernate

&#x20;  ↓

Database

```



This structure separates user interface logic, business use cases, domain rules, and persistence logic.



\---



\## 3. Package Structure



The main package is:



```text

com.example.parcellocker

```



The project is divided into the following packages:



```text

com.example.parcellocker

├── application

├── application.dto

├── domain

├── persistence

├── repository

├── service

└── ui

```



Each package has a specific role.



\---



\## 4. UI Layer



Package:



```text

com.example.parcellocker.ui

```



Main class:



```text

MainFrame

```



The UI layer contains the Java Swing user interface.



It is responsible for:



\* displaying forms;

\* displaying tables;

\* reading user input;

\* showing success and error messages;

\* calling application service methods.



The UI layer does \*\*not\*\* directly access:



\* repositories;

\* `EntityManager`;

\* Hibernate;

\* database transactions.



This is intentional because the UI should not contain persistence logic.



\---



\## 5. Application Layer



Package:



```text

com.example.parcellocker.application

```



Main class:



```text

ParcelLockerApplicationService

```



The application layer contains use cases of the system.



Examples of use cases:



\* register customer;

\* create locker cell;

\* create parcel;

\* assign parcel to available locker cell;

\* collect parcel;

\* find all customers;

\* find all parcels;

\* find all locker cells.



The application service coordinates:



\* transactions;

\* repositories;

\* domain services;

\* entity loading;

\* DTO creation.



This layer acts as a bridge between the UI and the domain/persistence layers.



\---



\## 6. DTO Layer



Package:



```text

com.example.parcellocker.application.dto

```



DTO classes:



```text

CustomerView

LockerCellView

ParcelView

```



DTOs are used to transfer data from the application service to the UI.



The UI displays DTO objects instead of directly working with JPA entities.



This has several advantages:



\* the UI does not depend on lazy-loaded entity relationships;

\* the UI receives only the data it needs;

\* the domain model remains protected from accidental UI changes;

\* table rendering becomes simpler.



\---



\## 7. Domain Layer



Package:



```text

com.example.parcellocker.domain

```



Main classes:



```text

Customer

Parcel

LockerCell

Size

ParcelStatus

```



The domain layer contains the core business entities.



It represents the main concepts of the system:



\* customers;

\* parcels;

\* locker cells;

\* parcel sizes;

\* parcel statuses.



The domain entities contain state and behavior related to the parcel locker business process.



For example, a parcel can be assigned to a locker cell and later collected.



\---



\## 8. Service Layer



Package:



```text

com.example.parcellocker.service

```



Main classes:



```text

ParcelLockerService

PickupCodeGenerator

BusinessException

```



The service layer contains business logic that does not belong to a single repository.



The main service is:



```text

ParcelLockerService

```



It implements core business operations such as:



\* assigning a parcel to a locker cell;

\* generating a pickup code;

\* collecting a parcel;

\* validating pickup codes;

\* releasing locker cells after collection.



Business rule violations are represented by:



```text

BusinessException

```



This makes error handling explicit and testable.



\---



\## 9. Repository Layer



Package:



```text

com.example.parcellocker.repository

```



Repository classes:



```text

CustomerRepository

ParcelRepository

LockerCellRepository

```



The repository layer is responsible for database access.



Repositories use JPA `EntityManager` to:



\* save entities;

\* find entities by ID;

\* find entities by business fields;

\* query available locker cells;

\* retrieve all records for display.



The repositories hide query details from the application layer.



\---



\## 10. Persistence Layer



Package:



```text

com.example.parcellocker.persistence

```



Main classes:



```text

JpaUtil

TransactionManager

TestJpaFactory

```



The persistence layer contains infrastructure code for JPA and database transactions.



\### JpaUtil



Used by the main application to create and access the production `EntityManagerFactory`.



\### TransactionManager



Wraps application operations in transactions.



It is responsible for:



\* opening an `EntityManager`;

\* starting a transaction;

\* committing the transaction on success;

\* rolling back the transaction on error;

\* closing the `EntityManager`.



\### TestJpaFactory



Used by automated tests to create test-specific `EntityManagerFactory` instances.



It supports:



\* H2 in-memory database tests;

\* PostgreSQL Testcontainers tests.



\---



\## 11. Database Architecture



The application uses JPA/Hibernate for persistence.



For normal desktop execution, the application uses an H2 file-based database:



```text

./data/parcel-locker-db

```



For automated tests, the project uses two database approaches:



\### H2 In-Memory Database



Used for fast tests:



\* application service tests;

\* repository integration tests;

\* Swing E2E tests.



\### PostgreSQL with Testcontainers



Used for real database integration tests.



This ensures that the persistence layer works with PostgreSQL and not only with H2.



\---



\## 12. Transaction Flow



A typical transaction flow looks like this:



```text

User clicks button in Swing UI

&#x20;       ↓

MainFrame calls ParcelLockerApplicationService

&#x20;       ↓

Application service calls TransactionManager

&#x20;       ↓

TransactionManager opens EntityManager and transaction

&#x20;       ↓

Application service uses repositories and domain service

&#x20;       ↓

Entities are modified

&#x20;       ↓

TransactionManager commits transaction

&#x20;       ↓

UI refreshes tables

```



Example: assigning a parcel.



```text

Assign parcel button

&#x20;       ↓

assignParcelToAvailableCell(parcelId)

&#x20;       ↓

Load parcel from ParcelRepository

&#x20;       ↓

Find available locker cell from LockerCellRepository

&#x20;       ↓

ParcelLockerService assigns parcel

&#x20;       ↓

Pickup code is generated

&#x20;       ↓

Transaction is committed

&#x20;       ↓

UI table is refreshed

```



\---



\## 13. Main Workflow



The main business workflow is:



```text

Register customer

&#x20;       ↓

Create locker cell

&#x20;       ↓

Create parcel

&#x20;       ↓

Assign parcel to locker cell

&#x20;       ↓

Generate pickup code

&#x20;       ↓

Collect parcel

&#x20;       ↓

Release locker cell

```



This workflow is implemented through the application service and verified by automated tests.



\---



\## 14. Why the UI Does Not Use Repositories Directly



The UI does not directly use repositories because that would mix different responsibilities.



Bad design example:



```text

Swing UI → EntityManager → Database

```



This would make the UI harder to test and maintain.



The project instead uses:



```text

Swing UI → Application Service → Repositories → Database

```



This design has several advantages:



\* UI code stays simple;

\* business use cases are centralized;

\* transaction handling is not duplicated in UI code;

\* tests can target the application layer without opening the UI;

\* the UI can be tested separately through end-to-end tests.



\---



\## 15. Error Handling Architecture



Business errors are represented by:



```text

BusinessException

```



Examples:



\* customer not found;

\* parcel not found;

\* duplicate phone number;

\* duplicate tracking number;

\* no available locker cell;

\* invalid pickup code.



The application service throws `BusinessException` when a business rule is violated.



The Swing UI catches this exception and displays an error dialog.



This keeps business validation separate from UI presentation.



\---



\## 16. Testability Benefits



The architecture was designed to make the project easy to test.



\### Unit Testing



The service layer can be tested without UI or database.



\### Application Service Testing



The application layer can be tested with an in-memory database.



\### Repository Testing



Repositories can be tested with H2 and PostgreSQL.



\### UI Testing



The Swing UI can be tested using AssertJ Swing because components have stable names.



\### Mutation Testing



The domain, service, and application layers can be tested with PIT mutation testing.



\---



\## 17. Component Names for UI Testing



Swing components are assigned names to support UI automation.



Examples:



```text

mainTabbedPane

customerNameField

customerPhoneField

registerCustomerButton

lockerCellNumberField

createLockerCellButton

parcelTrackingNumberField

createParcelButton

assignParcelButton

collectParcelButton

parcelsTable

```



These names allow AssertJ Swing tests to find and interact with UI components reliably.



\---



\## 18. Dialog Handling for Tests



The main UI normally shows success and error dialogs.



For automated UI tests, dialogs can be disabled with:



```text

new MainFrame(applicationService, false)

```



This avoids unnecessary popup handling in tests and makes the E2E test more stable.



The default constructor still enables dialogs for normal users:



```text

new MainFrame(applicationService)

```



\---



\## 19. Design Trade-Offs



The project intentionally uses a simple architecture.



Some advanced production features are not included:



\* dependency injection framework;

\* Spring Boot;

\* connection pool such as HikariCP;

\* user authentication;

\* advanced validation framework;

\* database migrations with Flyway or Liquibase.



These features were not added because the goal of the project is to demonstrate automated software testing techniques in a clear and manageable Java desktop application.



The architecture is simple enough for an exam project but structured enough to support meaningful tests.



\---



\## 20. Conclusion



The architecture of the Parcel Locker Management System separates the application into clear layers:



```text

UI

Application

Domain

Service

Repository

Persistence

Database

```



This separation improves:



\* maintainability;

\* readability;

\* testability;

\* error handling;

\* transaction management.



The architecture supports the main goal of the project: building a small Java Swing application with a strong automated testing strategy.



