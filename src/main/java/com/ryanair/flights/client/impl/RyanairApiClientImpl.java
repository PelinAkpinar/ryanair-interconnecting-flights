package com.ryanair.flights.client.impl;

import com.ryanair.flights.client.RyanairApiClient;
import com.ryanair.flights.exception.ExternalApiException;
import com.ryanair.flights.model.external.Route;
import com.ryanair.flights.model.external.Schedule;
import com.ryanair.flights.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.List;

@Service
public class RyanairApiClientImpl implements RyanairApiClient {

    private final RestTemplate restTemplate;

    @Autowired
    public RyanairApiClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Route> getRoutes() {
        try {
            Route[] routes = restTemplate.getForObject(Constants.ROUTES_API_URL, Route[].class);
            if (routes != null && routes.length == 0) {
                throw new ExternalApiException("No routes found");
            }
            return List.of(routes);
        } catch (Exception e) {
            throw new ExternalApiException(e.getMessage());
        }
    }

    @Override
    public Schedule getSchedule(String from, String to, int year, int month) {
        String url = MessageFormat.format(Constants.SCHEDULES_API_URL, from, to, String.valueOf(year), String.valueOf(month));
        try {
            return restTemplate.getForObject(url, Schedule.class);
        } catch (Exception e) {
            throw new ExternalApiException(e.getMessage());
        }
    }
}