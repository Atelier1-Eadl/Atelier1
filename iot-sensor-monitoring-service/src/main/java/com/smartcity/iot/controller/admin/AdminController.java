package com.smartcity.iot.controller.admin;

import com.smartcity.iot.dto.InjectIncidentRequest;
import com.smartcity.iot.entity.SensorStatus;
import com.smartcity.iot.service.SeedService;
import com.smartcity.iot.service.SensorService;
import com.smartcity.iot.service.simulation.SimulationOrchestrator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin", description = "Administration et pilotage de la simulation")
public class AdminController {

    private final SimulationOrchestrator orchestrator;
    private final SensorService sensorService;
    private final SeedService seedService;

    @Autowired
    public AdminController(SimulationOrchestrator orchestrator,
                           SensorService sensorService,
                           SeedService seedService) {
        this.orchestrator = orchestrator;
        this.sensorService = sensorService;
        this.seedService = seedService;
    }

    @PostMapping("/sensors/{id}/start")
    @Operation(summary = "Demarrer la simulation d'un capteur")
    public ResponseEntity<Map<String, Object>> startSensor(@PathVariable UUID id) {
        orchestrator.startSensor(id);
        return ResponseEntity.ok(Map.of(
                "sensorId", id,
                "running", orchestrator.isRunning(id),
                "message", "Simulation demarree"
        ));
    }

    @PostMapping("/sensors/{id}/stop")
    @Operation(summary = "Arreter la simulation d'un capteur")
    public ResponseEntity<Map<String, Object>> stopSensor(@PathVariable UUID id) {
        orchestrator.stopSensor(id);
        return ResponseEntity.ok(Map.of(
                "sensorId", id,
                "running", orchestrator.isRunning(id),
                "message", "Simulation arretee"
        ));
    }

    @PostMapping("/sensors/{id}/inject-incident")
    @Operation(summary = "Injecter un incident sur un capteur (pic pollution, embouteillage, panne)")
    public ResponseEntity<Map<String, Object>> injectIncident(
            @PathVariable UUID id,
            @RequestBody @Valid InjectIncidentRequest request) {

        int duration = request.durationSeconds() != null ? request.durationSeconds() : 60;
        String incidentType = request.type().name();

        if (request.type() == InjectIncidentRequest.IncidentType.SENSOR_FAILURE) {
            orchestrator.stopSensor(id);
            sensorService.updateStatus(id,
                    SensorStatus.OFFLINE,
                    "Panne simulee via endpoint admin");
        } else {
            orchestrator.injectIncident(id, incidentType, duration);
        }

        return ResponseEntity.ok(Map.of(
                "sensorId", id,
                "incident", incidentType,
                "durationSeconds", duration,
                "message", "Incident injecte avec succes"
        ));
    }

    @PostMapping("/simulation/start-all")
    @Operation(summary = "Demarrer toutes les simulations")
    public ResponseEntity<Map<String, Object>> startAll() {
        orchestrator.startAll();
        return ResponseEntity.ok(Map.of(
                "activeSensors", orchestrator.activeCount(),
                "message", "Toutes les simulations demarrees"
        ));
    }

    @PostMapping("/simulation/stop-all")
    @Operation(summary = "Arreter toutes les simulations")
    public ResponseEntity<Map<String, Object>> stopAll() {
        orchestrator.stopAll();
        return ResponseEntity.ok(Map.of(
                "activeSensors", orchestrator.activeCount(),
                "message", "Toutes les simulations arretees"
        ));
    }

    @GetMapping("/simulation/status")
    @Operation(summary = "Etat global de la simulation")
    public ResponseEntity<Map<String, Object>> simulationStatus() {
        return ResponseEntity.ok(Map.of(
                "activeSensors", orchestrator.activeCount()
        ));
    }

    @PostMapping("/seed")
    @Operation(summary = "Creer un jeu de capteurs de demonstration (10 QA + 15 trafic)")
    public ResponseEntity<Map<String, Object>> seed() {
        int created = seedService.seedDemoSensors();
        return ResponseEntity.ok(Map.of(
                "createdSensors", created,
                "message", created + " capteurs de demonstration crees et demarres"
        ));
    }
}
