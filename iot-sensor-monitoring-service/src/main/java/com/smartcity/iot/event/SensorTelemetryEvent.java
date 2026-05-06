package com.smartcity.iot.event;

import com.smartcity.iot.entity.SensorType;
import lombok.Builder;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Builder
public record SensorTelemetryEvent(
        UUID sensorId,
        String externalId,
        SensorType sensorType,
        Instant timestamp,
        Map<String, Object> measurements
) {}
