\# Testing Strategy



\# Parcel Locker Management System



\## 1. Purpose of This Document



This document describes the automated testing strategy used in the \*\*Parcel Locker Management System\*\* project.



The goal of the testing strategy is to demonstrate different levels of automated software testing in a small but realistic Java Swing desktop application.



The project includes:



\* unit tests;

\* integration tests with H2;

\* integration tests with PostgreSQL using Testcontainers;

\* application service tests;

\* Swing end-to-end tests;

\* code coverage with JaCoCo;

\* mutation testing with PIT.



\---



\## 2. Testing Goals



The main goals of the test suite are:



1\. Verify the correctness of business rules.

2\. Verify persistence behavior with JPA/Hibernate.

3\. Verify that repositories correctly store and retrieve entities.

4\. Verify that the application service correctly coordinates use cases.

5\. Verify that the Swing UI is connected to the application logic.

6\. Verify behavior against a real PostgreSQL database.

7\. Measure code coverage.

8\. Evaluate test quality using mutation testing.



The project is intentionally small, but it is designed to contain enough business logic to make testing meaningful.



\---



\## 3. Test Pyramid



The project follows a practical test pyramid approach.



```text

&#x20;          End-to-End UI Tests

&#x20;       PostgreSQL Integration Tests

&#x20;      H2 Repository Integration Tests

&#x20;     Application Service Integration Tests

&#x20;            Unit Tests

```



The lower levels contain faster tests that check business logic and application behavior.



The higher levels contain slower tests that verify the complete system flow and real database behavior.



\---



\## 4. Unit Testing



\## 4.1 Purpose



Unit tests verify the core business rules without starting the UI or a real database.



These tests are fast and focus on the domain and service layer.



\## 4.2 Tested Components



Main tested class:



```text

ParcelLockerService

```



Related domain classes:



```text

Parcel

LockerCell

Customer

```



\## 4.3 Examples of Tested Rules



The unit tests verify that:



\* a parcel can be assigned to an available locker cell;

\* a parcel cannot be assigned to an occupied locker cell;

\* a parcel cannot be assigned to a locker cell with a different size;

\* a parcel can only be collected with a valid pickup code;

\* a locker cell becomes available after parcel collection;

\* a collected parcel no longer has an active pickup code;

\* a collected parcel is no longer assigned to a locker cell.



\## 4.4 Value of Unit Tests



Unit tests provide fast feedback and make sure that the most important business rules are correct.



They are useful during development because they can be executed quickly with:



```bash

mvn -U clean test

```



\---



\## 5. Application Service Tests



\## 5.1 Purpose



Application service tests verify complete use cases at the application layer.



The application service coordinates:



\* transactions;

\* repositories;

\* domain services;

\* entity state changes.



Main tested class:



```text

ParcelLockerApplicationService

```



\## 5.2 Tested Scenarios



The tests verify that the user can complete the main business workflow:



```text

Register customer

Create locker cell

Create parcel

Assign parcel

Collect parcel

```



They also verify validation and error scenarios, such as:



\* duplicate customer phone number;

\* duplicate locker cell number;

\* assigning a parcel when no suitable locker cell exists.



\## 5.3 Database Used



Application service tests use an H2 in-memory database.



This keeps tests fast while still verifying JPA/Hibernate persistence behavior.



\---



\## 6. Repository Integration Tests with H2



\## 6.1 Purpose



Repository integration tests verify persistence behavior using JPA/Hibernate.



They check that entities can be saved, retrieved, and queried correctly.



\## 6.2 Tested Repositories



The following repositories are tested:



```text

CustomerRepository

ParcelRepository

LockerCellRepository

```



\## 6.3 What Is Verified



The repository tests verify:



\* saving customers;

\* saving parcels;

\* saving locker cells;

\* querying by phone number;

\* querying by tracking number;

\* finding available locker cells by size;

\* entity relationships between customers and parcels;

\* entity relationships between parcels and locker cells.



\## 6.4 Why H2 Is Used



H2 is used for fast local integration tests.



Advantages:



\* starts quickly;

