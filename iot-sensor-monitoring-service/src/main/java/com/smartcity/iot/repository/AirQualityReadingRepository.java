package com.smartcity.iot.repository;

import com.smartcity.iot.entity.AirQualityReading;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AirQualityReadingRepository extends JpaRepository<AirQualityReading, UUID> {

    Page<AirQualityReading> findBySensorIdOrderByTimestampDesc(UUID sensorId, Pageable pageable);

    Optional<AirQualityReading> findTopBySensorIdOrderByTimestampDesc(UUID sensorId);

    @Query("SELECT AVG(r.aqi) FROM AirQualityReading r")
    Double computeAverageAqi();

    @Query("SELECT AVG(r.aqi) FROM AirQualityReading r WHERE r.sensorId = :sensorId AND r.timestamp >= :since")
    Double computeAverageAqiForSensor(@Param("sensorId") UUID sensorId,
                                      @Param("since") java.time.Instant since);
}
