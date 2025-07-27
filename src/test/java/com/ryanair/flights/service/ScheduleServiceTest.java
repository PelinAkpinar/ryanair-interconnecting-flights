package com.ryanair.flights.service;

import com.ryanair.flights.client.RyanairApiClient;
import com.ryanair.flights.model.external.Schedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ScheduleServiceTest {

    @InjectMocks
    private ScheduleService scheduleService;

    @Mock
    private RyanairApiClient ryanairApiClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getScheduleReturnsScheduleForGivenParameters() {
        String from = "DUB";
        String to = "WRO";
        LocalDateTime date = LocalDateTime.of(2023, 10, 1, 0, 0);
        Schedule schedule = new Schedule();

        when(ryanairApiClient.getSchedule(from, to, 2023, 10)).thenReturn(schedule);

        Schedule result = scheduleService.getSchedule(from, to, date.getYear(), date.getMonthValue());

        assertEquals(schedule, result);
    }

}