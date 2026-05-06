package com.smartcity.iot.controller;

import com.smartcity.iot.repository.AirQualityReadingRepository;
import com.smartcity.iot.repository.TrafficReadingRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Map;

/**
 * Server-Sent Events endpoint for real-time sensor data streaming.
 * Uses WebFlux Flux even within a Spring MVC application (mixed mode).
 */
@RestController
@RequestMapping("/api/v1/sensors")
@Tag(name = "SSE Stream", description = "Flux SSE temps reel des mesures capteurs")
@Slf4j
@SuppressWarnings("null")
public class SseController {

    private final AirQualityReadingRepository airQualityReadingRepository;
    private final TrafficReadingRepository trafficReadingRepository;

    @Autowired
    public SseController(AirQualityReadingRepository airQualityReadingRepository,
                          TrafficReadingRepository trafficReadingRepository) {
        this.airQualityReadingRepository = airQualityReadingRepository;
        this.trafficReadingRepository = trafficReadingRepository;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Flux SSE - toutes les lectures en temps reel (polling toutes les 5s)")
    public Flux<ServerSentEvent<Object>> streamAll() {
        return Flux.interval(Duration.ofSeconds(5))
                .flatMap(tick -> {
                    Flux<ServerSentEvent<Object>> airEvents = Flux.fromIterable(
                            airQualityReadingRepository.findAll(
                                    org.springframework.data.domain.PageRequest.of(0, 5,
                                            org.springframework.data.domain.Sort.by("timestamp").descending())
                            ).getContent()
                    ).map(reading -> ServerSentEvent.builder()
                            .event("air-quality")
                            .id(reading.getId().toString())
                            .data((Object) Map.of(
                                    "sensorId", reading.getSensorId(),
                                    "aqi", reading.getAqi(),
                                    "aqiCategory", reading.getAqiCategory(),
                                    "pm25", reading.getPm25(),
                                    "timestamp", reading.getTimestamp()
                            ))
                            .build());

                    Flux<ServerSentEvent<Object>> trafficEvents = Flux.fromIterable(
                            trafficReadingRepository.findAll(
                                    org.springframework.data.domain.PageRequest.of(0, 5,
                                            org.springframework.data.domain.Sort.by("timestamp").descending())
                            ).getContent()
                    ).map(reading -> ServerSentEvent.builder()
                            .event("traffic")
                            .id(reading.getId().toString())
                            .data((Object) Map.of(
                                    "sensorId", reading.getSensorId(),
                                    "congestionLevel", reading.getCongestionLevel(),
                                    "averageSpeed", reading.getAverageSpeed(),
                                    "vehicleCount", reading.getVehicleCount(),
                                    "timestamp", reading.getTimestamp()
                            ))
                            .build());

                    return Flux.concat(airEvents, trafficEvents);
                })
                .doOnSubscribe(s -> log.info("SSE client connected"))
                .doOnCancel(() -> log.info("SSE client disconnected"))
                .onErrorResume(ex -> {
                    log.error("SSE stream error: {}", ex.getMessage());
                    return Flux.empty();
                });
    }
}
