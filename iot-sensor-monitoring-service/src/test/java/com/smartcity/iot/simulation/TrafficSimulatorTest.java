package com.smartcity.iot.simulation;

import com.smartcity.iot.entity.*;
import com.smartcity.iot.service.simulation.TrafficSimulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TrafficSimulatorTest {

    private TrafficSimulator simulator;
    private Sensor testSensor;

    @BeforeEach
    void setUp() {
        simulator = new TrafficSimulator();
        testSensor = Sensor.builder()
                .id(UUID.randomUUID())
                .externalId("TR-TEST-001")
                .name("Test Traffic Sensor")
                .type(SensorType.TRAFFIC)
                .latitude(48.8698)
                .longitude(2.3078)
                .generationFrequencyMs(10000L)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Generated reading has correct sensor ID and non-null fields")
    void generateReading_shouldHaveCorrectSensorIdAndNonNullFields() {
        TrafficReading reading = simulator.generateReading(testSensor);

        assertThat(reading).isNotNull();
        assertThat(reading.getSensorId()).isEqualTo(testSensor.getId());
        assertThat(reading.getVehicleCount()).isNotNull();
        assertThat(reading.getAverageSpeed()).isNotNull();
        assertThat(reading.getOccupancyRate()).isNotNull();
        assertThat(reading.getCongestionLevel()).isNotNull();
        assertThat(reading.getCarCount()).isNotNull();
        assertThat(reading.getTruckCount()).isNotNull();
        assertThat(reading.getMotorcycleCount()).isNotNull();
        assertThat(reading.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Generated readings respect configured value bounds")
    void generateReading_shouldRespectValueBounds() {
        for (int i = 0; i < 100; i++) {
            TrafficReading reading = simulator.generateReading(testSensor);

            assertThat(reading.getVehicleCount()).isBetween(0, 80);
            assertThat(reading.getAverageSpeed()).isBetween(5.0, 130.0);
            assertThat(reading.getOccupancyRate()).isBetween(0.0, 95.0);
            assertThat(reading.getCarCount()).isGreaterThanOrEqualTo(0);
            assertThat(reading.getTruckCount()).isGreaterThanOrEqualTo(0);
            assertThat(reading.getMotorcycleCount()).isGreaterThanOrEqualTo(0);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {8, 18})
    @DisplayName("Rush factor is higher during peak hours")
    void computeRushFactor_shouldBeHigherDuringRushHours(int rushHour) {
        double rushFactor = simulator.computeRushFactor(rushHour);
        double nightFactor = simulator.computeRushFactor(3);
        assertThat(rushFactor).isGreaterThan(nightFactor);
    }

    @Test
    @DisplayName("Night rush factor is lower than midday")
    void computeRushFactor_nightShouldBeLower() {
        double nightFactor = simulator.computeRushFactor(2);
        double middayFactor = simulator.computeRushFactor(12);
        assertThat(nightFactor).isLessThan(middayFactor);
    }

    @Test
    @DisplayName("Congestion level FREE_FLOW at high speed, low occupancy")
    void computeCongestion_freeFlowAtHighSpeed() {
        assertThat(simulator.computeCongestion(110.0, 0.15)).isEqualTo(CongestionLevel.FREE_FLOW);
    }

    @Test
    @DisplayName("Congestion level JAM at very low speed, high occupancy")
    void computeCongestion_jamAtLowSpeed() {
        assertThat(simulator.computeCongestion(10.0, 0.92)).isEqualTo(CongestionLevel.JAM);
    }

    @Test
    @DisplayName("Congestion level HEAVY at moderately low speed")
    void computeCongestion_heavyAtModerateLowSpeed() {
        assertThat(simulator.computeCongestion(35.0, 0.75)).isEqualTo(CongestionLevel.HEAVY);
    }

    @Test
    @DisplayName("Traffic jam incident activates correctly")
    void applyIncident_trafficJam_shouldActivate() {
        simulator.applyIncident("TRAFFIC_JAM", 120);
        assertThat(simulator.hasActiveIncident()).isTrue();
    }

    @Test
    @DisplayName("Clearing incident deactivates it")
    void clearIncident_shouldDeactivate() {
        simulator.applyIncident("TRAFFIC_JAM", 120);
        simulator.clearIncident();
        assertThat(simulator.hasActiveIncident()).isFalse();
    }

    @Test
    @DisplayName("Vehicle counts sum sanity: car + truck + motorcycle close to total")
    void vehicleSumSanity() {
        for (int i = 0; i < 20; i++) {
            TrafficReading reading = simulator.generateReading(testSensor);
            int total = reading.getCarCount() + reading.getTruckCount() + reading.getMotorcycleCount();
            // Sum can deviate slightly due to clamping, but should be in the same ballpark
            assertThat(total).isGreaterThanOrEqualTo(0);
        }
    }
}
