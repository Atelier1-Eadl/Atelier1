package com.smartcity.iot.scheduler;

import com.smartcity.iot.config.AppProperties;
import com.smartcity.iot.entity.Sensor;
import com.smartcity.iot.entity.SensorStatus;
import com.smartcity.iot.repository.SensorRepository;
import com.smartcity.iot.service.SensorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * Periodically detects sensors that have not sent data within the configured threshold
 * and marks them as OFFLINE.
 */
@Component
@Slf4j
public class OfflineDetectionScheduler {

    private final SensorRepository sensorRepository;
    private final SensorService sensorService;
    private final AppProperties appProperties;

    @Autowired
    public OfflineDetectionScheduler(SensorRepository sensorRepository,
                                     SensorService sensorService,
                                     AppProperties appProperties) {
        this.sensorRepository = sensorRepository;
        this.sensorService = sensorService;
        this.appProperties = appProperties;
    }

    @Scheduled(fixedDelayString = "${app.simulation.offline-threshold-seconds:60}000")
    public void detectOfflineSensors() {
        long thresholdSeconds = appProperties.getSimulation().getOfflineThresholdSeconds();
        Instant threshold = Instant.now().minusSeconds(thresholdSeconds);

        List<Sensor> stale = sensorRepository.findActiveSensorsLastSeenBefore(threshold);

        for (Sensor sensor : stale) {
            if (sensor.getStatus() != SensorStatus.OFFLINE) {
                log.warn("Sensor {} ({}) marked OFFLINE - no data since {}",
                        sensor.getExternalId(), sensor.getType(), sensor.getLastSeenAt());
                sensorService.updateStatus(sensor.getId(), SensorStatus.OFFLINE,
                        "Aucune donnee recue depuis " + thresholdSeconds + " secondes");
            }
        }

        if (!stale.isEmpty()) {
            log.info("Offline detection: {} sensor(s) marked OFFLINE", stale.size());
        }
    }
}
