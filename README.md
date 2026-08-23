# Flight Operations Management Platform

A production-style starter for an airline operations control platform that manages flights, aircraft assignments, crew availability, delays, cancellations, and operational alerts.

## Project purpose

This repository provides a clean first version of a full-stack Flight Operations Management Platform. It is organized as a monorepo today so the backend domain and UI can evolve quickly, while the package structure and deployment assets leave a clear path toward Azure-hosted microservices later.

## Technology stack

- **Backend:** Java 21, Spring Boot 3, Maven, Spring Web, Spring Data JPA, Spring Cache, Spring for Apache Kafka
- **Frontend:** React with Vite
- **Data:** PostgreSQL for persistence, Redis for cache-backed runtime data
- **Messaging:** Apache Kafka for operational flight events
- **DevOps:** Docker, Docker Compose, GitHub Actions
- **Cloud direction:** Azure App Service / Azure Container Apps, Azure Database for PostgreSQL, Azure Cache for Redis, Azure Event Hubs or managed Kafka-compatible infrastructure

## Architecture

```text
frontend/                       React dashboard
backend/                        Spring Boot monolith foundation
  src/main/java/com/flightops/platform
    controller/                 REST API layer
    domain/                     Core airline operations entities
    dto/                        API and dashboard response models
    event/                      Kafka event payloads
    repository/                 JPA repositories
    service/                    Business logic, seeding, event handling
```

### Current application boundaries

The initial backend is a modular monolith with explicit domains that can later be separated into services such as:

- `flight-service`
- `aircraft-service`
- `crew-service`
- `operations-service`
- `notification-service`

### Implemented foundation

- Flight domain with status lifecycle support: `SCHEDULED`, `BOARDING`, `DELAYED`, `AIRBORNE`, `LANDED`, `CANCELLED`
- Supporting domain models for `Aircraft`, `CrewMember`, `AircraftAssignment`, `CrewAssignment`, and `OperationalAlert`
- REST endpoints to create, retrieve, update, and change flight status
- Kafka publisher for major flight events:
  - `FLIGHT_DELAYED`
  - `FLIGHT_CANCELLED`
  - `FLIGHT_DEPARTED`
  - `FLIGHT_ARRIVED`
- Operations event consumer that turns those events into operational alerts
- Dashboard API with totals, flight list, aircraft assignments, and alerts
- Seed data for realistic airline operations scenarios
- Unit and integration test foundations with JUnit, Mockito, and Spring Boot test support

## Local setup instructions

### Prerequisites

- Java 21
- Maven 3.9+
- Node.js 22+
- PostgreSQL 16+
- Redis 7+
- Kafka 3+

### Backend

```bash
cd backend
mvn spring-boot:run
```

The backend reads configuration from environment variables:

| Variable | Default |
| --- | --- |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/flightops` |
| `SPRING_DATASOURCE_USERNAME` | `flightops` |
| `SPRING_DATASOURCE_PASSWORD` | required |
| `SPRING_CACHE_TYPE` | `simple` |
| `SPRING_DATA_REDIS_HOST` | `localhost` |
| `SPRING_DATA_REDIS_PORT` | `6379` |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` |
| `APP_KAFKA_ENABLED` | `true` |
| `APP_SEED_ENABLED` | `true` |
| `APP_CORS_ALLOWED_ORIGINS` | `http://localhost:5173,http://127.0.0.1:5173` |

> `SPRING_CACHE_TYPE=simple` keeps the backend easy to run without Redis, while Docker Compose enables Redis-backed caching for the intended local stack.

### Frontend

```bash
cd frontend
npm ci
npm run dev
```

The dashboard uses `VITE_API_BASE_URL` and defaults to `/api`. During local Vite development, `/api` is proxied to `http://localhost:8080`. In Docker Compose, nginx proxies `/api` to the backend container.

## Docker setup

Run the full local stack with:

```bash
docker compose up --build
```

This starts:

- PostgreSQL
- Redis
- Zookeeper
- Kafka
- Spring Boot backend on `http://localhost:8080`
- React dashboard on `http://localhost:5173`

## API endpoints

### Flights

- `GET /api/flights` — list flights
- `GET /api/flights/{id}` — retrieve a flight
- `POST /api/flights` — create a flight
- `PUT /api/flights/{id}` — replace a flight definition
- `PATCH /api/flights/{id}/status` — change a flight status

Example status update payload:

```json
{
  "status": "DELAYED",
  "delayMinutes": 30
}
```

### Dashboard and alerts

- `GET /api/dashboard` — summary cards, current flights, aircraft assignments, alerts
- `GET /api/alerts` — recent operational alerts

## Testing

Backend tests:

```bash
cd backend
mvn test
```

Frontend validation:

```bash
cd frontend
npm ci
npm run lint
npm run build
```

## GitHub Actions

`.github/workflows/ci.yml` builds the frontend, lints the React app, and runs the backend Maven test suite on every push and pull request.

## Future Azure deployment plans

This repository is intentionally structured so the current modular monolith can be decomposed into dedicated Spring Boot services. A next Azure-focused phase can:

- package backend services as separate containers for Azure Container Apps or AKS
- provision Azure Database for PostgreSQL for persistent storage
- migrate Redis usage to Azure Cache for Redis
- replace local Kafka with Azure Event Hubs Kafka endpoint or Azure-managed messaging infrastructure
- add Azure OpenID Connect, Key Vault, and Application Insights integrations
