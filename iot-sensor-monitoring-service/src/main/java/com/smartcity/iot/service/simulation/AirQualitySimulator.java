package com.smartcity.iot.service.simulation;

import com.smartcity.iot.entity.AirQualityReading;
import com.smartcity.iot.entity.AqiCategory;
import com.smartcity.iot.entity.Sensor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Simulates air quality readings with realistic daily patterns and inertia.
 * Values evolve progressively - no abrupt jumps between readings.
 */
@Component
@Scope("prototype")
public class AirQualitySimulator implements SensorSimulator<AirQualityReading> {

    private static final Random RANDOM = new Random();

    // Inertia: retained previous values to smooth transitions
    private final AtomicReference<Double> prevPm25 = new AtomicReference<>(15.0);
    private final AtomicReference<Double> prevPm10 = new AtomicReference<>(25.0);
    private final AtomicReference<Double> prevNo2 = new AtomicReference<>(30.0);
    private final AtomicReference<Double> prevO3 = new AtomicReference<>(50.0);
    private final AtomicReference<Double> prevCo = new AtomicReference<>(0.5);

    private final AtomicBoolean incidentActive = new AtomicBoolean(false);
    private final AtomicInteger incidentRemainingSeconds = new AtomicInteger(0);

    @Override
    public AirQualityReading generateReading(Sensor sensor) {
        int hourOfDay = LocalTime.now(ZoneId.of("Europe/Paris")).getHour();
        double trafficFactor = computeTrafficFactor(hourOfDay);

        double pm25 = evolve(prevPm25, basePm25(trafficFactor), 5.0, 150.0);
        double pm10 = evolve(prevPm10, basePm10(trafficFactor), 10.0, 200.0);
        double no2 = evolve(prevNo2, baseNo2(trafficFactor), 10.0, 250.0);
        double o3 = evolve(prevO3, baseO3(hourOfDay), 20.0, 180.0);
        double co = evolve(prevCo, baseCo(trafficFactor), 0.2, 10.0);
        double temperature = computeTemperature(hourOfDay);
        double humidity = clamp(50.0 + gaussian(15.0), 30.0, 95.0);

        if (incidentActive.get()) {
            int remaining = incidentRemainingSeconds.decrementAndGet();
            pm25 = clamp(pm25 * 3.0 + gaussian(20.0), 5.0, 300.0);
            pm10 = clamp(pm10 * 2.5 + gaussian(30.0), 10.0, 400.0);
            no2 = clamp(no2 * 2.0 + gaussian(20.0), 10.0, 300.0);
            if (remaining <= 0) {
                incidentActive.set(false);
            }
        }

        int aqi = computeAqi(pm25, pm10, no2, o3, co);
        AqiCategory category = classifyAqi(aqi);

        return AirQualityReading.builder()
                .sensorId(sensor.getId())
                .pm25(round2(pm25))
                .pm10(round2(pm10))
                .no2(round2(no2))
                .o3(round2(o3))
                .co(round2(co))
                .temperature(round2(temperature))
                .humidity(round2(humidity))
                .aqi(aqi)
                .aqiCategory(category)
                .timestamp(Instant.now())
                .build();
    }

    @Override
    public void applyIncident(String incidentType, int durationSeconds) {
        if ("POLLUTION_SPIKE".equals(incidentType)) {
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
     * Traffic factor: 1.0 = baseline, higher during rush hours.
     * Morning peak 7-9h, evening peak 17-19h.
     */
    public double computeTrafficFactor(int hour) {
        if (hour >= 7 && hour <= 9) return 1.0 + 0.5 * gaussianPeak(hour, 8, 1.5);
        if (hour >= 17 && hour <= 19) return 1.0 + 0.4 * gaussianPeak(hour, 18, 1.2);
        if (hour >= 22 || hour < 5) return 0.4;
        return 0.8;
    }

    private double basePm25(double trafficFactor) {
        return clamp(12.0 * trafficFactor + gaussian(3.0), 5.0, 150.0);
    }

    private double basePm10(double trafficFactor) {
        return clamp(20.0 * trafficFactor + gaussian(5.0), 10.0, 200.0);
    }

    private double baseNo2(double trafficFactor) {
        return clamp(35.0 * trafficFactor + gaussian(8.0), 10.0, 250.0);
    }

    private double baseO3(int hour) {
        // Ozone peaks in early afternoon due to photochemistry
        double base = 40.0 + 50.0 * gaussianPeak(hour, 14, 3.0);
        return clamp(base + gaussian(5.0), 20.0, 180.0);
    }

    private double baseCo(double trafficFactor) {
        return clamp(0.5 * trafficFactor + gaussian(0.1), 0.2, 10.0);
    }

    private double computeTemperature(int hour) {
        // Daily cycle: min at 5h (~8C), max at 14h (~22C) for a Paris spring day
        double base = 15.0 + 7.0 * Math.sin(Math.PI * (hour - 5) / 18.0);
        return round2(base + gaussian(0.8));
    }

    /**
     * Smoothed value evolution: blends previous value with new target (80/20 inertia).
     */
    private double evolve(AtomicReference<Double> prev, double target, double min, double max) {
        double smoothed = 0.75 * prev.get() + 0.25 * target;
        double clamped = clamp(smoothed, min, max);
        prev.set(clamped);
        return clamped;
    }

    /**
     * European AQI computation based on worst sub-index.
     */
    public int computeAqi(double pm25, double pm10, double no2, double o3, double co) {
        int aqiPm25 = scaleToAqi(pm25, new double[]{0, 10, 20, 25, 50, 75, 800});
        int aqiPm10 = scaleToAqi(pm10, new double[]{0, 20, 40, 50, 100, 150, 1200});
        int aqiNo2 = scaleToAqi(no2, new double[]{0, 40, 90, 120, 230, 340, 1000});
        int aqiO3 = scaleToAqi(o3, new double[]{0, 50, 100, 130, 240, 380, 800});
        int aqiCo = scaleToAqi(co, new double[]{0, 2, 4, 6, 10, 15, 100});
        return Math.max(Math.max(Math.max(aqiPm25, aqiPm10), Math.max(aqiNo2, aqiO3)), aqiCo);
    }

    private int scaleToAqi(double value, double[] breakpoints) {
        // Returns 0-500 index, 0=excellent, 500=extremely poor
        int[] aqiBreaks = {0, 50, 100, 150, 200, 300, 500};
        for (int i = 0; i < breakpoints.length - 1; i++) {
            if (value <= breakpoints[i + 1]) {
                double ratio = (value - breakpoints[i]) / (breakpoints[i + 1] - breakpoints[i]);
                return (int) (aqiBreaks[i] + ratio * (aqiBreaks[i + 1] - aqiBreaks[i]));
            }
        }
        return 500;
    }

    public AqiCategory classifyAqi(int aqi) {
        if (aqi <= 50) return AqiCategory.GOOD;
        if (aqi <= 100) return AqiCategory.FAIR;
        if (aqi <= 150) return AqiCategory.MODERATE;
        if (aqi <= 200) return AqiCategory.POOR;
        if (aqi <= 300) return AqiCategory.VERY_POOR;
        return AqiCategory.EXTREMELY_POOR;
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

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
