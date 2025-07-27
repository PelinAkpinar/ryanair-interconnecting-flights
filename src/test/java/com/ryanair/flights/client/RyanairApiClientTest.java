package com.ryanair.flights.client;

import com.ryanair.flights.client.impl.RyanairApiClientImpl;
import com.ryanair.flights.exception.ExternalApiException;
import com.ryanair.flights.model.external.Route;
import com.ryanair.flights.model.external.Schedule;
import com.ryanair.flights.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RyanairApiClientTest {

    private RyanairApiClient ryanairApiClient;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ryanairApiClient = new RyanairApiClientImpl(restTemplate);
    }

    @Test
    void getRoutesThrowsExternalApiExceptionWhenApiReturnsEmptyArray() {
        when(restTemplate.getForObject(Constants.ROUTES_API_URL, Route[].class)).thenReturn(new Route[0]);

        ExternalApiException exception = assertThrows(ExternalApiException.class, () -> ryanairApiClient.getRoutes());

        assertEquals("No routes found", exception.getMessage());
        verify(restTemplate, times(1)).getForObject(Constants.ROUTES_API_URL, Route[].class);
    }

    @Test
    void getRoutesThrowsExternalApiExceptionWhenApiCallFails() {
        when(restTemplate.getForObject(Constants.ROUTES_API_URL, Route[].class)).thenThrow(new RuntimeException("API error"));

        ExternalApiException exception = assertThrows(ExternalApiException.class, () -> ryanairApiClient.getRoutes());

        assertEquals("API error", exception.getMessage());
        verify(restTemplate, times(1)).getForObject(Constants.ROUTES_API_URL, Route[].class);
    }

    @Test
    void getScheduleThrowsExternalApiExceptionWhenApiCallFails() {
        String url = MessageFormat.format(Constants.SCHEDULES_API_URL,"DUB","WRO","2023","6");
        when(restTemplate.getForObject(url, Schedule.class)).thenThrow(new RuntimeException("API error"));

        ExternalApiException exception = assertThrows(ExternalApiException.class, () -> ryanairApiClient.getSchedule("DUB", "WRO", 2023, 6));

        assertEquals("API error", exception.getMessage());
        verify(restTemplate, times(1)).getForObject(url, Schedule.class);
    }

    @Test
    void getScheduleReturnsScheduleWhenApiCallSucceeds() {
        String url = MessageFormat.format(Constants.SCHEDULES_API_URL,"DUB","WRO","2023","6");
        Schedule mockSchedule = new Schedule();
        when(restTemplate.getForObject(url, Schedule.class)).thenReturn(mockSchedule);

        Schedule result = ryanairApiClient.getSchedule("DUB", "WRO", 2023, 6);

        assertNotNull(result);
        assertEquals(mockSchedule, result);
        verify(restTemplate, times(1)).getForObject(url, Schedule.class);
    }
}