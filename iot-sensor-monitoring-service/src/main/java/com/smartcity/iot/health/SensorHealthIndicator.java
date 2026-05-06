package com.smartcity.iot.health;

import com.smartcity.iot.entity.SensorStatus;
import com.smartcity.iot.entity.SensorType;
import com.smartcity.iot.repository.SensorRepository;
import com.smartcity.iot.service.simulation.SimulationOrchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("sensorSimulation")
public class SensorHealthIndicator implements HealthIndicator {

    private final SensorRepository sensorRepository;
    private final SimulationOrchestrator orchestrator;

    @Autowired
    public SensorHealthIndicator(SensorRepository sensorRepository,
                                  SimulationOrchestrator orchestrator) {
        this.sensorRepository = sensorRepository;
        this.orchestrator = orchestrator;
    }

    @Override
    public Health health() {
        long aqOnline = sensorRepository.countByTypeAndStatus(SensorType.AIR_QUALITY, SensorStatus.ONLINE);
        long aqOffline = sensorRepository.countByTypeAndStatus(SensorType.AIR_QUALITY, SensorStatus.OFFLINE);
        long trOnline = sensorRepository.countByTypeAndStatus(SensorType.TRAFFIC, SensorStatus.ONLINE);
        long trOffline = sensorRepository.countByTypeAndStatus(SensorType.TRAFFIC, SensorStatus.OFFLINE);

        long totalOnline = aqOnline + trOnline;
        long totalOffline = aqOffline + trOffline;
        int activeSimulations = orchestrator.activeCount();

        Health.Builder builder = (totalOffline > totalOnline && totalOnline + totalOffline > 0)
                ? Health.down()
                : Health.up();

        return builder
                .withDetail("airQuality.online", aqOnline)
                .withDetail("airQuality.offline", aqOffline)
                .withDetail("traffic.online", trOnline)
                .withDetail("traffic.offline", trOffline)
                .withDetail("activeSimulations", activeSimulations)
                .build();
    }
}
