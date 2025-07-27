package com.ryanair.flights.model.external;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Day {
    private int day;
    private List<FlightSchedule> flights;

}