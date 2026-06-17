\# Demo Script



\# Parcel Locker Management System



\## 1. Purpose of This Document



This document provides a short demo script for presenting the \*\*Parcel Locker Management System\*\* project during the Automated Software Testing exam.



The goal is to demonstrate:



\* the application functionality;

\* the main business workflow;

\* the automated test suite;

\* the PostgreSQL Testcontainers integration tests;

\* the JaCoCo coverage report;

\* the PIT mutation testing report;

\* the project documentation.



\---



\## 2. Recommended Demo Duration



Recommended duration:



```text

5–7 minutes

```



The demo should be focused and practical.



The main idea is to show that the project is not only implemented, but also properly tested.



\---



\## 3. Before the Demo



Before starting the demo, make sure that:



\* Docker Desktop is running;

\* Java 17 is installed;

\* Maven is installed;

\* the project opens correctly in the IDE;

\* the application can be started;

\* `mvn -U clean test` passes;

\* `mvn -U clean verify` passes;

\* JaCoCo report exists;

\* PIT report exists.



Useful paths:



```text

target/site/jacoco/index.html

target/pit-reports/index.html

docs/

README.md

```



\---



\## 4. Project Introduction



Suggested explanation:



```text

This project is a Java Swing desktop application called Parcel Locker Management System.



It allows a user to manage customers, parcels, and locker cells. The main workflow is to register a customer, create a parcel, assign it to a suitable locker cell, generate a pickup code, and then collect the parcel using that code.



The project was designed to be simple enough to implement properly, but with enough business rules to demonstrate different automated testing techniques.

```



\---



\## 5. Show the Project Structure



Open the project in the IDE and briefly show the package structure:



```text

src/main/java/com/example/parcellocker

```



Mention the main packages:



```text

application

domain

persistence

repository

service

ui

```



Suggested explanation:



```text

The project follows a layered architecture.



The Swing UI does not access the database directly. It calls the application service. The application service coordinates transactions, repositories, and domain services.



This separation makes the project easier to test.

```



\---



\## 6. Show the Domain Model



Open the domain package:



```text

com.example.parcellocker.domain

```



Show:



```text

Customer

Parcel

LockerCell

Size

ParcelStatus

```



Suggested explanation:



```text

The domain model has three main entities: Customer, Parcel, and LockerCell.



A customer can have multiple parcels. A parcel can be assigned to a locker cell. The locker cell has a size and can be occupied or available.

```



\---



\## 7. Explain the Main Business Rules



Mention the main rules:



```text

A parcel can only be assigned to an available locker cell.

The parcel size must match the locker cell size.

A parcel can only be collected with a valid pickup code.

After collection, the locker cell becomes available again.

```



Optional extra rules:



```text

Customer phone number must be unique.

Locker cell number must be unique.

Parcel tracking number must be unique.

```



\---



\## 8. Run the Application



Run:



```bash

mvn exec:java "-Dexec.mainClass=com.example.parcellocker.Main"

```



On Windows PowerShell, keep the argument in quotes.



Suggested explanation:



```text

This command starts the Swing desktop application.

The application uses an H2 file-based database for normal desktop execution.

```



\---



\## 9. Manual UI Demo Workflow



Perform the following steps in the UI.



\---



\### Step 1 — Register Customer



Open the \*\*Customers\*\* tab.



Enter:



```text

Full name: Alice Brown

Phone number: +390000000100

```



Click:



```text

Register customer

```



Expected result:



```text

The customer appears in the customers table.

```



\---



\### Step 2 — Create Locker Cell



Open the \*\*Locker Cells\*\* tab.



Enter:



```text

Cell number: A1

Size: SMALL

```



Click:



```text

Create locker cell

```



Expected result:



```text

The locker cell appears in the locker cells table.

Occupied = false

```



\---



\### Step 3 — Create Parcel



Open the \*\*Parcels\*\* tab.



Enter:



```text

Tracking number: TRK-001

Description: Phone charger

Size: SMALL

Customer ID: use the ID from the customers table

```



Click:



```text

Create parcel

```



Expected result:



```text

The parcel appears in the parcels table.

Status = CREATED

```



\---



\### Step 4 — Assign Parcel



Open the \*\*Actions\*\* tab.



Enter:



```text

Parcel ID: use the ID from the parcels table

```



Click:



```text

Assign parcel

```



Expected result:



```text

Status = ASSIGNED

Cell = A1

Pickup code = generated automatically

Locker cell occupied = true

```



\---



\### Step 5 — Collect Parcel



Open the \*\*Actions\*\* tab.



Enter:



```text

Parcel ID: same parcel ID

Pickup code: generated pickup code from the parcels table

```



Click:



```text

Collect parcel

```



Expected result:



```text

Status = COLLECTED

Cell = empty

Pickup code = empty

Locker cell occupied = false

```



Suggested explanation:



```text

This demonstrates the complete business workflow from parcel creation to parcel collection.

```



\---



\## 10. Show Unit Tests



Open:



```text

ParcelLockerServiceTest

```



Suggested explanation:



```text

These unit tests check the core business rules without starting the UI or a real database.



For example, they verify that a parcel cannot be assigned to an occupied locker cell, that the parcel size must match the locker cell size, and that a parcel can only be collected with a valid pickup code.

```



Run:



```bash

mvn -U clean test

```



Expected result:



```text

BUILD SUCCESS

```



Mention:



```text

The standard test suite includes unit tests, H2 integration tests, application service tests, and a Swing end-to-end test.

```



\---



\## 11. Show Application Service Tests



Open:



```text

ParcelLockerApplicationServiceTest

```



