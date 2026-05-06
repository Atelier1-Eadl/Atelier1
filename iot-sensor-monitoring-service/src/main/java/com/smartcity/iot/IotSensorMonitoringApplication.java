package com.smartcity.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IotSensorMonitoringApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotSensorMonitoringApplication.class, args);
    }
}
