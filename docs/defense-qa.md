\# Defense Q\&A



\# Parcel Locker Management System



\## 1. Purpose of This Document



This document contains possible questions and suggested answers for the defense of the \*\*Parcel Locker Management System\*\* project.



The goal is to help explain the project clearly during the Automated Software Testing exam.



\---



\# General Project Questions



\## Q1. What is the project about?



The project is a Java Swing desktop application for managing parcel lockers.



It allows the user to register customers, create parcels, create locker cells, assign parcels to suitable available locker cells, generate pickup codes, and collect parcels using those pickup codes.



The project is intentionally small, but it contains enough business rules to demonstrate several automated testing techniques.



\---



\## Q2. Why did you choose this domain?



I chose this domain because it is simple to understand but still has meaningful business logic.



The domain includes clear entities and rules:



\* customers;

\* parcels;

\* locker cells;

\* parcel sizes;

\* pickup codes;

\* assignment rules;

\* collection rules.



This makes it suitable for demonstrating unit tests, integration tests, UI tests, database tests, code coverage, and mutation testing.



\---



\## Q3. What are the main features of the application?



The main features are:



\* register customers;

\* create locker cells;

\* create parcels;

\* assign parcels to available locker cells;

\* generate pickup codes;

\* collect parcels using valid pickup codes;

\* release locker cells after parcel collection;

\* display customers, parcels, and locker cells in Swing tables.



\---



\## Q4. What is the main workflow?



The main workflow is:



```text

Register customer

→ Create locker cell

→ Create parcel

→ Assign parcel to locker cell

→ Generate pickup code

→ Collect parcel

→ Release locker cell

```



This workflow can be performed manually through the Swing UI and is also verified by automated tests.



\---



\# Domain Model Questions



\## Q5. What are the main domain entities?



The main domain entities are:



```text

Customer

Parcel

LockerCell

```



Additional domain types are:



```text

Size

ParcelStatus

```



\---



\## Q6. What is the relationship between Customer and Parcel?



A customer can have multiple parcels.



Each parcel belongs to one customer.



This represents a one-to-many relationship:



```text

Customer 1 → many Parcels

```



\---



\## Q7. What is the relationship between Parcel and LockerCell?



A parcel can be assigned to one locker cell.



A locker cell can hold one parcel at a time.



After the parcel is collected, the locker cell becomes available again.



\---



\## Q8. What parcel statuses exist?



The parcel statuses are:



```text

CREATED

ASSIGNED

COLLECTED

```



`CREATED` means the parcel exists but is not assigned to a locker cell yet.



`ASSIGNED` means the parcel is stored in a locker cell and has a pickup code.



`COLLECTED` means the parcel has been collected by the customer.



\---



\## Q9. What sizes are supported?



The supported sizes are:



```text

SMALL

MEDIUM

LARGE

```



Both parcels and locker cells have a size. A parcel can only be assigned to a locker cell with the same size.



\---



\# Business Rule Questions



\## Q10. What are the most important business rules?



The most important business rules are:



1\. A parcel can only be assigned to an available locker cell.

2\. The parcel size must match the locker cell size.

3\. A parcel can only be collected with a valid pickup code.

4\. After collection, the locker cell becomes available again.

5\. Customer phone numbers must be unique.

6\. Locker cell numbers must be unique.

7\. Parcel tracking numbers must be unique.



\---



\## Q11. What happens after a parcel is collected?



After a parcel is collected:



\* the parcel status becomes `COLLECTED`;

\* the pickup code is removed;

\* the parcel is no longer assigned to a locker cell;

\* the locker cell becomes available again.



This rule is tested in unit tests, application service tests, and the Swing E2E test.



\---



\## Q12. What happens when the pickup code is wrong?



The parcel cannot be collected.



The service throws a `BusinessException`.



The UI catches this exception and shows an error dialog to the user.



\---



\## Q13. What happens when there is no suitable locker cell?



The parcel cannot be assigned.



The application checks for an available locker cell with the same size as the parcel.



When no matching locker cell exists, the application throws a `BusinessException`.



