package com.smartcity.iot.service.simulation;

import com.smartcity.iot.entity.CongestionLevel;
import com.smartcity.iot.entity.Sensor;
import com.smartcity.iot.entity.TrafficReading;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Simulates traffic readings with realistic daily and weekly patterns.
 * Morning peak 7-9h, evening peak 17-19h, reduced on weekends.
 */
@Component
@Scope("prototype")
public class TrafficSimulator implements SensorSimulator<TrafficReading> {

    private static final Random RANDOM = new Random();
    private static final ZoneId PARIS_TZ = ZoneId.of("Europe/Paris");

    private final AtomicReference<Double> prevSpeed = new AtomicReference<>(90.0);
    private final AtomicReference<Double> prevOccupancy = new AtomicReference<>(0.2);
    private final AtomicReference<Integer> prevVehicleCount = new AtomicReference<>(20);

    private final AtomicBoolean incidentActive = new AtomicBoolean(false);
    private final AtomicInteger incidentRemainingSeconds = new AtomicInteger(0);

    @Override
    public TrafficReading generateReading(Sensor sensor) {
        LocalDateTime now = LocalDateTime.now(PARIS_TZ);
        int hour = now.getHour();
        DayOfWeek day = now.getDayOfWeek();

        double weekendFactor = isWeekend(day) ? 0.45 : 1.0;
        double rushFactor = computeRushFactor(hour);
        double loadFactor = weekendFactor * rushFactor;

        int vehicleCount = evolveInt(prevVehicleCount, (int) (60 * loadFactor + gaussian(5)), 0, 80);
        double occupancyRate = evolve(prevOccupancy, clamp(0.1 + 0.75 * loadFactor + gaussian(0.05), 0.0, 0.95), 0.0, 0.95);
        double averageSpeed = computeSpeed(occupancyRate);

        if (incidentActive.get()) {
            int remaining = incidentRemainingSeconds.decrementAndGet();
            occupancyRate = clamp(occupancyRate * 1.8 + gaussian(0.05), 0.0, 0.95);
            averageSpeed = clamp(averageSpeed * 0.3, 5.0, 130.0);
            vehicleCount = clamp(vehicleCount + (int) gaussian(8), 0, 80);
            if (remaining <= 0) {
                incidentActive.set(false);
            }
        }

        CongestionLevel congestion = computeCongestion(averageSpeed, occupancyRate);

        int totalVehicles = vehicleCount;
        int carCount = (int) (totalVehicles * (0.75 + gaussian(0.05)));
        int truckCount = (int) (totalVehicles * (0.15 + gaussian(0.03)));
        int motorcycleCount = Math.max(0, totalVehicles - carCount - truckCount);

        return TrafficReading.builder()
                .sensorId(sensor.getId())
                .vehicleCount(vehicleCount)
                .averageSpeed(round2(averageSpeed))
                .occupancyRate(round2(occupancyRate * 100.0))
                .congestionLevel(congestion)
                .carCount(Math.max(0, carCount))
                .truckCount(Math.max(0, truckCount))
                .motorcycleCount(Math.max(0, motorcycleCount))
                .timestamp(Instant.now())
                .build();
    }

    @Override
    public void applyIncident(String incidentType, int durationSeconds) {
        if ("TRAFFIC_JAM".equals(incidentType)) {
            incidentRemainingSeconds.set(durationSeconds);
            incidentActive.set(true);
        }
    }

    @Override
    public void clearIncident() {
        incidentActive.set(false);
        incidentRemainingSeconds.set(0);
    }

    @Override
    public boolean hasActiveIncident() {
        return incidentActive.get();
    }

    /**
     * Rush hour factor: peaks at 8h (morning) and 18h (evening).
     */
    public double computeRushFactor(int hour) {
        double morningPeak = gaussianPeak(hour, 8.0, 1.2);
        double eveningPeak = gaussianPeak(hour, 18.0, 1.0);
        double nightDiscount = (hour < 5 || hour >= 23) ? 0.05 : 0.0;
        return Math.max(nightDiscount, 0.3 + 0.7 * Math.max(morningPeak, eveningPeak));
    }

    /**
     * Speed inversely correlates with occupancy rate.
     */
    private double computeSpeed(double occupancy) {
        double baseSpeed = 130.0 * (1.0 - Math.pow(occupancy, 0.6));
        double prev = prevSpeed.get();
        double smoothed = 0.7 * prev + 0.3 * baseSpeed;
        double clamped = clamp(smoothed + gaussian(3.0), 5.0, 130.0);
        prevSpeed.set(clamped);
        return clamped;
    }

    /**
     * Congestion derived from speed and occupancy rate.
     */
    public CongestionLevel computeCongestion(double speed, double occupancy) {
        if (speed >= 90 && occupancy < 0.25) return CongestionLevel.FREE_FLOW;
        if (speed >= 70 && occupancy < 0.45) return CongestionLevel.LIGHT;
        if (speed >= 50 && occupancy < 0.65) return CongestionLevel.MODERATE;
        if (speed >= 25 && occupancy < 0.80) return CongestionLevel.HEAVY;
        return CongestionLevel.JAM;
    }

    private boolean isWeekend(DayOfWeek day) {
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    private double evolve(AtomicReference<Double> prev, double target, double min, double max) {
        double smoothed = 0.7 * prev.get() + 0.3 * target;
        double clamped = clamp(smoothed, min, max);
        prev.set(clamped);
        return clamped;
    }

    private int evolveInt(AtomicReference<Integer> prev, int target, int min, int max) {
        int smoothed = (int) (0.7 * prev.get() + 0.3 * target);
        int clamped = clamp(smoothed, min, max);
        prev.set(clamped);
        return clamped;
    }

    private double gaussian(double stddev) {
        return RANDOM.nextGaussian() * stddev;
    }

    private double gaussianPeak(double x, double mean, double sigma) {
        return Math.exp(-0.5 * Math.pow((x - mean) / sigma, 2));
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
