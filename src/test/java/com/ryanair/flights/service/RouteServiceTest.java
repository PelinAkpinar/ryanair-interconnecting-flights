package com.ryanair.flights.service;

import com.ryanair.flights.client.RyanairApiClient;
import com.ryanair.flights.model.external.Route;
import com.ryanair.flights.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class RouteServiceTest {

    @InjectMocks
    private RouteService routeService;

    @Mock
    private CachedRouteService cachedRouteService;

    @Mock
    private RyanairApiClient ryanairApiClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findDirectRouteReturnsEmptyListWhenNoMatchingRoutes() {
        Route route = new Route();
        route.setAirportFrom("DUB");
        route.setAirportTo("STN");

        when(ryanairApiClient.getRoutes()).thenReturn(Arrays.asList(route));

        List<Route> result = routeService.findDirectRoute("DUB", "WRO");

        assertTrue(result.isEmpty());
    }

    @Test
    void findRoutesFromReturnsEmptyListWhenNoRoutesFromAirport() {
        Route route = new Route();
        route.setAirportFrom("STN");
        route.setAirportTo("WRO");

        when(ryanairApiClient.getRoutes()).thenReturn(Arrays.asList(route));

        List<Route> result = routeService.findRoutesFrom("DUB");

        assertTrue(result.isEmpty());
    }

    @Test
    void findRoutesToReturnsEmptyListWhenNoRoutesToAirport() {
        Route route = new Route();
        route.setAirportFrom("DUB");
        route.setAirportTo("STN");

        when(ryanairApiClient.getRoutes()).thenReturn(Arrays.asList(route));

        List<Route> result = routeService.findRoutesTo("WRO");

        assertTrue(result.isEmpty());
    }

    @Test
    void isValidRouteReturnsFalseWhenRouteIsNull() {
        boolean result = routeService.isValidRoute(null);

        assertFalse(result);
    }

    @Test
    void isValidRouteReturnsFalseWhenOperatorIsNotRyanair() {
        Route route = new Route();
        route.setOperator("OtherOperator");

        boolean result = routeService.isValidRoute(route);

        assertFalse(result);
    }

    @Test
    void isValidRouteReturnsFalseWhenConnectingAirportIsNotNull() {
        Route route = new Route();
        route.setOperator(Constants.RYANAIR_OPERATOR);
        route.setConnectingAirport("ABC");

        boolean result = routeService.isValidRoute(route);

        assertFalse(result);
    }

    @Test
    void isValidRouteReturnsTrueForValidRoute() {
        Route route = new Route();
        route.setOperator(Constants.RYANAIR_OPERATOR);
        route.setConnectingAirport(null);

        boolean result = routeService.isValidRoute(route);

        assertTrue(result);
    }
}