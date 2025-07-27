package com.ryanair.flights.model.external;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Route {
    private String airportFrom;
    private String airportTo;
    private String connectingAirport;
    private String operator;
    private boolean newRoute;
    private boolean seasonalRoute;
    private String group;

}