package com.smartcity.iot.controller;

import com.smartcity.iot.dto.AirQualityReadingDto;
import com.smartcity.iot.dto.PagedResponse;
import com.smartcity.iot.service.AirQualityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sensors/air-quality")
@Tag(name = "Air Quality", description = "Lectures de qualite de l'air")
public class AirQualityController {

    private final AirQualityService airQualityService;

    @Autowired
    public AirQualityController(AirQualityService airQualityService) {
        this.airQualityService = airQualityService;
    }

    @GetMapping("/{id}/readings")
    @Operation(summary = "Historique des mesures de qualite de l'air (pagine)")
    public ResponseEntity<PagedResponse<AirQualityReadingDto>> getReadings(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(airQualityService.getReadings(id, page, size));
    }

    @GetMapping("/{id}/readings/latest")
    @Operation(summary = "Derniere mesure de qualite de l'air")
    public ResponseEntity<AirQualityReadingDto> getLatest(@PathVariable UUID id) {
        return ResponseEntity.ok(airQualityService.getLatest(id));
    }
}