\---



\# Architecture Questions



\## Q14. What architecture does the project use?



The project uses a layered architecture:



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



Each layer has a clear responsibility.



\---



\## Q15. Why does the UI not access repositories directly?



The UI should only handle user interaction.



It should not manage transactions, repositories, or database logic.



The UI calls the application service, and the application service coordinates repositories, transactions, and business logic.



This makes the project easier to test and maintain.



\---



\## Q16. What is the role of the application service?



The application service represents application use cases.



For example:



\* register customer;

\* create locker cell;

\* create parcel;

\* assign parcel;

\* collect parcel;

\* find all customers;

\* find all parcels;

\* find all locker cells.



It coordinates transactions, repositories, and domain services.



\---



\## Q17. What is the role of the domain service?



The domain service contains core business operations that do not belong to one repository.



The main domain service is:



```text

ParcelLockerService

```



It handles:



\* assigning parcels;

\* checking locker cell availability;

\* checking size compatibility;

\* generating pickup codes;

\* collecting parcels;

\* releasing locker cells.



\---



\## Q18. What is the role of repositories?



Repositories are responsible for database access.



They hide JPA queries from the rest of the application.



The project has repositories for:



```text

CustomerRepository

ParcelRepository

LockerCellRepository

```



\---



\## Q19. Why do you use DTOs?



The UI receives DTOs instead of directly working with JPA entities.



DTOs used in the project:



```text

CustomerView

ParcelView

LockerCellView

```



This makes the UI simpler and avoids problems with lazy-loaded entity relationships.



It also prevents the UI from accidentally changing domain entities.



\---



\## Q20. How are transactions handled?



Transactions are handled by `TransactionManager`.



It opens an `EntityManager`, starts a transaction, executes the operation, commits the transaction on success, rolls back on error, and closes the `EntityManager`.



This keeps transaction handling centralized.



\---



\# Database Questions



\## Q21. What database does the application use?



For normal desktop execution, the application uses an H2 file-based database.



The local database is stored in:



```text

./data/parcel-locker-db

```



For tests, the project uses both H2 in-memory databases and PostgreSQL through Testcontainers.



\---



\## Q22. Why do you use H2?



H2 is used because it is fast and easy to start.



It is useful for:



\* repository integration tests;

\* application service tests;

\* Swing E2E tests.



Each test can use a separate in-memory database, which keeps tests isolated and repeatable.



\---



\## Q23. Why do you also use PostgreSQL with Testcontainers?



H2 is useful, but it is not exactly the same as PostgreSQL.



Some database behavior can differ between H2 and PostgreSQL.



Testcontainers allows the project to start a real PostgreSQL database in Docker during integration tests.



This gives more confidence that the persistence layer works with a real database.



\---



\## Q24. What does RepositoryPostgresIT test?



`RepositoryPostgresIT` verifies that the repository and persistence layer work with a real PostgreSQL database.



It checks that:



\* Hibernate can create the schema;

\* entities can be saved;

\* repositories work correctly;

\* transactions work correctly;

\* relationships are handled correctly.



\---



\## Q25. Why are there warnings like "table does not exist, skipping"?



These warnings are caused by Hibernate schema generation in `create-drop` mode.



Hibernate first tries to drop old tables and sequences before creating new ones.



In a fresh Testcontainers PostgreSQL database, those tables and sequences do not exist yet, so PostgreSQL reports that they are skipped.



This is not a test failure.



The build is successful when Maven ends with:



```text

BUILD SUCCESS

```



\---



\# Testing Questions



\## Q26. What testing techniques are used in the project?



The project uses:



\* unit testing;

\* H2 integration testing;

\* PostgreSQL integration testing with Testcontainers;

\* application service testing;

\* Swing end-to-end testing;

\* code coverage with JaCoCo;

\* mutation testing with PIT.



\---



\## Q27. What do the unit tests check?



The unit tests check the core business rules in isolation.



They verify:



\* parcel assignment;

\* locker cell availability;

\* size matching;

\* pickup code validation;

\* parcel collection;

