\# User Manual



\# Parcel Locker Management System



\## 1. Introduction



Parcel Locker Management System is a Java Swing desktop application for managing customers, parcels, and parcel locker cells.



The application allows the user to:



\* register customers;

\* create locker cells;

\* create parcels;

\* assign parcels to suitable available locker cells;

\* generate pickup codes;

\* collect parcels using pickup codes;

\* release locker cells after parcel collection.



This manual explains how to run and use the application.



\---



\## 2. System Requirements



To run the application, the following software is required:



\* Java 17

\* Maven

\* Windows, Linux, or macOS



For running PostgreSQL integration tests, Docker is also required.



\---



\## 3. Running the Application



Open a terminal in the project root directory and run:



```bash

mvn exec:java "-Dexec.mainClass=com.example.parcellocker.Main"

```



On Windows PowerShell, the quotes around the `-Dexec.mainClass` argument are important.



After the command runs successfully, the main application window opens.



\---



\## 4. Main Window Overview



The application window contains four tabs:



1\. \*\*Customers\*\*

2\. \*\*Locker Cells\*\*

3\. \*\*Parcels\*\*

4\. \*\*Actions\*\*



Each tab is responsible for a specific part of the parcel locker workflow.



\---



\## 5. Customers Tab



The \*\*Customers\*\* tab is used to register new customers and view existing customers.



\### Fields



\* \*\*Full name\*\* — the customer's full name.

\* \*\*Phone number\*\* — the customer's phone number.



\### Button



\* \*\*Register customer\*\* — creates a new customer.



\### Customer Table



The customer table displays:



\* ID

\* Full name

\* Phone number

\* Number of parcels



\### Example



Enter:



```text

Full name: Alice Brown

Phone number: +390000000100

```



Click:



```text

Register customer

```



The customer appears in the customers table.



\---



\## 6. Locker Cells Tab



The \*\*Locker Cells\*\* tab is used to create and view locker cells.



\### Fields



\* \*\*Cell number\*\* — unique locker cell number.

\* \*\*Size\*\* — locker cell size.



Available sizes:



\* SMALL

\* MEDIUM

\* LARGE



\### Button



\* \*\*Create locker cell\*\* — creates a new locker cell.



\### Locker Cells Table



The locker cells table displays:



\* ID

\* Cell number

\* Size

\* Occupied status



\### Example



Enter:



```text

Cell number: A1

Size: SMALL

```



Click:



```text

Create locker cell

```



The locker cell appears in the locker cells table.



\---



\## 7. Parcels Tab



The \*\*Parcels\*\* tab is used to create parcels and view parcel information.



\### Fields



\* \*\*Tracking number\*\* — unique parcel tracking number.

\* \*\*Description\*\* — short parcel description.

\* \*\*Size\*\* — parcel size.

\* \*\*Customer ID\*\* — ID of the customer who owns the parcel.



Available parcel sizes:



\* SMALL

\* MEDIUM

\* LARGE



\### Button



\* \*\*Create parcel\*\* — creates a new parcel for the selected customer.



\### Parcels Table



The parcels table displays:



\* ID

\* Tracking number

\* Description

\* Size

\* Status

\* Customer

\* Locker cell

\* Pickup code



\### Parcel Statuses



A parcel can have one of the following statuses:



```text

CREATED

ASSIGNED

COLLECTED

```



\### Example



Enter:



```text

Tracking number: TRK-001

Description: Phone charger

Size: SMALL

Customer ID: 1

```



Click:



```text

Create parcel

```



The parcel appears in the parcels table with status:



```text

CREATED

```



\---



\## 8. Actions Tab



The \*\*Actions\*\* tab is used to assign parcels to locker cells and collect parcels.



\---



\## 9. Assigning a Parcel



To assign a parcel to a locker cell:



1\. Open the \*\*Actions\*\* tab.

2\. Enter the parcel ID.

3\. Click \*\*Assign parcel\*\*.



The application searches for an available locker cell with the same size as the parcel.



If a suitable locker cell exists:



\* the parcel status becomes `ASSIGNED`;

\* the locker cell becomes occupied;

\* a pickup code is generated;

\* the pickup code appears in the parcels table.



\### Example



Enter:



```text

Parcel ID: 1

```



Click:



```text

Assign parcel

```



Expected result:



```text

Status: ASSIGNED

Cell: A1

Pickup code: generated automatically

```



\---



\## 10. Collecting a Parcel



To collect a parcel:



1\. Open the \*\*Actions\*\* tab.

2\. Enter the parcel ID.

3\. Enter the pickup code.

4\. Click \*\*Collect parcel\*\*.



If the pickup code is valid:



\* the parcel status becomes `COLLECTED`;

\* the pickup code is removed;

\* the locker cell is released;

\* the locker cell becomes available again.



