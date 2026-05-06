package com.smartcity.iot.dto;

import com.smartcity.iot.entity.SensorStatus;
import com.smartcity.iot.entity.SensorType;

import java.time.Instant;
import java.util.UUID;

public record SensorDto(
        UUID id,
        String externalId,
        String name,
        SensorType type,
        Double latitude,
        Double longitude,
        String address,
        Instant registeredAt,
        Instant lastSeenAt,
        SensorStatus status,
        Long generationFrequencyMs,
        Boolean active
) {}
