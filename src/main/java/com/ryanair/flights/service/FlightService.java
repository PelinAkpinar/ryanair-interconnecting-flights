package com.ryanair.flights.service;

import com.ryanair.flights.model.dto.Flight;
import com.ryanair.flights.model.internal.FlightSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class FlightService {

    private final InterconnectionService interconnectionService;

    @Autowired
    public FlightService(InterconnectionService interconnectionService) {
        this.interconnectionService = interconnectionService;
    }

    public List<Flight> searchFlights(FlightSearchCriteria criteria) throws ExecutionException, InterruptedException {
        List<Flight> directFlights = interconnectionService.findDirectFlights(criteria).get();
        List<Flight> interconnectedFlights = interconnectionService.findInterconnectedFlights(criteria).get();
        return combineResults(directFlights, interconnectedFlights);

    }

    public List<Flight> combineResults(List<Flight> direct, List<Flight> interconnected) {
        List<Flight> combined = new ArrayList<>();
        combined.addAll(direct);
        combined.addAll(interconnected);
        return combined;
    }
}
