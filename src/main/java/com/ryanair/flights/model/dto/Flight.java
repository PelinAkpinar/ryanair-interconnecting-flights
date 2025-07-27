package com.ryanair.flights.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class Flight {
    private int stops;
    private List<FlightLeg> legs = new ArrayList<>();

    public Flight(int stops, List<FlightLeg> legs) {
        this.stops = stops;
        this.legs = legs;
    }

    public Flight() {

    }
}