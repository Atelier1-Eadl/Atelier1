package com.smartcity.iot.controller;

import com.smartcity.iot.dto.PagedResponse;
import com.smartcity.iot.dto.TrafficReadingDto;
import com.smartcity.iot.service.TrafficService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sensors/traffic")
@Tag(name = "Traffic", description = "Lectures de trafic routier")
public class TrafficController {

    private final TrafficService trafficService;

    @Autowired
    public TrafficController(TrafficService trafficService) {
        this.trafficService = trafficService;
    }

    @GetMapping("/{id}/readings")
    @Operation(summary = "Historique des mesures de trafic (pagine)")
    public ResponseEntity<PagedResponse<TrafficReadingDto>> getReadings(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(trafficService.getReadings(id, page, size));
    }

    @GetMapping("/{id}/readings/latest")
    @Operation(summary = "Derniere mesure de trafic")
    public ResponseEntity<TrafficReadingDto> getLatest(@PathVariable UUID id) {
        return ResponseEntity.ok(trafficService.getLatest(id));
    }
}
