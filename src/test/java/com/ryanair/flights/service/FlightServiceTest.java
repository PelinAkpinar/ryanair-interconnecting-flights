package com.ryanair.flights.service;

import com.ryanair.flights.model.dto.Flight;
import com.ryanair.flights.model.internal.FlightSearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class FlightServiceTest {

    @InjectMocks
    private FlightService flightService;

    @Mock
    private InterconnectionService interconnectionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void searchFlightsReturnsCombinedResultsFromDirectAndInterconnectedFlights() throws ExecutionException, InterruptedException {
        FlightSearchCriteria criteria = new FlightSearchCriteria();
        criteria.setDeparture("DUB");
        criteria.setArrival("WRO");

        List<Flight> directFlights = new ArrayList<>();
        directFlights.add(new Flight(0, new ArrayList<>()));

        List<Flight> interconnectedFlights = new ArrayList<>();
        interconnectedFlights.add(new Flight(1, new ArrayList<>()));

        when(interconnectionService.findDirectFlights(criteria)).thenReturn(CompletableFuture.completedFuture(directFlights));
        when(interconnectionService.findInterconnectedFlights(criteria)).thenReturn(CompletableFuture.completedFuture(interconnectedFlights));

        List<Flight> result = flightService.searchFlights(criteria);

        assertEquals(2, result.size());
        verify(interconnectionService).findDirectFlights(criteria);
        verify(interconnectionService).findInterconnectedFlights(criteria);
    }

    @Test
    void searchFlightsReturnsEmptyListWhenNoFlightsFound() throws ExecutionException, InterruptedException {
        FlightSearchCriteria criteria = new FlightSearchCriteria();
        criteria.setDeparture("DUB");
        criteria.setArrival("WRO");

        when(interconnectionService.findDirectFlights(criteria)).thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));
        when(interconnectionService.findInterconnectedFlights(criteria)).thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));

        List<Flight> result = flightService.searchFlights(criteria);

        assertTrue(result.isEmpty());
        verify(interconnectionService).findDirectFlights(criteria);
        verify(interconnectionService).findInterconnectedFlights(criteria);
    }

    @Test
    void combineResultsReturnsAllFlightsFromBothLists() {
        List<Flight> directFlights = new ArrayList<>();
        directFlights.add(new Flight(0, new ArrayList<>()));

        List<Flight> interconnectedFlights = new ArrayList<>();
        interconnectedFlights.add(new Flight(1, new ArrayList<>()));

        List<Flight> result = flightService.combineResults(directFlights, interconnectedFlights);

        assertEquals(2, result.size());
        assertEquals(0, result.get(0).getStops());
        assertEquals(1, result.get(1).getStops());
    }

    @Test
    void combineResultsReturnsDirectFlightsWhenInterconnectedFlightsAreEmpty() {
        List<Flight> directFlights = new ArrayList<>();
        directFlights.add(new Flight(0, new ArrayList<>()));

        List<Flight> interconnectedFlights = new ArrayList<>();

        List<Flight> result = flightService.combineResults(directFlights, interconnectedFlights);

        assertEquals(1, result.size());
        assertEquals(0, result.getFirst().getStops());
    }

    @Test
    void combineResultsReturnsInterconnectedFlightsWhenDirectFlightsAreEmpty() {
        List<Flight> directFlights = new ArrayList<>();

        List<Flight> interconnectedFlights = new ArrayList<>();
        interconnectedFlights.add(new Flight(1, new ArrayList<>()));

        List<Flight> result = flightService.combineResults(directFlights, interconnectedFlights);

        assertEquals(1, result.size());
        assertEquals(1, result.getFirst().getStops());
    }
}