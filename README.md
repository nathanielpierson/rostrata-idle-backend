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

## Skill XP curve

Canonical level 1–100 progression (350 XP for 1→2, tiered multipliers 1.09 → 1.075 → 1.050) lives in
`com.rostrata.idle.progression.XpProgression`. The player wiki documents the full table at
`experience-chart` (`rostrata-idle-wiki`).

## Endpoints

- `GET /health` — Returns `{"status":"UP"}` when the app is running.

Server runs on port 8080 by default.
