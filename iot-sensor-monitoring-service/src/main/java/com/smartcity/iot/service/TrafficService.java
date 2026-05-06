package com.smartcity.iot.service;

import com.smartcity.iot.dto.PagedResponse;
import com.smartcity.iot.dto.TrafficReadingDto;
import com.smartcity.iot.entity.SensorType;
import com.smartcity.iot.exception.SensorNotFoundException;
import com.smartcity.iot.mapper.TrafficMapper;
import com.smartcity.iot.repository.SensorRepository;
import com.smartcity.iot.repository.TrafficReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@SuppressWarnings("null")
@Service
public class TrafficService {

    private final TrafficReadingRepository repository;
    private final SensorRepository sensorRepository;
    private final TrafficMapper mapper;

    @Autowired
    public TrafficService(TrafficReadingRepository repository,
                          SensorRepository sensorRepository,
                          TrafficMapper mapper) {
        this.repository = repository;
        this.sensorRepository = sensorRepository;
        this.mapper = mapper;
    }

    public PagedResponse<TrafficReadingDto> getReadings(UUID sensorId, int page, int size) {
        verifySensorExists(sensorId);
        Pageable pageable = PageRequest.of(page, size);
        return PagedResponse.from(
                repository.findBySensorIdOrderByTimestampDesc(sensorId, pageable)
                          .map(mapper::toDto)
        );
    }

    public TrafficReadingDto getLatest(UUID sensorId) {
        verifySensorExists(sensorId);
        return repository.findTopBySensorIdOrderByTimestampDesc(sensorId)
                .map(mapper::toDto)
                .orElseThrow(() -> new IllegalStateException("Aucune mesure disponible pour ce capteur"));
    }

    private void verifySensorExists(UUID sensorId) {
        sensorRepository.findById(sensorId)
                .filter(s -> s.getType() == SensorType.TRAFFIC)
                .orElseThrow(() -> new SensorNotFoundException(sensorId));
    }
}
