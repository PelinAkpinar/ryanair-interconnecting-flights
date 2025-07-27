package com.ryanair.flights.integration;

import com.ryanair.flights.RyanairFlightsApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = RyanairFlightsApplication.class)
@AutoConfigureMockMvc
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    private static final String BASE_URL = "/ryanair/interconnections";

    private String departureDateTimeString;
    private String arrivalDateTimeString;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        this.departureDateTimeString = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        this.arrivalDateTimeString = now.plusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
    }

    @Test
    void testGetInterconnections() throws Exception {

        mockMvc.perform(get(BASE_URL)
                        .param("departure", "DUB")
                        .param("arrival", "WRO")
                        .param("departureDateTime", departureDateTimeString)
                        .param("arrivalDateTime", arrivalDateTimeString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetInterconnectionsWithNotCorrectlyFormattedTime() throws Exception {

        mockMvc.perform(get(BASE_URL)
                        .param("departure", "DUB")
                        .param("arrival", "WRO")
                        .param("departureDateTime", LocalDateTime.now().toString())
                        .param("arrivalDateTime", LocalDateTime.now().plusDays(2).toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetInterconnectionsWithInvalidParams() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("departure", "")
                        .param("arrival", "WRO"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetInterconnectionsNoResults() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("departure", "XYZ")
                        .param("arrival", "ABC")
                        .param("departureDateTime", departureDateTimeString)
                        .param("arrivalDateTime", arrivalDateTimeString))
                .andExpect(status().isNotFound());
    }
}