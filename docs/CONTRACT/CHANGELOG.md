# Changelog - UrbanHub

## [1.0.0] - 2026-07-03

### Added
- Ingestion Service (API REST)
- Kafka integration (sensor-data topic)
- Redis connection
- Docker setup
- Swagger OpenAPI ingestion-service
- Microservices architecture (Gateway + services)
- Event-driven communication with Kafka

### Added (architecture)
- Storage Service (MySQL integration)
- Analytics Service (real-time processing)
- Alerting Service (WebSocket alerts)
- Kafka message bus between services

### Tests
- Unit test for publishSensorData
- Mock Kafka producer with Jest
- Stub for sensor data validation

### Infrastructure
- Docker Compose setup
- Environment variables configuration
- Kafka + Redis + MySQL services

---

## [0.1.0] - Initial version

### Added
- Basic Express server setup
- First ingestion endpoint /ingestion/ingest