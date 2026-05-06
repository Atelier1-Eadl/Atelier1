package com.smartcity.iot.mapper;

import com.smartcity.iot.dto.TrafficReadingDto;
import com.smartcity.iot.entity.TrafficReading;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrafficMapper {

    TrafficReadingDto toDto(TrafficReading reading);

    List<TrafficReadingDto> toDtoList(List<TrafficReading> readings);
}
