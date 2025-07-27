package com.ryanair.flights.model.external;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Schedule {
    private int month;
    private List<Day> days;

}