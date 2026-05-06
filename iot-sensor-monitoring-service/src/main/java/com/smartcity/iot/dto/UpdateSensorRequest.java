package com.smartcity.iot.dto;

import jakarta.validation.constraints.*;

public record UpdateSensorRequest(
        String name,
        String address,
        @Min(1000) @Max(300000) Long generationFrequencyMs,
        Boolean active
) {}
