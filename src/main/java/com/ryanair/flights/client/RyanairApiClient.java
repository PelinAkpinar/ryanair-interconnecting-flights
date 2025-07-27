package com.ryanair.flights.client;

import com.ryanair.flights.model.external.Route;
import com.ryanair.flights.model.external.Schedule;

import java.util.List;

public interface RyanairApiClient {
    List<Route> getRoutes();

    Schedule getSchedule(String from, String to, int year, int month);
}