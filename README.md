# Omnia

**A modular Java infrastructure and backend toolkit for Spring-based applications.**

Omnia is a Maven multi-module project that packages reusable backend building blocks for common infrastructure concerns such as persistence, messaging, caching, logging, cryptography, storage, API clients, and MVC/reactive application support.

It is designed around **Java 21** and **Spring Boot 3.4.x**, with modules that can be developed and maintained independently while sharing a common parent build and dependency baseline.

![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.x-6DB33F?logo=springboot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9.9%2B-C71A36?logo=apachemaven&logoColor=white)

## What Omnia Provides

Omnia groups infrastructure concerns into focused modules instead of coupling them into a single application layer.

| Module | Purpose |
|---|---|
| `core` | Shared core utilities and common backend functionality |
| `core-mvc` | Spring MVC-oriented support |
| `core-reactive` | Reactive application support |
| `api-client` | Reusable API client utilities |
| `db` | General database integration |
| `orm` | ORM / JPA-related support |
| `oracle` | Oracle database integration |
| `mysql` | MySQL integration |
| `mssql` | Microsoft SQL Server integration |
| `mongodb` | MongoDB integration |
| `redis` | Redis integration |
| `cache` | Caching abstractions and utilities |
| `amqp` | AMQP messaging integration |
| `elastic` | Elasticsearch integration |
| `liquibase` | Database migrations with Liquibase |
| `log` | Logging infrastructure and utilities |
| `cryptography` | Cryptography-related utilities |
| `storage` | Storage-related utilities |

## Technology Baseline

The parent build currently defines:

- Java 21
- Spring Boot 3.4.x
- Maven multi-module build
- Spring Data JPA
- Spring Validation
- Spring Boot Actuator
- Micrometer / Prometheus / JMX
- Jackson
- MapStruct
- Lombok
- Mockito

The build enforces **JDK 21** and **Maven 3.9.9+**.

## Project Structure

```text
omnia/
├── amqp/
├── api-client/
├── cache/
├── core/
├── core-mvc/
├── core-reactive/
├── cryptography/
├── db/
├── elastic/
├── liquibase/
├── log/
├── mongodb/
├── mssql/
├── mysql/
├── oracle/
├── orm/
├── redis/
├── storage/
├── pom.xml
└── README.md
```

## Build

Build all modules from the repository root:

### Linux / macOS

```bash
./mvnw clean install
```

### Windows

```cmd
mvnw.cmd clean install
```

If you use a system Maven installation instead of the wrapper:

```bash
mvn clean install
```

## Test

Run the test suite for all modules:

```bash
./mvnw test
```

On Windows:

```cmd
mvnw.cmd test
```

## Build Requirements

Before building locally, make sure you have:

```text
JDK   21
Maven 3.9.9+
```

The Maven Enforcer configuration rejects unsupported Java or Maven versions.

## Optional Splunk Java Logging Dependency

Some logging integrations may require the Splunk Java Logging library to be installed in your local Maven repository.

After obtaining `splunk-library-javalogging-1.11.8.jar`, install it with:

```bash
mvn install:install-file \
  -Dfile=path/to/splunk-library-javalogging-1.11.8.jar \
  -DgroupId=com.splunk.logging \
  -DartifactId=splunk-library-javalogging \
  -Dversion=1.11.8 \
  -Dpackaging=jar
```

## Design Direction

Omnia is intended to keep recurring infrastructure concerns reusable and separated from application-specific business logic.

The project favors:

- modular infrastructure components
- shared conventions across backend services
- reusable persistence and integration layers
- consistent observability and logging foundations
- support for both MVC and reactive application styles
- centralized dependency and build governance

## Version

Current parent artifact:

```text
com.omnia:omnia:1.0.27
```

## Contributing

Issues and pull requests are welcome for bug fixes, improvements, additional integrations, and documentation updates.

When contributing, keep changes scoped to the relevant module and run the project test suite before submitting a pull request.

---

Built and maintained by [Hooman Yarahmadi](https://github.com/HoomanDevp).
