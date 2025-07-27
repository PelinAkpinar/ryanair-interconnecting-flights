package com.ryanair.flights.service;

import com.ryanair.flights.model.dto.Flight;
import com.ryanair.flights.model.internal.FlightSearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InterconnectionServiceTest {

    @InjectMocks
    private InterconnectionService interconnectionService;

    @Mock
    private RouteService routeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findDirectFlightsReturnsEmptyListWhenNoRoutesFound() throws ExecutionException, InterruptedException {
        FlightSearchCriteria criteria = new FlightSearchCriteria();
        criteria.setDeparture("DUB");
        criteria.setArrival("WRO");
        criteria.setDepartureDateTime(LocalDateTime.now());
        criteria.setArrivalDateTime(LocalDateTime.now().plusHours(2));

        when(routeService.findDirectRoute("DUB", "WRO")).thenReturn(new ArrayList<>());

        List<Flight> result = interconnectionService.findDirectFlights(criteria).get();

        assertEquals(0, result.size());
        verify(routeService).findDirectRoute("DUB", "WRO");
    }

    @Test
    void findInterconnectedFlightsReturnsEmptyListWhenNoCandidateRoutes() throws ExecutionException, InterruptedException {
        FlightSearchCriteria criteria = new FlightSearchCriteria();
        criteria.setDeparture("DUB");
        criteria.setArrival("WRO");
        criteria.setDepartureDateTime(LocalDateTime.now());
        criteria.setArrivalDateTime(LocalDateTime.now().plusHours(5));

        when(routeService.findRoutesFrom("DUB")).thenReturn(new ArrayList<>());
        when(routeService.findRoutesTo("WRO")).thenReturn(new ArrayList<>());

        List<Flight> result = interconnectionService.findInterconnectedFlights(criteria).get();

        assertEquals(0, result.size());
        verify(routeService).findRoutesFrom("DUB");
        verify(routeService).findRoutesTo("WRO");
    }
}