\* does not require Docker;

\* allows isolated in-memory databases;

\* makes tests repeatable.



However, H2 is not exactly the same as PostgreSQL, so the project also includes Testcontainers-based PostgreSQL tests.



\---



\## 7. PostgreSQL Integration Tests with Testcontainers



\## 7.1 Purpose



PostgreSQL integration tests verify that the persistence layer works with a real PostgreSQL database.



This is important because H2 and PostgreSQL may behave differently in some SQL and schema-generation cases.



\## 7.2 Tool Used



The project uses:



```text

Testcontainers

PostgreSQL Docker container

```



\## 7.3 Tested Class



Main PostgreSQL integration test:



```text

RepositoryPostgresIT

```



\## 7.4 What Is Verified



The PostgreSQL integration tests verify that:



\* Hibernate can create the schema in PostgreSQL;

\* entities can be persisted in PostgreSQL;

\* repositories work with PostgreSQL;

\* transactions work with a real database;

\* entity relationships are correctly handled in PostgreSQL.



\## 7.5 Running PostgreSQL Integration Tests



The PostgreSQL integration tests are executed during:



```bash

mvn -U clean verify

```



On Windows, if Testcontainers cannot detect Docker automatically, the following environment variables can be used:



```powershell

$env:DOCKER\_HOST="npipe:////./pipe/docker\_engine"

$env:DOCKER\_API\_VERSION="1.44"

mvn -U clean verify

```



\## 7.6 Value of Testcontainers



Testcontainers makes the test environment closer to production because it uses a real PostgreSQL database instead of an embedded database.



This improves confidence that the application works outside of the local H2 test environment.



\---



\## 8. Swing End-to-End UI Testing



\## 8.1 Purpose



The project includes an end-to-end test for the Swing user interface.



The goal is to verify that the UI is correctly connected to the application service and that a user can complete the main workflow through the graphical interface.



\## 8.2 Tool Used



The project uses:



```text

AssertJ Swing

```



\## 8.3 Tested Class



Main UI test:



```text

MainFrameE2ETest

```



\## 8.4 Tested Scenario



The test opens the Swing window and performs the following workflow:



```text

Register customer

Create locker cell

Create parcel

Assign parcel

Collect parcel

Verify final state

```



\## 8.5 What Is Verified



The UI end-to-end test verifies that:



\* the main frame opens successfully;

\* text fields accept input;

\* buttons trigger the correct application actions;

\* tables are updated after actions;

\* the parcel can be assigned through the UI;

\* the parcel can be collected through the UI;

\* the final domain state is correct.



\## 8.6 Why Dialogs Are Disabled in Tests



The UI contains success and error dialogs for normal user interaction.



For end-to-end tests, dialogs can be disabled through a constructor parameter:



```text

new MainFrame(applicationService, false)

```



This makes the UI test easier to automate because the test does not need to close popup dialogs after each action.



\---



\## 9. Code Coverage with JaCoCo



\## 9.1 Purpose



JaCoCo is used to measure how much of the code is executed by automated tests.



Coverage is not treated as proof of correctness, but it helps identify untested areas.



\## 9.2 Running JaCoCo



The JaCoCo report is generated with:



```bash

mvn -U clean verify

```



\## 9.3 Report Location



The HTML report is generated at:



```text

target/site/jacoco/index.html

```



\## 9.4 What the Report Shows



The report includes:



\* instruction coverage;

\* branch coverage;

\* line coverage;

\* method coverage;

\* class coverage.



\## 9.5 Interpretation



Line coverage shows whether lines of code were executed.



Branch coverage shows whether alternative paths, such as `if` conditions, were tested.



Branch coverage is usually harder to maximize than line coverage because it requires testing both success and failure paths.



\---



\## 10. Mutation Testing with PIT



\## 10.1 Purpose



Mutation testing checks the quality of the test suite.



Unlike normal coverage, mutation testing modifies the application code and checks whether the tests detect the behavioral change.



If a test fails after a mutation, the mutant is considered killed.



If all tests still pass, the mutant survived.



