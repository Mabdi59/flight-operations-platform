package com.flightops.platform.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightops.platform.domain.FlightStatus;
import com.flightops.platform.dto.FlightRequest;
import com.flightops.platform.dto.FlightStatusUpdateRequest;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FlightControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateAndUpdateFlightStatus() throws Exception {
        FlightRequest request = new FlightRequest(
                "AZ999",
                "SFO",
                "DEN",
                LocalDateTime.of(2026, 8, 23, 10, 0),
                LocalDateTime.of(2026, 8, 23, 13, 0),
                null,
                null,
                FlightStatus.SCHEDULED,
                0);

        String createdFlight = mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flightNumber").value("AZ999"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long flightId = objectMapper.readTree(createdFlight).get("id").asLong();

        mockMvc.perform(patch("/api/flights/{id}/status", flightId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new FlightStatusUpdateRequest(
                                FlightStatus.DELAYED,
                                25,
                                null,
                                null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELAYED"))
                .andExpect(jsonPath("$.delayMinutes").value(25));

        mockMvc.perform(get("/api/flights/{id}", flightId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber").value("AZ999"))
                .andExpect(jsonPath("$.status").value("DELAYED"));
    }
}
