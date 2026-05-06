package com.smartcity.iot.mapper;

import com.smartcity.iot.dto.AirQualityReadingDto;
import com.smartcity.iot.entity.AirQualityReading;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AirQualityMapper {

    AirQualityReadingDto toDto(AirQualityReading reading);

    List<AirQualityReadingDto> toDtoList(List<AirQualityReading> readings);
}
