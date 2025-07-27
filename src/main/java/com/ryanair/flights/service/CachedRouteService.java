package com.ryanair.flights.service;

import com.ryanair.flights.client.RyanairApiClient;
import com.ryanair.flights.model.external.Route;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CachedRouteService {

    private final RyanairApiClient ryanairApiClient;

    public CachedRouteService(RyanairApiClient ryanairApiClient) {
        this.ryanairApiClient = ryanairApiClient;
    }

    @Cacheable("routes")
    public List<Route> getAllRoutes() {
        return ryanairApiClient.getRoutes();
    }
}