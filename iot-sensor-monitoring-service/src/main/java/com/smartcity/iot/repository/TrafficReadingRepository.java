package com.smartcity.iot.repository;

import com.smartcity.iot.entity.CongestionLevel;
import com.smartcity.iot.entity.TrafficReading;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TrafficReadingRepository extends JpaRepository<TrafficReading, UUID> {

    Page<TrafficReading> findBySensorIdOrderByTimestampDesc(UUID sensorId, Pageable pageable);

    Optional<TrafficReading> findTopBySensorIdOrderByTimestampDesc(UUID sensorId);

    @Query("SELECT r.congestionLevel, COUNT(r) FROM TrafficReading r GROUP BY r.congestionLevel ORDER BY COUNT(r) DESC")
    java.util.List<Object[]> countByCongestionLevel();

    @Query("SELECT r.congestionLevel FROM TrafficReading r WHERE r.sensorId = :sensorId ORDER BY r.timestamp DESC LIMIT 1")
    Optional<CongestionLevel> findLatestCongestionLevelForSensor(@Param("sensorId") UUID sensorId);
}
