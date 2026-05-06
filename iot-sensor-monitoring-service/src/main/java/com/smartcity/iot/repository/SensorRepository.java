package com.smartcity.iot.repository;

import com.smartcity.iot.entity.Sensor;
import com.smartcity.iot.entity.SensorStatus;
import com.smartcity.iot.entity.SensorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SensorRepository extends JpaRepository<Sensor, UUID> {

    List<Sensor> findAllByType(SensorType type);

    List<Sensor> findAllByActiveTrue();

    List<Sensor> findAllByActiveTrueAndType(SensorType type);

    Optional<Sensor> findByExternalId(String externalId);

    boolean existsByExternalId(String externalId);

    @Query("SELECT s FROM Sensor s WHERE s.active = true AND s.status != 'OFFLINE' AND s.lastSeenAt < :threshold")
    List<Sensor> findActiveSensorsLastSeenBefore(@Param("threshold") Instant threshold);

    @Query("SELECT COUNT(s) FROM Sensor s WHERE s.type = :type AND s.status = :status")
    long countByTypeAndStatus(@Param("type") SensorType type, @Param("status") SensorStatus status);

    @Query("SELECT s.status, COUNT(s) FROM Sensor s WHERE s.type = :type GROUP BY s.status")
    List<Object[]> countByStatusGroupedForType(@Param("type") SensorType type);
}
