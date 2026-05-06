package com.smartcity.iot.kafka.producer;

import com.smartcity.iot.config.AppProperties;
import com.smartcity.iot.entity.*;
import com.smartcity.iot.event.SensorAlertEvent;
import com.smartcity.iot.event.SensorStatusChangedEvent;
import com.smartcity.iot.event.SensorTelemetryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("null")
@Component
@Slf4j
public class SensorEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AppProperties appProperties;

    @Autowired
    public SensorEventProducer(KafkaTemplate<String, Object> kafkaTemplate,
                                AppProperties appProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.appProperties = appProperties;
    }

    public void publishAirQualityReading(Sensor sensor, AirQualityReading reading) {
        Map<String, Object> measurements = new LinkedHashMap<>();
        measurements.put("pm25", reading.getPm25());
        measurements.put("pm10", reading.getPm10());
        measurements.put("no2", reading.getNo2());
        measurements.put("o3", reading.getO3());
        measurements.put("co", reading.getCo());
        measurements.put("temperature", reading.getTemperature());
        measurements.put("humidity", reading.getHumidity());
        measurements.put("aqi", reading.getAqi());
        measurements.put("aqiCategory", reading.getAqiCategory().name());

        SensorTelemetryEvent event = SensorTelemetryEvent.builder()
                .sensorId(sensor.getId())
                .externalId(sensor.getExternalId())
                .sensorType(SensorType.AIR_QUALITY)
                .timestamp(reading.getTimestamp())
                .measurements(measurements)
                .build();

        String topic = appProperties.getKafka().getTopics().getAirQualityTelemetry();
        kafkaTemplate.send(topic, sensor.getId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish air quality telemetry for sensor {}: {}",
                                sensor.getExternalId(), ex.getMessage());
                    }
                });

        if (reading.getAqiCategory() == AqiCategory.POOR
                || reading.getAqiCategory() == AqiCategory.VERY_POOR
                || reading.getAqiCategory() == AqiCategory.EXTREMELY_POOR) {
            publishAlert(sensor, "HIGH_AQI",
                    "Qualite de l'air degradee : " + reading.getAqiCategory().name(),
                    reading.getAqi());
        }
    }

    public void publishTrafficReading(Sensor sensor, TrafficReading reading) {
        Map<String, Object> measurements = new LinkedHashMap<>();
        measurements.put("vehicleCount", reading.getVehicleCount());
        measurements.put("averageSpeed", reading.getAverageSpeed());
        measurements.put("occupancyRate", reading.getOccupancyRate());
        measurements.put("congestionLevel", reading.getCongestionLevel().name());
        measurements.put("carCount", reading.getCarCount());
        measurements.put("truckCount", reading.getTruckCount());
        measurements.put("motorcycleCount", reading.getMotorcycleCount());

        SensorTelemetryEvent event = SensorTelemetryEvent.builder()
                .sensorId(sensor.getId())
                .externalId(sensor.getExternalId())
                .sensorType(SensorType.TRAFFIC)
                .timestamp(reading.getTimestamp())
                .measurements(measurements)
                .build();

        String topic = appProperties.getKafka().getTopics().getTrafficTelemetry();
        kafkaTemplate.send(topic, sensor.getId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish traffic telemetry for sensor {}: {}",
                                sensor.getExternalId(), ex.getMessage());
                    }
                });

        if (reading.getCongestionLevel() == CongestionLevel.JAM) {
            publishAlert(sensor, "TRAFFIC_JAM",
                    "Embouteillage detecte sur " + sensor.getName(),
                    reading.getCongestionLevel());
        }
    }

    public void publishStatusChanged(Sensor sensor, SensorStatus previousStatus, String reason) {
        SensorStatusChangedEvent event = SensorStatusChangedEvent.builder()
                .sensorId(sensor.getId())
                .externalId(sensor.getExternalId())
                .sensorType(sensor.getType())
                .previousStatus(previousStatus)
                .newStatus(sensor.getStatus())
                .reason(reason)
                .occurredAt(Instant.now())
                .build();

        String topic = appProperties.getKafka().getTopics().getStatusChanged();
        kafkaTemplate.send(topic, sensor.getId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish status changed event for sensor {}: {}",
                                sensor.getExternalId(), ex.getMessage());
                    }
                });
    }

    private void publishAlert(Sensor sensor, String alertType, String message, Object value) {
        SensorAlertEvent event = SensorAlertEvent.builder()
                .sensorId(sensor.getId())
                .externalId(sensor.getExternalId())
                .sensorType(sensor.getType())
                .alertType(alertType)
                .message(message)
                .value(value)
                .occurredAt(Instant.now())
                .build();

        kafkaTemplate.send(appProperties.getKafka().getTopics().getAlert(),
                sensor.getId().toString(), event);
    }
}
