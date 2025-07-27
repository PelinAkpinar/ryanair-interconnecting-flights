package com.ryanair.flights.util;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class DateTimeUtil {

    public static boolean isWithinTimeWindow(LocalDateTime flightDepartureCriteria, LocalDateTime flightArrivalCriteria, LocalDateTime start, LocalDateTime end) {
        return flightDepartureCriteria.isBefore(start) && flightArrivalCriteria.isAfter(end);
    }

    public static List<YearMonth> getMonthsBetween(LocalDateTime start, LocalDateTime end) {
        List<YearMonth> months = new ArrayList<>();
        YearMonth startMonth = YearMonth.from(start);
        YearMonth endMonth = YearMonth.from(end);

        while (!startMonth.isAfter(endMonth)) {
            months.add(startMonth);
            startMonth = startMonth.plusMonths(1);
        }
        return months;
    }

    private DateTimeUtil(){
        // Private constructor to prevent instantiation
    }
}