package com.smartcity.iot.service;

import com.smartcity.iot.dto.AirQualityReadingDto;
import com.smartcity.iot.dto.PagedResponse;
import com.smartcity.iot.entity.SensorType;
import com.smartcity.iot.exception.SensorNotFoundException;
import com.smartcity.iot.mapper.AirQualityMapper;
import com.smartcity.iot.repository.AirQualityReadingRepository;
import com.smartcity.iot.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@SuppressWarnings("null")
@Service
public class AirQualityService {

    private final AirQualityReadingRepository repository;
    private final SensorRepository sensorRepository;
    private final AirQualityMapper mapper;

    @Autowired
    public AirQualityService(AirQualityReadingRepository repository,
                              SensorRepository sensorRepository,
                              AirQualityMapper mapper) {
        this.repository = repository;
        this.sensorRepository = sensorRepository;
        this.mapper = mapper;
    }

    public PagedResponse<AirQualityReadingDto> getReadings(UUID sensorId, int page, int size) {
        verifySensorExists(sensorId, SensorType.AIR_QUALITY);
        Pageable pageable = PageRequest.of(page, size);
        return PagedResponse.from(
                repository.findBySensorIdOrderByTimestampDesc(sensorId, pageable)
                          .map(mapper::toDto)
        );
    }

    public AirQualityReadingDto getLatest(UUID sensorId) {
        verifySensorExists(sensorId, SensorType.AIR_QUALITY);
        return repository.findTopBySensorIdOrderByTimestampDesc(sensorId)
                .map(mapper::toDto)
                .orElseThrow(() -> new IllegalStateException("Aucune mesure disponible pour ce capteur"));
    }

    private void verifySensorExists(UUID sensorId, SensorType expectedType) {
        sensorRepository.findById(sensorId)
                .filter(s -> s.getType() == expectedType)
                .orElseThrow(() -> new SensorNotFoundException(sensorId));
    }
}
