package com.ryanair.flights.model.dto;

import com.ryanair.flights.model.external.FlightSchedule;
import com.ryanair.flights.model.external.Route;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class FlightLeg {
    private LocalDateTime flightDepartureTime;
    private LocalDateTime flightArrivalTime;
    private String flightNumber;
    private String flightFrom;
    private String flightTo;

    public FlightLeg() {
    }

    public FlightLeg(int day, int month, int year, FlightSchedule flightSchedule, Route route) {
        this.flightNumber = flightSchedule.getNumber();
        LocalDate date = LocalDate.of(year, month, day);
        this.flightDepartureTime = LocalDateTime.of(date, LocalTime.parse(flightSchedule.getDepartureTime()));
        this.flightArrivalTime = LocalDateTime.of(date, LocalTime.parse(flightSchedule.getArrivalTime()));

        if (this.flightArrivalTime.isBefore(this.flightDepartureTime)) {
            this.flightArrivalTime = this.flightArrivalTime.plusDays(1);
        }
        this.flightFrom = route.getAirportFrom();
        this.flightTo = route.getAirportTo();
    }

}
