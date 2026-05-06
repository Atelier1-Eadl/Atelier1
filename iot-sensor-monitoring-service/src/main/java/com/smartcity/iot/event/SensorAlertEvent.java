package com.smartcity.iot.event;

import com.smartcity.iot.entity.SensorType;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record SensorAlertEvent(
        UUID sensorId,
        String externalId,
        SensorType sensorType,
        String alertType,
        String message,
        Object value,
        Instant occurredAt
) {}
