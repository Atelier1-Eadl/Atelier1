package com.smartcity.iot.dto;

import com.smartcity.iot.entity.CongestionLevel;

public record SensorOverviewDto(
        long totalSensors,
        long airQualityOnline,
        long airQualityOffline,
        long airQualityDegraded,
        long trafficOnline,
        long trafficOffline,
        long trafficDegraded,
        Double averageCityAqi,
        String averageCityAqiCategory,
        CongestionLevel dominantCongestionLevel
) {}