\* locker cell release after collection.



\---



\## Q28. What do the application service tests check?



Application service tests check complete use cases.



They verify that the application can:



\* register a customer;

\* create a locker cell;

\* create a parcel;

\* assign a parcel;

\* collect a parcel.



They also test invalid cases like duplicate phone numbers and missing available locker cells.



\---



\## Q29. What do repository integration tests check?



Repository integration tests check database persistence behavior.



They verify:



\* saving entities;

\* finding entities;

\* querying by business fields;

\* relationships between entities;

\* available locker cell search.



\---



\## Q30. What does the Swing E2E test check?



The Swing E2E test opens the UI and performs the full workflow:



```text

Register customer

Create locker cell

Create parcel

Assign parcel

Collect parcel

Verify final state

```



It checks that the graphical interface is correctly connected to the application logic.



\---



\## Q31. Why did you use AssertJ Swing?



AssertJ Swing allows automated testing of Java Swing interfaces.



It can find UI components by name, enter text, click buttons, select tabs, and verify tables.



This is useful because the project is a desktop application, not a web application.



\---



\## Q32. Why are component names important in Swing tests?



Swing components need stable names so that the test can find them reliably.



Examples:



```text

customerNameField

registerCustomerButton

parcelsTable

mainTabbedPane

```



Without stable component names, UI tests would be more fragile.



\---



\## Q33. Why are dialogs disabled in the E2E test?



Success and error dialogs are useful for real users, but they make automated UI tests slower and more fragile.



The UI has a constructor that disables dialogs during tests:



```text

new MainFrame(applicationService, false)

```



The normal application constructor still shows dialogs.



\---



\# JaCoCo Questions



\## Q34. What is JaCoCo used for?



JaCoCo is used to measure code coverage.



It shows which parts of the code were executed by tests.



The report is generated with:



```bash

mvn -U clean verify

```



The report is available at:



```text

target/site/jacoco/index.html

```



\---



\## Q35. What coverage did the project achieve?



The current JaCoCo report shows approximately:



```text

Instruction coverage: 87%

Branch coverage: 52%

```



This means most instructions are executed by tests, while branch coverage still has room for improvement.



\---



\## Q36. Why is branch coverage lower than instruction coverage?



Branch coverage requires testing alternative paths, such as both true and false outcomes of conditions.



It is usually harder to achieve high branch coverage than line or instruction coverage.



In this project, branch coverage can be improved by adding more tests for invalid input and error paths.



\---



\## Q37. Is high coverage proof that the application is correct?



No.



Code coverage only shows that code was executed.



It does not prove that the tests checked the correct behavior.



That is why the project also uses mutation testing with PIT.



\---



\# PIT Mutation Testing Questions



\## Q38. What is mutation testing?



Mutation testing changes the application code in small ways and runs the tests again.



When tests fail, the mutation is killed.



When tests still pass, the mutation survives.



This checks whether tests can detect incorrect behavior.



\---



\## Q39. Why did you use PIT?



PIT was used to evaluate the quality of the tests.



Normal coverage answers:



```text

Was this code executed?

```



Mutation testing answers:



```text

Would the tests detect incorrect behavior?

```



This makes PIT useful for assessing test strength.



\---



\## Q40. What mutation testing result did the project achieve?



The current PIT result is approximately:



```text

Generated mutations: 104

Killed mutations: 74

Mutation coverage: 71%

Test strength: 87%

```



This shows that the test suite detects many behavioral changes.



\---



\## Q41. Why is mutation coverage not 100%?



Mutation coverage is not 100% because some mutants survived or were not covered.



This can happen when:



\* some branches are not fully tested;

\* some DTO or UI-related code has simple getters or return values;

\* some behavior is not critical enough to be covered deeply;

\* some equivalent or near-equivalent mutants are difficult to kill.



For this project, the result is acceptable because mutation testing was used to evaluate and improve the test suite, not to artificially reach 100%.



\---



\## Q42. Why not run mutation testing on the full UI and Docker tests?



Mutation testing repeats tests many times.



