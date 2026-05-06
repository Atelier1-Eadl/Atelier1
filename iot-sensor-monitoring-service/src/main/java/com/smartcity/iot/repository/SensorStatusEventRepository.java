package com.smartcity.iot.repository;

import com.smartcity.iot.entity.SensorStatusEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SensorStatusEventRepository extends JpaRepository<SensorStatusEvent, UUID> {

    Page<SensorStatusEvent> findBySensorIdOrderByOccurredAtDesc(UUID sensorId, Pageable pageable);

    List<SensorStatusEvent> findTop10BySensorIdOrderByOccurredAtDesc(UUID sensorId);
}
