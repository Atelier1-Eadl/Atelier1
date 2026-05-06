package com.smartcity.iot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("IoT Sensor Monitoring Service")
                        .description("Smart City IoT sensor monitoring and simulation microservice")
                        .version("1.0.0")
                        .contact(new Contact().name("SmartCity Platform")))
                .servers(List.of(
                        new Server().url("http://localhost:8081").description("Local development"),
                        new Server().url("http://gateway:8080/iot").description("Via Gateway")
                ));
    }
}
