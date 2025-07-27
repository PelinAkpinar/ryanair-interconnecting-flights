package com.ryanair.flights.service;

import com.ryanair.flights.model.external.Route;
import com.ryanair.flights.util.Constants;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteService {

    private final CachedRouteService cachedRouteService;

    public RouteService(CachedRouteService cachedRouteService) {
        this.cachedRouteService = cachedRouteService;
    }

    public List<Route> findDirectRoute(String from, String to) {
        return cachedRouteService.getAllRoutes().stream()
                .filter(this::isValidRoute)
                .filter(route -> route.getAirportFrom().equalsIgnoreCase(from) &&
                        route.getAirportTo().equalsIgnoreCase(to))
                .toList();
    }

    public List<Route> findRoutesFrom(String airport) {
        return cachedRouteService.getAllRoutes().stream()
                .filter(this::isValidRoute)
                .filter(route -> route.getAirportFrom().equalsIgnoreCase(airport))
                .toList();
    }

    public List<Route> findRoutesTo(String airport) {
        return cachedRouteService.getAllRoutes().stream()
                .filter(this::isValidRoute)
                .filter(route -> route.getAirportTo().equalsIgnoreCase(airport))
                .toList();
    }

    public boolean isValidRoute(Route route) {
        return route != null && route.getOperator() != null && route.getOperator().equalsIgnoreCase(Constants.RYANAIR_OPERATOR) && route.getConnectingAirport() == null;
    }
}