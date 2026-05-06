package com.smartcity.iot.service;

import com.smartcity.iot.config.AppProperties;
import com.smartcity.iot.entity.Sensor;
import com.smartcity.iot.entity.SensorStatus;
import com.smartcity.iot.entity.SensorType;
import com.smartcity.iot.repository.SensorRepository;
import com.smartcity.iot.service.simulation.SimulationOrchestrator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates demonstration sensor data programmatically (alternative to Flyway V2 seed).
 */
@Service
@Slf4j
public class SeedService {

    private final SensorRepository sensorRepository;
    private final SimulationOrchestrator orchestrator;
    private final AppProperties appProperties;

    @Autowired
    public SeedService(SensorRepository sensorRepository,
                       SimulationOrchestrator orchestrator,
                       AppProperties appProperties) {
        this.sensorRepository = sensorRepository;
        this.orchestrator = orchestrator;
        this.appProperties = appProperties;
    }

    @Transactional
    public int seedDemoSensors() {
        List<Sensor> toSave = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            String extId = String.format("AQ-DEMO-%03d", i + 1);
            if (!sensorRepository.existsByExternalId(extId)) {
                toSave.add(buildSensor(extId, "Capteur QA Demo " + (i + 1),
                        SensorType.AIR_QUALITY, 48.85 + (i * 0.01), 2.35 + (i * 0.01)));
            }
        }

        for (int i = 0; i < 15; i++) {
            String extId = String.format("TR-DEMO-%03d", i + 1);
            if (!sensorRepository.existsByExternalId(extId)) {
                toSave.add(buildSensor(extId, "Radar Trafic Demo " + (i + 1),
                        SensorType.TRAFFIC, 48.86 + (i * 0.01), 2.34 + (i * 0.01)));
            }
        }

        List<Sensor> saved = sensorRepository.saveAll(toSave);
        saved.forEach(s -> orchestrator.startSensor(s.getId()));
        log.info("Seeded {} demo sensors", saved.size());
        return saved.size();
    }

    private Sensor buildSensor(String externalId, String name, SensorType type,
                                double lat, double lng) {
        return Sensor.builder()
                .externalId(externalId)
                .name(name)
                .type(type)
                .latitude(lat)
                .longitude(lng)
                .address("Paris, France")
                .status(SensorStatus.UNKNOWN)
                .generationFrequencyMs(appProperties.getSimulation().getDefaultFrequencyMs())
                .active(true)
                .build();
    }
}
