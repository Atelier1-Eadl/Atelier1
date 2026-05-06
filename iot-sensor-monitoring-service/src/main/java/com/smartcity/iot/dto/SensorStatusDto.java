package com.smartcity.iot.dto;

import com.smartcity.iot.entity.SensorStatus;
import com.smartcity.iot.entity.SensorType;

import java.time.Instant;
import java.util.UUID;

public record SensorStatusDto(
        UUID sensorId,
        String name,
        SensorType type,
        SensorStatus status,
        Instant lastSeenAt,
        Instant cachedAt
) {}