Swing UI tests and Testcontainers tests are slower and more expensive to repeat.



Therefore, mutation testing is focused mainly on the domain, service, and application layers, where business logic is concentrated.



\---



\# Maven and Build Questions



\## Q43. What is the difference between `mvn test` and `mvn verify`?



`mvn test` runs the standard test suite using Maven Surefire.



It runs tests named like:



```text

\*Test

```



`mvn verify` runs the full verification lifecycle.



It includes standard tests, integration tests with Maven Failsafe, and JaCoCo report generation.



Integration tests are named like:



```text

\*IT

```



\---



\## Q44. Why do you use Maven Surefire and Failsafe separately?



Surefire is used for standard tests.



Failsafe is used for integration tests.



This separation is useful because heavier tests, such as PostgreSQL Testcontainers tests, should run during the integration-test/verify phases rather than during the normal test phase.



\---



\## Q45. Why is RepositoryPostgresIT named with IT?



The `IT` suffix is used for integration tests that are executed by Maven Failsafe.



This separates it from standard tests executed by Maven Surefire.



\---



\## Q46. What command should be used to run the full project verification?



The command is:



```bash

mvn -U clean verify

```



On Windows, when Docker detection needs explicit configuration:



```powershell

$env:DOCKER\_HOST="npipe:////./pipe/docker\_engine"

$env:DOCKER\_API\_VERSION="1.44"

mvn -U clean verify

```



\---



\# Design Trade-Off Questions



\## Q47. Why did you use Java Swing instead of a web UI?



The exam project was designed as a Java desktop application.



Swing is simple enough for a small project and allows the project to demonstrate desktop UI testing with AssertJ Swing.



The focus is not on modern UI design, but on automated testing techniques.



\---



\## Q48. Why did you not use Spring Boot?



Spring Boot would add more infrastructure and complexity.



For this exam project, a lighter architecture with plain Java, Swing, JPA/Hibernate, and Maven is enough.



The goal is to demonstrate testing techniques, not to build a large enterprise application.



\---



\## Q49. Why did you not use a production connection pool?



The project is a small desktop exam application.



Hibernate's built-in connection handling is enough for the current scope.



A production application could use a proper connection pool such as HikariCP.



\---



\## Q50. What are the main limitations of the project?



The main limitations are:



\* no authentication;

\* no user roles;

\* no advanced search;

\* no advanced validation UI;

\* no database migrations;

\* no production connection pool;

\* no real carrier integration;

\* limited error-path UI testing.



These limitations are acceptable because the project focuses on automated software testing techniques.



\---



\# Improvement Questions



\## Q51. How could the project be improved?



Possible improvements:



\* add more validation tests;

\* improve branch coverage;

\* improve mutation coverage;

\* add database migration tool such as Flyway;

\* add better UI validation messages;

\* add search and filtering;

\* add user roles;

\* add export functionality;

\* add CI pipeline with GitHub Actions;

\* add more PostgreSQL integration tests.



\---



\## Q52. What would you improve first?



The first improvement would be to add more tests for invalid input and error paths.



This would improve branch coverage and mutation coverage.



For example:



\* invalid customer ID;

\* invalid parcel ID;

\* duplicate tracking number;

\* wrong pickup code through the UI;

\* assigning already collected parcels;

\* assigning already assigned parcels.



\---



\## Q53. How would CI be added later?



A GitHub Actions workflow can be added to run Maven tests automatically on each push and pull request.



The workflow should run:



```bash

mvn -B clean verify

```



Because the project has Swing E2E tests, CI should use a virtual display such as `xvfb-run`.



\---



\# Final Summary Answer



\## Q54. How would you summarize the project?



This project is a small Java Swing desktop application with a clear parcel locker workflow and a strong automated testing strategy.



It includes a layered architecture, JPA/Hibernate persistence, H2 and PostgreSQL database testing, unit tests, integration tests, Swing end-to-end tests, JaCoCo code coverage, and PIT mutation testing.



The goal was not to build a production parcel locker system, but to demonstrate automated software testing techniques on a realistic and understandable domain.



