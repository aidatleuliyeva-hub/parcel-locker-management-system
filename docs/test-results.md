# Test Results

# Parcel Locker Management System

## 1. Purpose

This document summarizes the final automated testing results for the Parcel Locker Management System project.

The project was verified with unit tests, integration tests, Swing end-to-end tests, PostgreSQL Testcontainers tests, and JaCoCo coverage checks.

## 2. Environment

- Operating system: Windows
- Java version: Java 17
- Build tool: Maven
- Local test database: H2
- Real integration test database: PostgreSQL via Testcontainers
- Docker: Docker Desktop

## 3. Commands Used

Standard test suite:

mvn -U clean test

Full verification:

mvn -U clean verify

Optional PIT mutation testing:

mvn -U org.pitest:pitest-maven:mutationCoverage

PIT is configured as an optional additional quality check. It is not required for the normal mvn clean verify workflow.

## 4. Final Test Results

The final Maven verification completed successfully.

- Standard test phase: 65 tests, 0 failures, 0 errors
- PostgreSQL integration phase: 2 tests, 0 failures, 0 errors
- Total verified tests: 67 tests, 0 failures, 0 errors
- Full verification result: BUILD SUCCESS

## 5. Tested Areas

The automated test suite verifies:

- domain model behavior;
- parcel locker business rules;
- pickup code generation;
- application service use cases;
- JPA/Hibernate repository behavior with H2;
- PostgreSQL persistence using Testcontainers;
- Swing UI workflow with an end-to-end test.

Main test classes include:

- DomainModelTest
- ParcelLockerServiceTest
- PickupCodeGeneratorTest
- ParcelLockerApplicationServiceTest
- RepositoryIntegrationTest
- RepositoryAdditionalTest
- RepositoryPostgresIT
- MainFrameE2ETest

## 6. PostgreSQL Testcontainers Result

The project includes integration tests that run against a real PostgreSQL database inside a Docker container.

- Test class: RepositoryPostgresIT
- Tests run: 2
- Failures: 0
- Errors: 0
- Result: PASS

This confirms that the persistence layer works with PostgreSQL, not only with the H2 in-memory database.

## 7. JaCoCo Coverage Result

JaCoCo was used to generate the coverage report and check the configured coverage rules.

- JaCoCo report: GENERATED
- JaCoCo coverage check: PASS
- Report location: target/site/jacoco/index.html

The Maven output confirms that all coverage checks have been met.

## 8. Notes About Warnings

Some warnings may appear during test execution, for example:

- H2Dialect does not need to be specified explicitly
- PostgreSQLDialect does not need to be specified explicitly
- relation does not exist, skipping
- sequence does not exist, skipping

These warnings do not indicate a test failure.

The PostgreSQL warnings appear because Hibernate creates and drops the test schema. In a fresh Testcontainers database, some tables and sequences do not exist yet, so PostgreSQL reports that they are skipped.

The build is successful only if Maven finishes with BUILD SUCCESS.

## 9. Conclusion

The current test results show that the project has a strong automated testing foundation.

The project demonstrates:

- unit testing;
- integration testing with H2;
- real PostgreSQL integration testing with Testcontainers;
- Swing end-to-end testing;
- code coverage with JaCoCo;
- optional mutation testing support with PIT.

The final verified result is BUILD SUCCESS.