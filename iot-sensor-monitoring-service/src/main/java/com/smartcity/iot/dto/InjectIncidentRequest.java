package com.smartcity.iot.dto;

import jakarta.validation.constraints.NotNull;

public record InjectIncidentRequest(
        @NotNull IncidentType type,
        Integer durationSeconds
) {
    public enum IncidentType {
        POLLUTION_SPIKE,
        TRAFFIC_JAM,
        SENSOR_FAILURE
    }
}
