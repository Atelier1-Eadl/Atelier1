package com.smartcity.iot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "traffic_readings", indexes = {
        @Index(name = "idx_tr_sensor_timestamp", columnList = "sensor_id, timestamp DESC")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrafficReading {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "sensor_id", nullable = false)
    private UUID sensorId;

    @Column(name = "vehicle_count", nullable = false)
    private Integer vehicleCount;

    @Column(name = "average_speed", nullable = false)
    private Double averageSpeed;

    @Column(name = "occupancy_rate", nullable = false)
    private Double occupancyRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "congestion_level", nullable = false)
    private CongestionLevel congestionLevel;

    @Column(name = "car_count", nullable = false)
    private Integer carCount;

    @Column(name = "truck_count", nullable = false)
    private Integer truckCount;

    @Column(name = "motorcycle_count", nullable = false)
    private Integer motorcycleCount;

    @Column(nullable = false)
    private Instant timestamp;
}
