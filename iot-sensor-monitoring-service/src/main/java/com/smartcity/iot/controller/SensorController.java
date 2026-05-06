package com.smartcity.iot.controller;

import com.smartcity.iot.dto.*;
import com.smartcity.iot.entity.SensorType;
import com.smartcity.iot.service.OverviewService;
import com.smartcity.iot.service.SensorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sensors")
@Tag(name = "Sensors", description = "Gestion des capteurs IoT")
public class SensorController {

    private final SensorService sensorService;
    private final OverviewService overviewService;

    @Autowired
    public SensorController(SensorService sensorService, OverviewService overviewService) {
        this.sensorService = sensorService;
        this.overviewService = overviewService;
    }

    @GetMapping
    @Operation(summary = "Lister tous les capteurs", description = "Filtre optionnel par type (AIR_QUALITY, TRAFFIC)")
    public ResponseEntity<List<SensorDto>> findAll(@RequestParam(required = false) SensorType type) {
        return ResponseEntity.ok(sensorService.findAll(type));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un capteur par ID")
    public ResponseEntity<SensorDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(sensorService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Creer un nouveau capteur")
    public ResponseEntity<SensorDto> create(@RequestBody @Valid CreateSensorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sensorService.create(request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Mettre a jour un capteur")
    public ResponseEntity<SensorDto> update(@PathVariable UUID id,
                                             @RequestBody @Valid UpdateSensorRequest request) {
        return ResponseEntity.ok(sensorService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un capteur")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        sensorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/status")
    @Operation(summary = "Obtenir le statut courant depuis Redis")
    public ResponseEntity<SensorStatusDto> getStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(sensorService.getStatus(id));
    }

    @GetMapping("/overview")
    @Operation(summary = "Resume global : nb capteurs par type/statut, AQI moyen, congestion dominante")
    public ResponseEntity<SensorOverviewDto> getOverview() {
        return ResponseEntity.ok(overviewService.getOverview());
    }
}
