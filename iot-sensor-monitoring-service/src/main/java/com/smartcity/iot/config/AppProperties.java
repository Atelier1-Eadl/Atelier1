package com.smartcity.iot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

    private Simulation simulation = new Simulation();
    private Kafka kafka = new Kafka();
    private Redis redis = new Redis();

    @Getter
    @Setter
    public static class Simulation {
        private boolean enabled = true;
        private long defaultFrequencyMs = 10000;
        private long offlineThresholdSeconds = 60;
    }

    @Getter
    @Setter
    public static class Kafka {
        private Topics topics = new Topics();

        @Getter
        @Setter
        public static class Topics {
            private String airQualityTelemetry = "sensor.telemetry.air-quality";
            private String trafficTelemetry = "sensor.telemetry.traffic";
            private String statusChanged = "sensor.status.changed";
            private String alert = "sensor.alert";
        }
    }

    @Getter
    @Setter
    public static class Redis {
        private long sensorStatusTtlSeconds = 120;
    }
}
