package com.ryanair.flights.model.external;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FlightSchedule {
    private String number;
    private String departureTime;
    private String arrivalTime;

}