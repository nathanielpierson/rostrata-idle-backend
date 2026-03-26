# rostrata-idle-backend

Spring Boot backend for Rostrata Idle.

## Requirements

- Java 21
- Maven 3.6+

## Run

```bash
./mvnw spring-boot:run
```

Or with a local Maven install:

```bash
mvn spring-boot:run
```

## Build

```bash
./mvnw clean package
```

## Test

```bash
./mvnw test
```

## Endpoints

- `GET /health` — Returns `{"status":"UP"}` when the app is running.

Server runs on port 8080 by default.
