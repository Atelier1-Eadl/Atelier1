package com.smartcity.iot.integration;

import com.smartcity.iot.dto.CreateSensorRequest;
import com.smartcity.iot.entity.SensorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("null")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class SensorControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/v1/sensors returns 200 with list")
    void listSensors_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/sensors")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("POST /api/v1/sensors creates sensor and returns 201")
    void createSensor_shouldReturn201() throws Exception {
        CreateSensorRequest request = new CreateSensorRequest(
                "IT-TEST-001",
                "Integration Test Sensor",
                SensorType.AIR_QUALITY,
                48.8584,
                2.2945,
                "75007 Paris",
                10000L
        );

        mockMvc.perform(post("/api/v1/sensors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.externalId").value("IT-TEST-001"))
                .andExpect(jsonPath("$.type").value("AIR_QUALITY"));
    }

    @Test
    @DisplayName("GET /api/v1/sensors/{id} returns 404 for unknown ID")
    void getSensor_unknownId_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/sensors/00000000-0000-0000-0000-000000000000")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").exists());
    }

    @Test
    @DisplayName("POST /api/v1/sensors returns 400 for invalid payload")
    void createSensor_invalidPayload_shouldReturn400() throws Exception {
        String badJson = "{}";

        mockMvc.perform(post("/api/v1/sensors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(badJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").exists());
    }

    @Test
    @DisplayName("GET /api/v1/sensors/overview returns overview DTO")
    void getOverview_shouldReturnOverviewDto() throws Exception {
        mockMvc.perform(get("/api/v1/sensors/overview")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSensors").exists());
    }

    @Test
    @DisplayName("GET /api/v1/sensors?type=TRAFFIC filters by type")
    void listSensors_withTypeFilter_shouldFilter() throws Exception {
        mockMvc.perform(get("/api/v1/sensors")
                .param("type", "TRAFFIC")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
