package com.smartcity.iot.dto;

import com.smartcity.iot.entity.AqiCategory;

import java.time.Instant;
import java.util.UUID;

public record AirQualityReadingDto(
        UUID id,
        UUID sensorId,
        Double pm25,
        Double pm10,
        Double no2,
        Double o3,
        Double co,
        Double temperature,
        Double humidity,
        Integer aqi,
        AqiCategory aqiCategory,
        Instant timestamp
) {}