\## 10.2 Tool Used



The project uses:



```text

PIT Mutation Testing

```



\## 10.3 Running PIT



Mutation testing can be executed with:



```bash

mvn -U org.pitest:pitest-maven:mutationCoverage

```



\## 10.4 Report Location



The PIT HTML report is generated at:



```text

target/pit-reports/index.html

```



\## 10.5 Mutation Testing Scope



Mutation testing is focused on the following layers:



\* domain layer;

\* service layer;

\* application layer.



The Swing UI tests and PostgreSQL Testcontainers tests are not the main focus of mutation testing because they are slower and less suitable for repeated mutation execution.



\## 10.6 Why Mutation Testing Is Useful



Mutation testing helps answer a deeper question:



```text

Do the tests really detect wrong behavior?

```



This is stronger than only asking:



```text

Did the tests execute the code?

```



For this reason, PIT is useful for evaluating the quality of the test suite.



\---



\## 11. Test Commands Summary



\## 11.1 Run Standard Tests



```bash

mvn -U clean test

```



Runs:



\* unit tests;

\* H2 integration tests;

\* application service tests;

\* Swing end-to-end tests.



\## 11.2 Run Full Verification



```bash

mvn -U clean verify

```



Runs:



\* standard tests;

\* PostgreSQL Testcontainers integration tests;

\* JaCoCo report generation.



\## 11.3 Run Mutation Testing



```bash

mvn -U org.pitest:pitest-maven:mutationCoverage

```



Runs:



\* PIT mutation testing.



\---



\## 12. Test Categories



The project uses the following naming convention:



```text

\*Test  -> standard unit/integration tests

\*IT    -> integration tests executed by Maven Failsafe

```



Examples:



```text

ParcelLockerServiceTest

ParcelLockerApplicationServiceTest

RepositoryIntegrationTest

MainFrameE2ETest

RepositoryPostgresIT

```



This separation allows Maven Surefire and Maven Failsafe to run different types of tests at the correct lifecycle phases.



\---



\## 13. Maven Test Lifecycle



The project uses Maven lifecycle phases as follows:



\## 13.1 Test Phase



The `test` phase is handled by Maven Surefire.



It runs tests named:



```text

\*Test

```



\## 13.2 Integration Test and Verify Phases



The `integration-test` and `verify` phases are handled by Maven Failsafe.



They run integration tests named:



```text

\*IT

```



This is used for the PostgreSQL Testcontainers test.



\---



\## 14. Test Data Isolation



The tests use isolated databases.



H2 tests use unique in-memory database names generated for each test setup.



This prevents test data from leaking between tests.



PostgreSQL integration tests use a temporary PostgreSQL Docker container.



The container is created for the test and removed afterwards by Testcontainers.



\---



\## 15. Manual Testing



In addition to automated tests, the main workflow was also manually verified through the Swing UI.



The manually tested workflow was:



```text

Register customer

Create locker cell

Create parcel

Assign parcel

Collect parcel

```



Expected final state:



```text

Parcel status: COLLECTED

Locker cell occupied: false

Pickup code: empty

Assigned cell: empty

```



Manual testing is not a replacement for automated testing, but it helps confirm that the application is usable from the user's point of view.



\---



\## 16. Known Testing Limitations



The testing strategy is strong for a small exam project, but it still has limitations:



\* not all UI validation paths are tested automatically;

\* not every possible invalid input is tested;

\* branch coverage can still be improved;

\* mutation coverage can still be improved;

\* performance testing is not included;

\* concurrency testing is not included;

\* security testing is not relevant for the current desktop-only scope.



These limitations are acceptable because the project focuses on demonstrating a broad and meaningful automated testing strategy.



\---



\## 17. Conclusion



The project uses multiple testing techniques to verify the correctness of the application.



The testing strategy covers:



\* isolated business logic;

\* application-level use cases;

\* repository persistence;

\* real PostgreSQL database behavior;

\* Swing UI workflow;

\* code coverage;

\* mutation testing.



This provides confidence that the main business workflow works correctly and that the test suite can detect many types of defects.



