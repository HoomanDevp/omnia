# Omnia Java Multi-Module Project

This repository contains a multi-module Java project built with Maven. It includes several core modules and integrations for messaging, database, caching, logging, and more.

## Project Structure

- **amqp/**: AMQP messaging integration
- **api-client/**: API client utilities
- **cache/**: Caching functionality
- **core/**: Core business logic and utilities
- **core-mvc/**: Spring MVC support
- **core-reactive/**: Reactive programming support
- **cryptography/**: Cryptography utilities
- **db/**: Database integration
- **elastic/**: Elasticsearch integration
- **liquibase/**: Database migrations with Liquibase
- **log/**: Logging utilities
- **mongodb/**: MongoDB integration
- **mssql/**: Microsoft SQL Server integration
- **mysql/**: MySQL integration
- **oracle/**: Oracle DB integration
- **orm/**: ORM (Object Relational Mapping) support
- **redis/**: Redis integration
- **storage/**: Storage utilities

## Building the Project

To build all modules:

```
./mvnw clean install
```

Or on Windows:

```
mvnw.cmd clean install
```

## Running Tests

To run tests for all modules:

```
./mvnw test
```

## Adding Splunk Java Logging JAR (Optional)

If you need Splunk logging integration, download the JAR from:

https://splunk.jfrog.io/ui/native/ext-releases-local/com/splunk/logging/splunk-library-javalogging/1.11.8/

Then add it to your local Maven repository:

```
mvn install:install-file -Dfile=path/to/splunk-library-javalogging-1.11.8.jar -DgroupId=com.splunk.logging -DartifactId=splunk-library-javalogging -Dversion=1.11.8 -Dpackaging=jar
```

## Contributing

Feel free to open issues or submit pull requests for improvements.

---

For more details, see the individual module README files (if available).