Suggested explanation:



```text

These tests verify complete use cases at the application service level.



They use an H2 in-memory database and test scenarios such as registering a customer, creating a locker cell, creating a parcel, assigning it, and collecting it.

```



\---



\## 12. Show Repository Integration Tests



Open:



```text

RepositoryIntegrationTest

```



Suggested explanation:



```text

These tests verify the JPA/Hibernate repository layer with an H2 in-memory database.



They check that entities can be saved, queried, and related correctly.

```



\---



\## 13. Show Swing End-to-End Test



Open:



```text

MainFrameE2ETest

```



Suggested explanation:



```text

This is an end-to-end UI test using AssertJ Swing.



It opens the Swing window, enters data into fields, clicks buttons, and verifies the final state of the application.



The test covers the same workflow that was shown manually: register customer, create locker cell, create parcel, assign parcel, collect parcel.

```



Mention:



```text

Dialogs are disabled during the UI test to make automation stable.

```



\---



\## 14. Show PostgreSQL Testcontainers Test



Open:



```text

RepositoryPostgresIT

```



Suggested explanation:



```text

This integration test uses Testcontainers to start a real PostgreSQL database in Docker.



This is important because H2 is useful for fast tests, but PostgreSQL can behave differently. The Testcontainers test verifies that the persistence layer works with a real PostgreSQL database.

```



Run full verification:



```powershell

$env:DOCKER\_HOST="npipe:////./pipe/docker\_engine"

$env:DOCKER\_API\_VERSION="1.44"

mvn -U clean verify

```



Expected result:



```text

BUILD SUCCESS

```



Mention:



```text

The verify lifecycle runs the standard tests, PostgreSQL integration tests, and generates the JaCoCo report.

```



\---



\## 15. Show JaCoCo Coverage Report



Open:



```text

target/site/jacoco/index.html

```



Suggested explanation:



```text

JaCoCo is used to measure code coverage.



The report shows instruction, branch, line, method, and class coverage.



Coverage is not proof that the program is correct, but it helps identify untested areas.

```



Current result to mention:



```text

Instruction coverage: 87%

Branch coverage: 52%

```



\---



\## 16. Show PIT Mutation Testing Report



Run, if needed:



```bash

mvn -U org.pitest:pitest-maven:mutationCoverage

```



Open:



```text

target/pit-reports/index.html

```



Suggested explanation:



```text

PIT is used for mutation testing.



Mutation testing changes the application code and checks whether the tests detect the change.



This is stronger than normal code coverage because it checks whether the tests can catch incorrect behavior.

```



Current result to mention:



```text

Generated mutations: 104

Killed mutations: 74

Mutation coverage: 71%

Test strength: 87%

```



\---



\## 17. Show Documentation



Show these files:



```text

README.md

docs/user-manual.md

docs/testing-strategy.md

docs/architecture.md

docs/test-results.md

docs/deliverables.md

```



Suggested explanation:



```text

The documentation explains how to run the application, how to use it, how the architecture is structured, what testing strategy is used, and what results were obtained.

```



\---



\## 18. Short Final Summary



Suggested final explanation:



```text

To summarize, this project demonstrates a complete small Java Swing application with a layered architecture and a strong automated testing strategy.



The application includes business rules around parcel assignment, locker cell availability, size matching, pickup code validation, and parcel collection.



The testing strategy includes unit tests, H2 integration tests, application service tests, Swing end-to-end tests, PostgreSQL Testcontainers tests, JaCoCo code coverage, and PIT mutation testing.

```



\---



\## 19. If There Is Only Time for a 3-Minute Demo



Use this shorter version:



```text

1\. Show the running Swing app.

2\. Demonstrate customer → locker cell → parcel → assign → collect.

3\. Show mvn clean verify BUILD SUCCESS.

4\. Open JaCoCo report.

5\. Open PIT report.

6\. Briefly show MainFrameE2ETest and RepositoryPostgresIT.

```



\---



\## 20. Possible Questions and Suggested Answers



\### Why did you use H2 and PostgreSQL?



```text

H2 is used for fast integration tests because it starts quickly and is easy to isolate. PostgreSQL is used with Testcontainers to verify behavior against a real database.

```



\### Why did you use Testcontainers?



```text

Testcontainers allows the project to run integration tests with a real PostgreSQL database in Docker. This makes the persistence tests more realistic than only using H2.

```



\### Why did you use mutation testing?



```text

Code coverage only shows which code was executed. Mutation testing checks whether the tests can detect behavioral changes. This gives a stronger indication of test quality.

```



\### Why is branch coverage lower than instruction coverage?



```text

Branch coverage requires testing all alternative decision paths, such as both true and false branches of conditions. It is usually harder to maximize than line or instruction coverage.

```



\### Why does the UI not access repositories directly?



```text

The UI only calls the application service. This keeps transaction and persistence logic out of the UI and makes the application easier to test.

```



\### Why are dialogs disabled in UI tests?



```text

Dialogs are useful for real users, but they make automated UI tests harder. The MainFrame has a test constructor that disables dialogs to make the E2E test stable.

```



\---



\## 21. Demo Checklist



Before the exam, check:



```text

Docker Desktop is running

mvn -U clean test passes

mvn -U clean verify passes

PIT report exists

JaCoCo report exists

Application starts

Manual workflow works

README and docs are present

```



Recommended final command before the demo:



```powershell

$env:DOCKER\_HOST="npipe:////./pipe/docker\_engine"

$env:DOCKER\_API\_VERSION="1.44"

mvn -U clean verify

```



Expected result:



```text

BUILD SUCCESS

```



