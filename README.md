# Example of Component Testing in Java

This is an example project showcasing how a microservice can be tested in Java.

## Motivation

A microservice has dependencies to databases and external services.
When a microservice is changed, it is important to know if the functionality provided to the end user still works as expected.
One way to achieve that is end-to-end testing.
This testing method spawns the whole application stack and tests the system as a whole.

It has several disadvantages.
* The startup of the application stack is slow and resource intensive, making it infeasible to run the tests for small changes.
* Failed tests can not necessarily be assigned to an individual service, causing a lack of responsibility for failed tests.
* It might not be possible to spawn all required dependencies.

Component testing aims to not have this disadvantages.
This testing method only focuses on a single microservice, eliminating the need to spawn the whole application stack.
Instead, only direct dependencies are spawned.
These direct dependencies can be either mocked (often the case for external services) or unmocked (often the case for databases).

As a consequence, it becomes feasible to run the tests for single changes on developer machines and on a CI server.
This increases developer's confidence in their changes allowing them to deploy changes earlier, in smaller chunks and more frequently.

## Application

The application under test offers an HTTP API to execute runs creating items in an external service.
It uses a `PostgreSQL` database for the persistence of runs.

The application uses several libraries to simplify the implementation.
* The framework `Dropwizard` with `Jersey` to expose endpoints
* The library `liquibase` for the database schema creation 
* The library `JDBI` to execute queries against the database
* The library `retrofit` to execute requests against the external service

## Test Strategy

The tests focus on three different parts of the application.
1. The schema creation of the database and the application's interface to the database
2. The business logic calling the external service
3. The logic of the endpoints and how they affect each other

### Database Tests

The first aspect is tested in the [RunDAOTest](src/test/java/RunDAOTest.java).
This test utilizes `testcontainers` to spawn a docker container with a `PostgreSQL` database.
It runs the `liquibase` schema migration of the application on the spawned database and tests the defined queries of the application's DAO.

### Business Logic and External Service Tests

The second aspect is tested in the [RunServiceTest](src/test/java/RunServiceTest.java).
This test utilizes `testcontainers` to spawn a docker container with a mock server.
It executes the business logic of the application and verifies that the application sends the expected requests to the mock of the external service.

### Resource Tests

The third aspect is tested in the [RunResourceTest](src/test/java/RunResourceTest.java).
This test utilizes `testcontainers` to spawn a docker container with a `PostgreSQL` database.
In contrast to the database tests, it uses a globally spawned docker container instead of a dedicated one.
It uses `Mockito` to mock the business logic of the application.
It calls the endpoints and verifies that they return the expected responses.