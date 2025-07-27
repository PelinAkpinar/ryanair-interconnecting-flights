package com.ryanair.flights.util;

import com.ryanair.flights.exception.InvalidRequestException;
import com.ryanair.flights.model.internal.FlightSearchCriteria;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;

@Component
public class FlightValidator {

    public void validateSearchCriteria(FlightSearchCriteria criteria) {
        validateAirportCode(criteria.getDeparture());
        validateAirportCode(criteria.getArrival());
        validateDateTimeRange(criteria.getDepartureDateTime(), criteria.getArrivalDateTime());
        if (criteria.getDeparture().equalsIgnoreCase(criteria.getArrival())) {
            throw new InvalidRequestException("Departure and arrival airports cannot be the same.");
        }
    }

    public void validateAirportCode(String code) {
        if (code == null || code.isEmpty()) {
            throw new InvalidRequestException("Airport code cannot be null or empty.");
        }
        if (code.length() != 3 || !code.matches("[A-Z]{3}")) {
            throw new InvalidRequestException("Invalid airport code: " + code);
        }
    }

    public void validateDateTimeRange(LocalDateTime start,LocalDateTime end) {
        if (start == null || end == null) {
            throw new InvalidRequestException("Departure and arrival times cannot be null.");
        }
        if (start.isAfter(end)) {
            throw new InvalidRequestException("Departure time must be before arrival time.");
        }
    }
}