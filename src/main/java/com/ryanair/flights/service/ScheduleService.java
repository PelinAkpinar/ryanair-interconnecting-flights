package com.ryanair.flights.service;

import com.ryanair.flights.client.RyanairApiClient;
import com.ryanair.flights.model.external.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {

    private final RyanairApiClient ryanairApiClient;

    @Autowired
    public ScheduleService(RyanairApiClient ryanairApiClient) {
        this.ryanairApiClient = ryanairApiClient;
    }

    public Schedule getSchedule(String departureAirport, String arrivalAirport, int year, int month) {
        return ryanairApiClient.getSchedule(departureAirport, arrivalAirport, year, month);
    }
}