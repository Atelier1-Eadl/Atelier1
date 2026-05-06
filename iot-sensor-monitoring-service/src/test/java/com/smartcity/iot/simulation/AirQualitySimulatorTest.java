package com.smartcity.iot.simulation;

import com.smartcity.iot.entity.*;
import com.smartcity.iot.service.simulation.AirQualitySimulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AirQualitySimulatorTest {

    private AirQualitySimulator simulator;
    private Sensor testSensor;

    @BeforeEach
    void setUp() {
        simulator = new AirQualitySimulator();
        testSensor = Sensor.builder()
                .id(UUID.randomUUID())
                .externalId("AQ-TEST-001")
                .name("Test Air Quality Sensor")
                .type(SensorType.AIR_QUALITY)
                .latitude(48.8584)
                .longitude(2.2945)
                .generationFrequencyMs(10000L)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Generated reading has correct sensor ID and non-null fields")
    void generateReading_shouldHaveCorrectSensorIdAndNonNullFields() {
        AirQualityReading reading = simulator.generateReading(testSensor);

        assertThat(reading).isNotNull();
        assertThat(reading.getSensorId()).isEqualTo(testSensor.getId());
        assertThat(reading.getPm25()).isNotNull();
        assertThat(reading.getPm10()).isNotNull();
        assertThat(reading.getNo2()).isNotNull();
        assertThat(reading.getO3()).isNotNull();
        assertThat(reading.getCo()).isNotNull();
        assertThat(reading.getTemperature()).isNotNull();
        assertThat(reading.getHumidity()).isNotNull();
        assertThat(reading.getAqi()).isNotNull();
        assertThat(reading.getAqiCategory()).isNotNull();
        assertThat(reading.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Generated readings respect configured value bounds")
    void generateReading_shouldRespectValueBounds() {
        for (int i = 0; i < 100; i++) {
            AirQualityReading reading = simulator.generateReading(testSensor);

            assertThat(reading.getPm25()).isBetween(5.0, 300.0);
            assertThat(reading.getPm10()).isBetween(10.0, 200.0);
            assertThat(reading.getNo2()).isBetween(10.0, 250.0);
            assertThat(reading.getO3()).isBetween(20.0, 180.0);
            assertThat(reading.getCo()).isBetween(0.2, 10.0);
            assertThat(reading.getHumidity()).isBetween(30.0, 95.0);
            assertThat(reading.getAqi()).isBetween(0, 500);
        }
    }

    @Test
    @DisplayName("AQI category matches AQI index correctly")
    void classifyAqi_shouldReturnCorrectCategories() {
        assertThat(simulator.classifyAqi(25)).isEqualTo(AqiCategory.GOOD);
        assertThat(simulator.classifyAqi(75)).isEqualTo(AqiCategory.FAIR);
        assertThat(simulator.classifyAqi(125)).isEqualTo(AqiCategory.MODERATE);
        assertThat(simulator.classifyAqi(175)).isEqualTo(AqiCategory.POOR);
        assertThat(simulator.classifyAqi(250)).isEqualTo(AqiCategory.VERY_POOR);
        assertThat(simulator.classifyAqi(400)).isEqualTo(AqiCategory.EXTREMELY_POOR);
    }

    @ParameterizedTest
    @ValueSource(ints = {8, 18})
    @DisplayName("Traffic factor is higher during rush hours (7-9h, 17-19h)")
    void computeTrafficFactor_shouldBeHigherDuringRushHours(int rushHour) {
        double rushFactor = simulator.computeTrafficFactor(rushHour);
        double nightFactor = simulator.computeTrafficFactor(3);

        assertThat(rushFactor).isGreaterThan(nightFactor);
    }

    @Test
    @DisplayName("Night traffic factor is lower than daytime")
    void computeTrafficFactor_nightShouldBeLower() {
        double nightFactor = simulator.computeTrafficFactor(3);
        double dayFactor = simulator.computeTrafficFactor(12);

        assertThat(nightFactor).isLessThan(dayFactor);
    }

    @Test
    @DisplayName("Pollution spike incident increases PM2.5 values")
    void applyIncident_pollutionSpike_shouldIncreasePollutionValues() {
        AirQualityReading baseReading = simulator.generateReading(testSensor);

        simulator.applyIncident("POLLUTION_SPIKE", 300);
        assertThat(simulator.hasActiveIncident()).isTrue();

        double totalPm25After = 0;
        int samples = 10;
        for (int i = 0; i < samples; i++) {
            totalPm25After += simulator.generateReading(testSensor).getPm25();
        }

        assertThat(totalPm25After / samples).isGreaterThan(baseReading.getPm25());
    }

    @Test
    @DisplayName("Clearing incident deactivates it")
    void clearIncident_shouldDeactivateIncident() {
        simulator.applyIncident("POLLUTION_SPIKE", 300);
        assertThat(simulator.hasActiveIncident()).isTrue();

        simulator.clearIncident();
        assertThat(simulator.hasActiveIncident()).isFalse();
    }

    @Test
    @DisplayName("Consecutive readings show inertia - no abrupt jumps")
    void consecutiveReadings_shouldShowInertia() {
        AirQualityReading first = simulator.generateReading(testSensor);
        AirQualityReading second = simulator.generateReading(testSensor);

        double pm25Delta = Math.abs(second.getPm25() - first.getPm25());
        double no2Delta = Math.abs(second.getNo2() - first.getNo2());

        assertThat(pm25Delta).isLessThan(50.0);
        assertThat(no2Delta).isLessThan(80.0);
    }
}
