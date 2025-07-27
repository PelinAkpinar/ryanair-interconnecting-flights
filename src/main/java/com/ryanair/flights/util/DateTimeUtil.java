package com.ryanair.flights.util;

import java.time.LocalDateTime;

public class DateTimeUtil {

    public static boolean isWithinTimeWindow(LocalDateTime flightDepartureCriteria, LocalDateTime flightArrivalCriteria, LocalDateTime start, LocalDateTime end) {
        return flightDepartureCriteria.isBefore(start) && flightArrivalCriteria.isAfter(end);
    }
}