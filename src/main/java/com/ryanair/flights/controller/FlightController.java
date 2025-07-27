package com.ryanair.flights.controller;

import com.ryanair.flights.exception.FlightNotFoundException;
import com.ryanair.flights.exception.InvalidRequestException;
import com.ryanair.flights.model.dto.Flight;
import com.ryanair.flights.model.internal.FlightSearchCriteria;
import com.ryanair.flights.service.FlightService;
import com.ryanair.flights.util.FlightValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/ryanair")
public class FlightController {

    private final FlightService flightService;

    @Autowired
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    //http://localhost:8080/ryanair/interconnections?
    //departure=DUB&arrival=WRO&departureDateTime=2018-03-01T07:00&arrivalDateTime=2018-03-
    //03T21:00
    @GetMapping("/interconnections")
    public ResponseEntity<List<Flight>> getInterconnections(
            @RequestParam("departure") String departure,
            @RequestParam("arrival") String arrival,
            @RequestParam("arrivalDateTime") String arrivalDateTime,
            @RequestParam("departureDateTime") String departureDateTime) throws ExecutionException, InterruptedException {
        LocalDateTime departureDateTimeParsed;
        LocalDateTime arrivalDateTimeParsed;
        try {
            departureDateTimeParsed = LocalDateTime.parse(departureDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            arrivalDateTimeParsed = LocalDateTime.parse(arrivalDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        } catch (DateTimeParseException ex) {
            throw new InvalidRequestException(ex.getMessage());
        }

        FlightSearchCriteria flightSearchCriteria = new FlightSearchCriteria(departure, arrival, departureDateTimeParsed, arrivalDateTimeParsed);

        FlightValidator.validateSearchCriteria(flightSearchCriteria);

        List<Flight> response = flightService.searchFlights(flightSearchCriteria);

        if (response.isEmpty()) {
            throw new FlightNotFoundException("No flights found for the given criteria.");
        }
        return ResponseEntity.ok(response);
    }
}