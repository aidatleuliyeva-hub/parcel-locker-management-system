\# Deliverables



\# Parcel Locker Management System



\## 1. Purpose of This Document



This document lists the deliverables prepared for the \*\*Parcel Locker Management System\*\* project.



The project was developed for the Automated Software Testing exam and includes source code, automated tests, documentation, and generated quality reports.



\---



\## 2. Source Code



The project includes the complete Java source code for a Swing desktop application.



Main source directory:



```text

src/main/java

```



Main packages:



```text

com.example.parcellocker.application

com.example.parcellocker.application.dto

com.example.parcellocker.domain

com.example.parcellocker.persistence

com.example.parcellocker.repository

com.example.parcellocker.service

com.example.parcellocker.ui

```



Main application entry point:



```text

com.example.parcellocker.Main

```



Main UI class:



```text

com.example.parcellocker.ui.MainFrame

```



\---



\## 3. Application Features



The delivered application supports the following features:



\* customer registration;

\* locker cell creation;

\* parcel creation;

\* parcel assignment to available locker cells;

\* pickup code generation;

\* parcel collection using pickup codes;

\* locker cell release after parcel collection;

\* displaying customers, parcels, and locker cells in Swing tables.



\---



\## 4. Domain Model



The delivered domain model includes three main entities:



```text

Customer

Parcel

LockerCell

```



Additional domain types:



```text

Size

ParcelStatus

```



The domain model supports the main business workflow:



```text

Customer registration

→ Parcel creation

→ Locker cell assignment

→ Pickup code generation

→ Parcel collection

→ Locker cell release

```



\---



\## 5. Business Rules



The project implements the following business rules:



1\. A parcel can only be assigned to an available locker cell.

2\. A parcel can only be assigned to a locker cell with the same size.

3\. A parcel can only be collected with a valid pickup code.

4\. After collection, the locker cell becomes available again.

5\. A customer phone number must be unique.

6\. A locker cell number must be unique.

7\. A parcel tracking number must be unique.

8\. A collected parcel no longer has an active pickup code.

9\. A collected parcel is no longer assigned to a locker cell.



\---



\## 6. Automated Tests



The project includes automated tests in:



```text

src/test/java

```



The test suite includes:



\* unit tests;

\* application service tests;

\* repository integration tests with H2;

\* Swing end-to-end tests;

\* PostgreSQL integration tests with Testcontainers.



\---



\## 7. Unit Tests



Main unit test class:



```text

ParcelLockerServiceTest

```



Purpose:



\* verify business rules;

\* verify parcel assignment;

\* verify parcel collection;

\* verify pickup code validation;

\* verify locker cell release.



Command:



```bash

mvn -U clean test

```



\---



\## 8. Application Service Tests



Main test class:



```text

ParcelLockerApplicationServiceTest

```



Purpose:



\* verify full application use cases;

\* verify transaction-based operations;

\* verify duplicate validation;

\* verify assignment and collection workflow.



Command:



```bash

mvn -U clean test

```



\---



\## 9. Repository Integration Tests with H2



Main test class:



```text

RepositoryIntegrationTest

```



Purpose:



\* verify JPA/Hibernate persistence;

\* verify repository queries;

\* verify entity relationships;

\* verify transaction behavior.



Database:



```text

H2 in-memory database

```



Command:



```bash

mvn -U clean test

```



\---



\## 10. Swing End-to-End Test



Main test class:



```text

MainFrameE2ETest

```



Purpose:



\* open the Swing UI;

\* enter data through UI fields;

\* click UI buttons;

\* verify the full user workflow;

\* verify final application state.



Tested workflow:



```text

Register customer

Create locker cell

Create parcel

Assign parcel

Collect parcel

Verify final state

```



Command:



```bash

mvn -U clean test

```



\---



\## 11. PostgreSQL Integration Tests with Testcontainers



Main integration test class:



```text

RepositoryPostgresIT

```



Purpose:



\* verify persistence with a real PostgreSQL database;

\* verify Hibernate schema generation on PostgreSQL;

\* verify repositories against PostgreSQL;

\* verify transactions with a real database.



Tool:



```text

Testcontainers

PostgreSQL Docker container

```



Command:



```bash

mvn -U clean verify

```



On Windows, if Docker is not detected automatically:



```powershell

$env:DOCKER\_HOST="npipe:////./pipe/docker\_engine"

$env:DOCKER\_API\_VERSION="1.44"

mvn -U clean verify

```



\---



\## 12. Code Coverage Report



Tool:



```text

JaCoCo

```



Command:



```bash

mvn -U clean verify

```



Report location:



```text

target/site/jacoco/index.html

```



The report includes:



\* instruction coverage;

\* branch coverage;

\* line coverage;

\* method coverage;

\* class coverage.



Current summary:



```text

Instruction coverage: 87%

Branch coverage:      52%

Classes analyzed:     20

Methods covered:      142 / 165

Lines covered:        525 / 598

```



\---



\## 13. Mutation Testing Report



Tool:



```text

PIT Mutation Testing

```



Command:



```bash

mvn -U org.pitest:pitest-maven:mutationCoverage

```



Report location:



```text

target/pit-reports/index.html

```



Current summary:



```text

Generated mutations: 104

Killed mutations:    74

Mutation coverage:   71%

Test strength:       87%

```



Mutation testing is focused on:



\* domain layer;

\* service layer;

\* application layer.



\---



\## 14. Documentation



The project includes the following documentation files:



```text

README.md

docs/user-manual.md

docs/testing-strategy.md

docs/architecture.md

docs/test-results.md

docs/deliverables.md

```



\### README.md



General project overview.



Includes:



\* project description;

\* features;

\* domain model;

\* business rules;

\* architecture overview;

\* technologies;

\* test commands;

\* report locations.



\### docs/user-manual.md



User-oriented manual.



Explains:



\* how to run the application;

\* how to register customers;

\* how to create locker cells;

\* how to create parcels;

\* how to assign parcels;

\* how to collect parcels;

\* how to reset local data.



\### docs/testing-strategy.md



Testing-focused document.



Explains:



\* unit testing;

\* integration testing;

\* UI testing;

\* Testcontainers;

\* JaCoCo;

\* PIT mutation testing;

\* test lifecycle.



\### docs/architecture.md



Architecture explanation.



Explains:



\* layered structure;

\* package responsibilities;

\* transaction flow;

\* DTO usage;

\* repository usage;

\* testability benefits.



\### docs/test-results.md



Summary of current test results.



Includes:



\* standard test results;

\* PostgreSQL integration test results;

\* JaCoCo results;

\* PIT mutation testing results.



\---



\## 15. Build Tool



The project uses:



```text

Maven

```



Main build file:



```text

pom.xml

```



The Maven configuration includes:



\* Java 17 compilation;

\* JUnit 5 testing;

\* Maven Surefire;

\* Maven Failsafe;

\* JaCoCo;

\* PIT;

\* Hibernate;

\* H2;

\* PostgreSQL driver;

\* Testcontainers;

\* AssertJ Swing.



\---



\## 16. How to Run the Application



Command:



```bash

mvn exec:java "-Dexec.mainClass=com.example.parcellocker.Main"

```



On Windows PowerShell, the quotes around the `-Dexec.mainClass` parameter are important.



\---



\## 17. How to Run Standard Tests



Command:



```bash

mvn -U clean test

```



Expected result:



```text

BUILD SUCCESS

```



This command runs:



\* unit tests;

\* H2 integration tests;

\* application service tests;

\* Swing E2E test.



\---



\## 18. How to Run Full Verification



Command:



```bash

mvn -U clean verify

```



Expected result:



```text

BUILD SUCCESS

```



This command runs:



\* standard tests;

\* PostgreSQL Testcontainers integration tests;

\* JaCoCo report generation.



\---



\## 19. How to Run Mutation Testing



Command:



```bash

mvn -U org.pitest:pitest-maven:mutationCoverage

```



Expected result:



```text

BUILD SUCCESS

```



Report location:



```text

target/pit-reports/index.html

```



\---



\## 20. Generated Reports



The following reports can be generated:



\### JaCoCo Report



```text

target/site/jacoco/index.html

```



\### PIT Report



```text

target/pit-reports/index.html

```



\### Surefire Reports



```text

target/surefire-reports

```



\### Failsafe Reports



```text

target/failsafe-reports

```



\---



\## 21. Suggested Submission Content



The final submission should include:



```text

Source code

pom.xml

README.md

docs folder

Automated tests

JaCoCo report

PIT report

Instructions for running the project

```



Recommended files and folders:



```text

src/

pom.xml

README.md

docs/

```



Generated folders such as `target/` are usually not committed to version control, but reports can be exported or shown during the exam if needed.



\---



\## 22. Notes for the Examiner



This project demonstrates the following automated software testing techniques:



\* unit testing;

\* integration testing;

\* database testing;

\* UI end-to-end testing;

\* code coverage analysis;

\* mutation testing.



The application is intentionally small, but it includes a complete business workflow and several business rules that can be verified automatically.



\---



\## 23. Final Deliverables Summary



Final deliverables:



```text

Java Swing desktop application

Layered architecture

JPA/Hibernate persistence

H2 local database

PostgreSQL Testcontainers integration tests

JUnit 5 tests

AssertJ Swing E2E test

JaCoCo coverage report

PIT mutation testing report

Project documentation

```



The project is ready to be demonstrated and explained as an Automated Software Testing exam project.



