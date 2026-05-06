package com.smartcity.iot.event;

import com.smartcity.iot.entity.SensorStatus;
import com.smartcity.iot.entity.SensorType;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record SensorStatusChangedEvent(
        UUID sensorId,
        String externalId,
        SensorType sensorType,
        SensorStatus previousStatus,
        SensorStatus newStatus,
        String reason,
        Instant occurredAt
) {}
