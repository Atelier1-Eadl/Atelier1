package com.smartcity.iot.service.simulation;

import com.smartcity.iot.config.AppProperties;
import com.smartcity.iot.entity.*;
import com.smartcity.iot.kafka.producer.SensorEventProducer;
import com.smartcity.iot.repository.AirQualityReadingRepository;
import com.smartcity.iot.repository.SensorRepository;
import com.smartcity.iot.repository.TrafficReadingRepository;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages the lifecycle of all active sensor simulation tasks.
 * Each active sensor gets its own scheduled task running at its configured frequency.
 */
@SuppressWarnings("null")
@Service
@Slf4j
public class SimulationOrchestrator {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10,
            r -> {
                Thread t = new Thread(r, "sensor-simulator");
                t.setDaemon(true);
                return t;
            });

    private final Map<UUID, ScheduledFuture<?>> activeTasks = new ConcurrentHashMap<>();
    private final Map<UUID, SensorSimulator<?>> sensorSimulators = new ConcurrentHashMap<>();

    private final SensorRepository sensorRepository;
    private final AirQualityReadingRepository airQualityReadingRepository;
    private final TrafficReadingRepository trafficReadingRepository;
    private final SensorEventProducer eventProducer;
    private final AppProperties appProperties;
    private final ApplicationContext applicationContext;

    private final AtomicBoolean globalEnabled = new AtomicBoolean(true);

    @Autowired
    public SimulationOrchestrator(SensorRepository sensorRepository,
                                  AirQualityReadingRepository airQualityReadingRepository,
                                  TrafficReadingRepository trafficReadingRepository,
                                  SensorEventProducer eventProducer,
                                  AppProperties appProperties,
                                  ApplicationContext applicationContext) {
        this.sensorRepository = sensorRepository;
        this.airQualityReadingRepository = airQualityReadingRepository;
        this.trafficReadingRepository = trafficReadingRepository;
        this.eventProducer = eventProducer;
        this.appProperties = appProperties;
        this.applicationContext = applicationContext;
    }

    public void startSensor(UUID sensorId) {
        if (activeTasks.containsKey(sensorId)) {
            log.debug("Sensor {} already running", sensorId);
            return;
        }
        sensorRepository.findById(sensorId).ifPresent(sensor -> {
            if (!sensor.getActive()) return;

            long frequencyMs = sensor.getGenerationFrequencyMs() != null
                    ? sensor.getGenerationFrequencyMs()
                    : appProperties.getSimulation().getDefaultFrequencyMs();

            SensorSimulator<?> sim = resolveSimulator(sensor.getType());
            sensorSimulators.put(sensorId, sim);

            ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
                    () -> executeSimulationTick(sensor, sim),
                    0, frequencyMs, TimeUnit.MILLISECONDS);

            activeTasks.put(sensorId, future);
            log.info("Started simulation for sensor {} ({}) every {}ms", sensor.getExternalId(), sensor.getType(), frequencyMs);
        });
    }

    public void stopSensor(UUID sensorId) {
        ScheduledFuture<?> future = activeTasks.remove(sensorId);
        if (future != null) {
            future.cancel(false);
            sensorSimulators.remove(sensorId);
            log.info("Stopped simulation for sensor {}", sensorId);
        }
    }

    public void startAll() {
        if (!appProperties.getSimulation().isEnabled()) {
            log.info("Simulation is disabled via configuration, skipping startAll");
            return;
        }
        sensorRepository.findAllByActiveTrue().forEach(sensor -> startSensor(sensor.getId()));
        log.info("Started {} sensor simulations", activeTasks.size());
    }

    public void stopAll() {
        new java.util.HashSet<>(activeTasks.keySet()).forEach(this::stopSensor);
    }

    public boolean isRunning(UUID sensorId) {
        return activeTasks.containsKey(sensorId);
    }

    public int activeCount() {
        return activeTasks.size();
    }

    public void injectIncident(UUID sensorId, String incidentType, int durationSeconds) {
        SensorSimulator<?> sim = sensorSimulators.get(sensorId);
        if (sim == null) {
            throw new IllegalStateException("Sensor " + sensorId + " is not currently running");
        }
        sim.applyIncident(incidentType, durationSeconds);
        log.info("Injected incident '{}' for sensor {} ({}s)", incidentType, sensorId, durationSeconds);
    }

    @SuppressWarnings("unchecked")
    private void executeSimulationTick(Sensor sensor, SensorSimulator<?> sim) {
        if (!globalEnabled.get()) return;
        try {
            if (sensor.getType() == SensorType.AIR_QUALITY) {
                AirQualityReading reading = ((SensorSimulator<AirQualityReading>) sim).generateReading(sensor);
                airQualityReadingRepository.save(reading);
                updateSensorLastSeen(sensor.getId());
                eventProducer.publishAirQualityReading(sensor, reading);
            } else if (sensor.getType() == SensorType.TRAFFIC) {
                TrafficReading reading = ((SensorSimulator<TrafficReading>) sim).generateReading(sensor);
                trafficReadingRepository.save(reading);
                updateSensorLastSeen(sensor.getId());
                eventProducer.publishTrafficReading(sensor, reading);
            }
        } catch (Exception ex) {
            log.error("Simulation tick failed for sensor {}: {}", sensor.getId(), ex.getMessage(), ex);
        }
    }

    private void updateSensorLastSeen(UUID sensorId) {
        sensorRepository.findById(sensorId).ifPresent(sensor -> {
            sensor.setLastSeenAt(Instant.now());
            if (sensor.getStatus() != SensorStatus.ONLINE) {
                sensor.setStatus(SensorStatus.ONLINE);
            }
            sensorRepository.save(sensor);
        });
    }

    /**
     * Creates a fresh prototype instance per sensor so each sensor has its own independent state.
     */
    private SensorSimulator<?> resolveSimulator(SensorType type) {
        return switch (type) {
            case AIR_QUALITY -> applicationContext.getBean(AirQualitySimulator.class);
            case TRAFFIC -> applicationContext.getBean(TrafficSimulator.class);
        };
    }

    @PreDestroy
    public void shutdown() {
        stopAll();
        scheduler.shutdownNow();
    }
}