\### Example



Enter:



```text

Parcel ID: 1

Pickup code: 123456

```



Click:



```text

Collect parcel

```



Expected result:



```text

Status: COLLECTED

Cell: empty

Pickup code: empty

Locker cell occupied: false

```



\---



\## 11. Complete Example Workflow



A typical user workflow is:



\### Step 1 — Register Customer



Open \*\*Customers\*\* tab.



Enter:



```text

Full name: Alice Brown

Phone number: +390000000100

```



Click:



```text

Register customer

```



\### Step 2 — Create Locker Cell



Open \*\*Locker Cells\*\* tab.



Enter:



```text

Cell number: A1

Size: SMALL

```



Click:



```text

Create locker cell

```



\### Step 3 — Create Parcel



Open \*\*Parcels\*\* tab.



Enter:



```text

Tracking number: TRK-001

Description: Phone charger

Size: SMALL

Customer ID: 1

```



Click:



```text

Create parcel

```



\### Step 4 — Assign Parcel



Open \*\*Actions\*\* tab.



Enter:



```text

Parcel ID: 1

```



Click:



```text

Assign parcel

```



The parcel receives a pickup code.



\### Step 5 — Collect Parcel



Open \*\*Actions\*\* tab.



Enter:



```text

Parcel ID: 1

Pickup code: generated pickup code

```



Click:



```text

Collect parcel

```



The parcel is collected and the locker cell becomes available again.



\---



\## 12. Error Handling



The application shows an error message when a business rule is violated.



Possible errors include:



\### Duplicate Customer Phone Number



A customer cannot be registered if another customer already has the same phone number.



\### Duplicate Locker Cell Number



A locker cell cannot be created if another locker cell already has the same cell number.



\### Duplicate Parcel Tracking Number



A parcel cannot be created if another parcel already has the same tracking number.



\### Customer Not Found



A parcel cannot be created if the entered customer ID does not exist.



\### Parcel Not Found



A parcel cannot be assigned or collected if the entered parcel ID does not exist.



\### No Available Locker Cell



A parcel cannot be assigned if there is no available locker cell with the same size.



\### Invalid Pickup Code



A parcel cannot be collected if the pickup code is incorrect.



\---



\## 13. Data Storage



The application uses an H2 file-based database by default.



The local database is stored in:



```text

./data/parcel-locker-db

```



This means that data remains available after closing and reopening the application.



\---



\## 14. Resetting Local Data



To delete all local application data, close the application and remove the `data` folder.



On Windows PowerShell:



```powershell

Remove-Item -Recurse -Force .\\data

```



After running the application again, a new empty database will be created automatically.



\---



\## 15. Running Tests



To run the standard test suite:



```bash

mvn -U clean test

```



This command runs:



\* unit tests;

\* H2 integration tests;

\* application service tests;

\* Swing end-to-end tests.



Expected result:



```text

BUILD SUCCESS

```



\---



\## 16. Running Full Verification



To run full verification:



```bash

mvn -U clean verify

```



This command runs:



\* unit tests;

\* integration tests;

\* Swing end-to-end tests;

\* PostgreSQL Testcontainers tests;

\* JaCoCo coverage report generation.



On Windows, if Docker is not detected by Testcontainers, run:



```powershell

$env:DOCKER\_HOST="npipe:////./pipe/docker\_engine"

$env:DOCKER\_API\_VERSION="1.44"

mvn -U clean verify

```



\---



\## 17. JaCoCo Coverage Report



The JaCoCo coverage report is generated after:



```bash

mvn -U clean verify

```



The report is available at:



```text

target/site/jacoco/index.html

```



The report shows:



\* instruction coverage;

\* branch coverage;

\* line coverage;

\* method coverage;

\* class coverage.



\---



\## 18. PIT Mutation Testing Report



To run mutation testing:



```bash

mvn -U org.pitest:pitest-maven:mutationCoverage

```



The PIT report is generated at:



```text

target/pit-reports/index.html

```



Mutation testing checks whether the test suite can detect behavioral changes in the code.



\---



\## 19. Notes for the Examiner



The application demonstrates several automated testing techniques:



\* unit testing;

\* integration testing;

\* end-to-end Swing UI testing;

\* real PostgreSQL testing with Testcontainers;

\* code coverage with JaCoCo;

\* mutation testing with PIT.



The system is intentionally simple, but it contains enough business rules to demonstrate meaningful testing scenarios.



\---



\## 20. Conclusion



Parcel Locker Management System provides a complete small-scale desktop application with a clear business workflow and a strong automated testing strategy.



The main workflow is:



```text

Customer registration

→ Locker cell creation

→ Parcel creation

→ Parcel assignment

→ Pickup code generation

→ Parcel collection

→ Locker cell release

```



This workflow is implemented in the application and verified by automated tests.



