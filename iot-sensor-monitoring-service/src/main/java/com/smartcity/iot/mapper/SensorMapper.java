package com.smartcity.iot.mapper;

import com.smartcity.iot.dto.CreateSensorRequest;
import com.smartcity.iot.dto.SensorDto;
import com.smartcity.iot.entity.Sensor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SensorMapper {

    SensorDto toDto(Sensor sensor);

    List<SensorDto> toDtoList(List<Sensor> sensors);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registeredAt", ignore = true)
    @Mapping(target = "lastSeenAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "generationFrequencyMs", defaultValue = "10000")
    Sensor toEntity(CreateSensorRequest request);
}
