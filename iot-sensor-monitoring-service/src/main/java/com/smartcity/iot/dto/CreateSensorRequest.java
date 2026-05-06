package com.smartcity.iot.dto;

import com.smartcity.iot.entity.SensorType;
import jakarta.validation.constraints.*;

public record CreateSensorRequest(
        @NotBlank String externalId,
        @NotBlank String name,
        @NotNull SensorType type,
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
        String address,
        @Min(1000) @Max(300000) Long generationFrequencyMs
) {}
