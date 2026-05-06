package com.smartcity.iot.service;

import com.smartcity.iot.dto.SensorOverviewDto;
import com.smartcity.iot.entity.CongestionLevel;
import com.smartcity.iot.entity.SensorStatus;
import com.smartcity.iot.entity.SensorType;
import com.smartcity.iot.repository.AirQualityReadingRepository;
import com.smartcity.iot.repository.SensorRepository;
import com.smartcity.iot.repository.TrafficReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OverviewService {

    private final SensorRepository sensorRepository;
    private final AirQualityReadingRepository airQualityReadingRepository;
    private final TrafficReadingRepository trafficReadingRepository;

    @Autowired
    public OverviewService(SensorRepository sensorRepository,
                           AirQualityReadingRepository airQualityReadingRepository,
                           TrafficReadingRepository trafficReadingRepository) {
        this.sensorRepository = sensorRepository;
        this.airQualityReadingRepository = airQualityReadingRepository;
        this.trafficReadingRepository = trafficReadingRepository;
    }

    public SensorOverviewDto getOverview() {
        long totalSensors = sensorRepository.count();

        long aqOnline = sensorRepository.countByTypeAndStatus(SensorType.AIR_QUALITY, SensorStatus.ONLINE);
        long aqOffline = sensorRepository.countByTypeAndStatus(SensorType.AIR_QUALITY, SensorStatus.OFFLINE);
        long aqDegraded = sensorRepository.countByTypeAndStatus(SensorType.AIR_QUALITY, SensorStatus.DEGRADED);

        long trOnline = sensorRepository.countByTypeAndStatus(SensorType.TRAFFIC, SensorStatus.ONLINE);
        long trOffline = sensorRepository.countByTypeAndStatus(SensorType.TRAFFIC, SensorStatus.OFFLINE);
        long trDegraded = sensorRepository.countByTypeAndStatus(SensorType.TRAFFIC, SensorStatus.DEGRADED);

        Double avgAqi = airQualityReadingRepository.computeAverageAqi();
        String aqiCategory = avgAqi != null ? classifyAvgAqi(avgAqi.intValue()) : "N/A";

        CongestionLevel dominant = resolveDominantCongestion();

        return new SensorOverviewDto(
                totalSensors,
                aqOnline, aqOffline, aqDegraded,
                trOnline, trOffline, trDegraded,
                avgAqi != null ? Math.round(avgAqi * 10.0) / 10.0 : null,
                aqiCategory,
                dominant
        );
    }

    private String classifyAvgAqi(int aqi) {
        if (aqi <= 50) return "BON";
        if (aqi <= 100) return "MOYEN";
        if (aqi <= 150) return "DEGRADE";
        if (aqi <= 200) return "MAUVAIS";
        if (aqi <= 300) return "TRES_MAUVAIS";
        return "EXTREMEMENT_MAUVAIS";
    }

    private CongestionLevel resolveDominantCongestion() {
        List<Object[]> results = trafficReadingRepository.countByCongestionLevel();
        if (results.isEmpty()) return CongestionLevel.FREE_FLOW;
        Object rawLevel = results.get(0)[0];
        if (rawLevel instanceof CongestionLevel cl) return cl;
        return CongestionLevel.FREE_FLOW;
    }
}
