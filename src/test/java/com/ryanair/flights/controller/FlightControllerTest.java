package com.ryanair.flights.controller;

import com.ryanair.flights.exception.InvalidRequestException;
import com.ryanair.flights.model.dto.Flight;
import com.ryanair.flights.model.dto.FlightLeg;
import com.ryanair.flights.model.internal.FlightSearchCriteria;
import com.ryanair.flights.service.FlightService;
import com.ryanair.flights.util.FlightValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FlightController.class)
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightService flightService;


    private final String departure = "DUB";
    private final String arrival = "WRO";
    private String departureDateTime;
    private String arrivalDateTime;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        this.departureDateTime = now.format(FORMATTER);
        this.arrivalDateTime = now.plusDays(3).format(FORMATTER);
    }

    @Test
    void getInterconnectionsShouldReturnOkWhenFlightsAreFound() throws Exception {
        List<Flight> mockFlights = List.of(createMockFlight());
        when(flightService.searchFlights(any(FlightSearchCriteria.class))).thenReturn(mockFlights);

        try (MockedStatic<FlightValidator> mockedValidator = Mockito.mockStatic(FlightValidator.class)) {
            mockedValidator.when(() -> FlightValidator.validateSearchCriteria(any(FlightSearchCriteria.class))).then(invocation -> null);

            mockMvc.perform(get("/ryanair/interconnections")
                            .param("departure", departure)
                            .param("arrival", arrival)
                            .param("departureDateTime", departureDateTime)
                            .param("arrivalDateTime", arrivalDateTime))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.size()").value(1))
                    .andExpect(jsonPath("$[0].stops").value(0));

            mockedValidator.verify(() -> FlightValidator.validateSearchCriteria(any(FlightSearchCriteria.class)));
        }
        verify(flightService).searchFlights(any(FlightSearchCriteria.class));
    }

    @Test
    void getInterconnectionsShouldReturnNotFoundWhenNoFlightsAreFound() throws Exception {
        when(flightService.searchFlights(any(FlightSearchCriteria.class))).thenReturn(Collections.emptyList());

        try (MockedStatic<FlightValidator> mockedValidator = Mockito.mockStatic(FlightValidator.class)) {
            mockedValidator.when(() -> FlightValidator.validateSearchCriteria(any(FlightSearchCriteria.class))).then(invocation -> null);

            mockMvc.perform(get("/ryanair/interconnections")
                            .param("departure", departure)
                            .param("arrival", arrival)
                            .param("departureDateTime", departureDateTime)
                            .param("arrivalDateTime", arrivalDateTime))
                    .andExpect(status().isNotFound());

            mockedValidator.verify(() -> FlightValidator.validateSearchCriteria(any(FlightSearchCriteria.class)));
        }
        verify(flightService).searchFlights(any(FlightSearchCriteria.class));
    }

    @Test
    void getInterconnectionsShouldReturnBadRequestWhenValidationFails() throws Exception {
        String errorMessage = "Departure and arrival airports cannot be the same.";

        try (MockedStatic<FlightValidator> mockedValidator = Mockito.mockStatic(FlightValidator.class)) {
            mockedValidator.when(() -> FlightValidator.validateSearchCriteria(any(FlightSearchCriteria.class)))
                    .thenThrow(new InvalidRequestException(errorMessage));

            mockMvc.perform(get("/ryanair/interconnections")
                            .param("departure", departure)
                            .param("arrival", departure) // Use invalid params to match the error
                            .param("departureDateTime", departureDateTime)
                            .param("arrivalDateTime", arrivalDateTime))
                    .andExpect(status().isBadRequest());

            mockedValidator.verify(() -> FlightValidator.validateSearchCriteria(any(FlightSearchCriteria.class)));
        }
        verify(flightService, never()).searchFlights(any());
    }

    @Test
    void getInterconnectionsShouldReturnBadRequestWhenParamIsMissing() throws Exception {
        mockMvc.perform(get("/ryanair/interconnections")
                        // Missing "departure" param
                        .param("arrival", arrival)
                        .param("departureDateTime", departureDateTime)
                        .param("arrivalDateTime", arrivalDateTime))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getInterconnectionsShouldReturnBadRequestWhenDateTimeFormatIsInvalid() throws Exception {
        mockMvc.perform(get("/ryanair/interconnections")
                        .param("departure", departure)
                        .param("arrival", arrival)
                        .param("departureDateTime", "2023/10/20 08:00") // Invalid format
                        .param("arrivalDateTime", arrivalDateTime))
                .andExpect(status().isBadRequest());
    }

    private Flight createMockFlight() {
        FlightLeg leg = new FlightLeg();
        leg.setFlightFrom("DUB");
        leg.setFlightTo("WRO");
        leg.setFlightDepartureTime(LocalDateTime.parse("2023-10-20T10:00"));
        leg.setFlightArrivalTime(LocalDateTime.parse("2023-10-20T12:00"));

        return Flight.builder()
                .stops(0)
                .legs(List.of(leg))
                .build();
    }
}