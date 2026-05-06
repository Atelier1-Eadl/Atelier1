package com.smartcity.iot.dto;

import com.smartcity.iot.entity.CongestionLevel;

import java.time.Instant;
import java.util.UUID;

public record TrafficReadingDto(
        UUID id,
        UUID sensorId,
        Integer vehicleCount,
        Double averageSpeed,
        Double occupancyRate,
        CongestionLevel congestionLevel,
        Integer carCount,
        Integer truckCount,
        Integer motorcycleCount,
        Instant timestamp
) {}
