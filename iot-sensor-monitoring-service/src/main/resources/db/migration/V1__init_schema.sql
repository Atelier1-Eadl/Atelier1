CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TYPE sensor_type AS ENUM ('AIR_QUALITY', 'TRAFFIC');
CREATE TYPE sensor_status AS ENUM ('ONLINE', 'OFFLINE', 'DEGRADED', 'UNKNOWN');
CREATE TYPE aqi_category AS ENUM ('GOOD', 'FAIR', 'MODERATE', 'POOR', 'VERY_POOR', 'EXTREMELY_POOR');
CREATE TYPE congestion_level AS ENUM ('FREE_FLOW', 'LIGHT', 'MODERATE', 'HEAVY', 'JAM');

CREATE TABLE sensors (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    external_id             VARCHAR(100) NOT NULL UNIQUE,
    name                    VARCHAR(255) NOT NULL,
    type                    VARCHAR(20)  NOT NULL,
    latitude                DOUBLE PRECISION NOT NULL,
    longitude               DOUBLE PRECISION NOT NULL,
    address                 VARCHAR(500),
    registered_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_seen_at            TIMESTAMPTZ,
    status                  VARCHAR(20) NOT NULL DEFAULT 'UNKNOWN',
    generation_frequency_ms BIGINT NOT NULL DEFAULT 10000,
    active                  BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE air_quality_readings (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sensor_id    UUID NOT NULL REFERENCES sensors(id) ON DELETE CASCADE,
    pm25         DOUBLE PRECISION NOT NULL,
    pm10         DOUBLE PRECISION NOT NULL,
    no2          DOUBLE PRECISION NOT NULL,
    o3           DOUBLE PRECISION NOT NULL,
    co           DOUBLE PRECISION NOT NULL,
    temperature  DOUBLE PRECISION NOT NULL,
    humidity     DOUBLE PRECISION NOT NULL,
    aqi          INTEGER NOT NULL,
    aqi_category VARCHAR(20) NOT NULL,
    timestamp    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_aqr_sensor_timestamp ON air_quality_readings (sensor_id, timestamp DESC);

CREATE TABLE traffic_readings (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sensor_id         UUID NOT NULL REFERENCES sensors(id) ON DELETE CASCADE,
    vehicle_count     INTEGER NOT NULL,
    average_speed     DOUBLE PRECISION NOT NULL,
    occupancy_rate    DOUBLE PRECISION NOT NULL,
    congestion_level  VARCHAR(20) NOT NULL,
    car_count         INTEGER NOT NULL,
    truck_count       INTEGER NOT NULL,
    motorcycle_count  INTEGER NOT NULL,
    timestamp         TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_tr_sensor_timestamp ON traffic_readings (sensor_id, timestamp DESC);

CREATE TABLE sensor_status_events (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sensor_id        UUID NOT NULL REFERENCES sensors(id) ON DELETE CASCADE,
    previous_status  VARCHAR(20),
    new_status       VARCHAR(20) NOT NULL,
    reason           TEXT,
    occurred_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_sse_sensor_id ON sensor_status_events (sensor_id);
