package com.smartcity.iot.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    private final AppProperties appProperties;

    @Autowired
    public KafkaConfig(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Bean
    public NewTopic airQualityTelemetryTopic() {
        return TopicBuilder.name(appProperties.getKafka().getTopics().getAirQualityTelemetry())
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic trafficTelemetryTopic() {
        return TopicBuilder.name(appProperties.getKafka().getTopics().getTrafficTelemetry())
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic statusChangedTopic() {
        return TopicBuilder.name(appProperties.getKafka().getTopics().getStatusChanged())
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic alertTopic() {
        return TopicBuilder.name(appProperties.getKafka().getTopics().getAlert())
                .partitions(1)
                .replicas(1)
                .build();
    }
}
