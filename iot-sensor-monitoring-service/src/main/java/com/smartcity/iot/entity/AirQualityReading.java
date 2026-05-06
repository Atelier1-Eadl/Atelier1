package com.smartcity.iot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "air_quality_readings", indexes = {
        @Index(name = "idx_aqr_sensor_timestamp", columnList = "sensor_id, timestamp DESC")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirQualityReading {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "sensor_id", nullable = false)
    private UUID sensorId;

    @Column(nullable = false)
    private Double pm25;

    @Column(nullable = false)
    private Double pm10;

    @Column(nullable = false)
    private Double no2;

    @Column(nullable = false)
    private Double o3;

    @Column(nullable = false)
    private Double co;

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private Double humidity;

    @Column(nullable = false)
    private Integer aqi;

    @Enumerated(EnumType.STRING)
    @Column(name = "aqi_category", nullable = false)
    private AqiCategory aqiCategory;

    @Column(nullable = false)
    private Instant timestamp;
}
