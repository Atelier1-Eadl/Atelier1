package com.smartcity.iot.service;

import com.smartcity.iot.config.AppProperties;
import com.smartcity.iot.dto.CreateSensorRequest;
import com.smartcity.iot.dto.SensorDto;
import com.smartcity.iot.dto.SensorStatusDto;
import com.smartcity.iot.dto.UpdateSensorRequest;
import com.smartcity.iot.entity.Sensor;
import com.smartcity.iot.entity.SensorStatus;
import com.smartcity.iot.entity.SensorStatusEvent;
import com.smartcity.iot.entity.SensorType;
import com.smartcity.iot.exception.SensorAlreadyExistsException;
import com.smartcity.iot.exception.SensorNotFoundException;
import com.smartcity.iot.kafka.producer.SensorEventProducer;
import com.smartcity.iot.mapper.SensorMapper;
import com.smartcity.iot.repository.SensorRepository;
import com.smartcity.iot.repository.SensorStatusEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@SuppressWarnings("null")
public class SensorService {

    private static final String REDIS_STATUS_KEY_PREFIX = "sensor:status:";

    private final SensorRepository sensorRepository;
    private final SensorStatusEventRepository statusEventRepository;
    private final SensorMapper sensorMapper;
    private final SensorEventProducer eventProducer;
    private final RedisTemplate<String, Object> redisTemplate;
    private final AppProperties appProperties;

    @Autowired
    public SensorService(SensorRepository sensorRepository,
                         SensorStatusEventRepository statusEventRepository,
                         SensorMapper sensorMapper,
                         SensorEventProducer eventProducer,
                         RedisTemplate<String, Object> redisTemplate,
                         AppProperties appProperties) {
        this.sensorRepository = sensorRepository;
        this.statusEventRepository = statusEventRepository;
        this.sensorMapper = sensorMapper;
        this.eventProducer = eventProducer;
        this.redisTemplate = redisTemplate;
        this.appProperties = appProperties;
    }

    public List<SensorDto> findAll(SensorType type) {
        List<Sensor> sensors = (type != null)
                ? sensorRepository.findAllByType(type)
                : sensorRepository.findAll();
        return sensorMapper.toDtoList(sensors);
    }

    public SensorDto findById(UUID id) {
        return sensorMapper.toDto(requireSensor(id));
    }

    @Transactional
    public SensorDto create(CreateSensorRequest request) {
        if (sensorRepository.existsByExternalId(request.externalId())) {
            throw new SensorAlreadyExistsException(request.externalId());
        }
        Sensor sensor = sensorMapper.toEntity(request);
        if (sensor.getGenerationFrequencyMs() == null) {
            sensor.setGenerationFrequencyMs(appProperties.getSimulation().getDefaultFrequencyMs());
        }
        return sensorMapper.toDto(sensorRepository.save(sensor));
    }

    @Transactional
    public SensorDto update(UUID id, UpdateSensorRequest request) {
        Sensor sensor = requireSensor(id);
        if (request.name() != null) sensor.setName(request.name());
        if (request.address() != null) sensor.setAddress(request.address());
        if (request.generationFrequencyMs() != null) sensor.setGenerationFrequencyMs(request.generationFrequencyMs());
        if (request.active() != null) sensor.setActive(request.active());
        return sensorMapper.toDto(sensorRepository.save(sensor));
    }

    @Transactional
    public void delete(UUID id) {
        requireSensor(id);
        sensorRepository.deleteById(id);
        redisTemplate.delete(REDIS_STATUS_KEY_PREFIX + id);
    }

    public SensorStatusDto getStatus(UUID id) {
        String key = REDIS_STATUS_KEY_PREFIX + id;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof SensorStatusDto dto) {
            return dto;
        }
        Sensor sensor = requireSensor(id);
        return buildAndCacheStatus(sensor);
    }

    @Transactional
    public void updateStatus(UUID sensorId, SensorStatus newStatus, String reason) {
        Sensor sensor = requireSensor(sensorId);
        SensorStatus previous = sensor.getStatus();
        if (previous == newStatus) return;

        sensor.setStatus(newStatus);
        sensorRepository.save(sensor);

        SensorStatusEvent event = SensorStatusEvent.builder()
                .sensorId(sensorId)
                .previousStatus(previous)
                .newStatus(newStatus)
                .reason(reason)
                .occurredAt(Instant.now())
                .build();
        statusEventRepository.save(event);

        cacheStatus(sensor);
        eventProducer.publishStatusChanged(sensor, previous, reason);
        log.info("Sensor {} status changed: {} -> {} ({})", sensor.getExternalId(), previous, newStatus, reason);
    }

    private SensorStatusDto buildAndCacheStatus(Sensor sensor) {
        SensorStatusDto dto = new SensorStatusDto(
                sensor.getId(),
                sensor.getName(),
                sensor.getType(),
                sensor.getStatus(),
                sensor.getLastSeenAt(),
                Instant.now()
        );
        cacheStatus(sensor, dto);
        return dto;
    }

    private void cacheStatus(Sensor sensor) {
        cacheStatus(sensor, new SensorStatusDto(
                sensor.getId(), sensor.getName(), sensor.getType(),
                sensor.getStatus(), sensor.getLastSeenAt(), Instant.now()));
    }

    private void cacheStatus(Sensor sensor, SensorStatusDto dto) {
        String key = REDIS_STATUS_KEY_PREFIX + sensor.getId();
        long ttl = appProperties.getRedis().getSensorStatusTtlSeconds();
        redisTemplate.opsForValue().set(key, dto, Duration.ofSeconds(ttl));
    }

    private Sensor requireSensor(UUID id) {
        return sensorRepository.findById(id)
                .orElseThrow(() -> new SensorNotFoundException(id));
    }
}
