package com.ryanair.flights.model.internal;

import com.ryanair.flights.model.dto.FlightLeg;
import com.ryanair.flights.model.external.Day;
import com.ryanair.flights.model.external.Route;
import com.ryanair.flights.model.external.Schedule;
import com.ryanair.flights.util.DateTimeUtil;
import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class InternalSchedule {
    private HashMap<Integer, List<FlightLeg>> flightsByDay;

    public InternalSchedule(Schedule schedule, FlightSearchCriteria criteria, Route route, YearMonth yearMonth) {
        this.flightsByDay = new HashMap<>();
        for (Day day : schedule.getDays()) {
            List<FlightLeg> internalFlights = day.getFlights().stream()
                    .map(x -> new FlightLeg(day.getDay(), yearMonth.getMonthValue(), yearMonth.getYear(), x, route))
                    .filter(x -> DateTimeUtil.isWithinTimeWindow(criteria.getDepartureDateTime(), criteria.getArrivalDateTime(), x.getFlightDepartureTime(), x.getFlightArrivalTime()))
                    .toList();
            this.flightsByDay.put(day.getDay(),internalFlights);
        }
    }
}
