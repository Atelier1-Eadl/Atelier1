package com.smartcity.iot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sensor_status_events", indexes = {
        @Index(name = "idx_sse_sensor_id", columnList = "sensor_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorStatusEvent {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "sensor_id", nullable = false)
    private UUID sensorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status")
    private SensorStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private SensorStatus newStatus;

    private String reason;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @PrePersist
    private void prePersist() {
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }
}
