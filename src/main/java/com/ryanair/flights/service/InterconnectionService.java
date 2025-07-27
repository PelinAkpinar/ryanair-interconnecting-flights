package com.ryanair.flights.service;

import com.ryanair.flights.model.dto.Flight;
import com.ryanair.flights.model.dto.FlightLeg;
import com.ryanair.flights.model.external.Route;
import com.ryanair.flights.model.external.Schedule;
import com.ryanair.flights.model.internal.FlightSearchCriteria;
import com.ryanair.flights.model.internal.InternalSchedule;
import com.ryanair.flights.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class InterconnectionService {

    private static final Logger log = LoggerFactory.getLogger(InterconnectionService.class);

    @Autowired
    private RouteService routeService;

    @Autowired
    private ScheduleService scheduleService;

    private record RoutePair(Route firstLeg, Route secondLeg) {
    }

    /**
     * Asynchronously finds direct flights for the given criteria.
     * The method is non-blocking and returns a CompletableFuture that will be completed with the list of flights.
     *
     * @param criteria The search criteria for the flights.
     * @return A CompletableFuture containing a list of direct flights.
     */

    public CompletableFuture<List<Flight>> findDirectFlights(FlightSearchCriteria criteria) {
        List<Route> directRoutes = routeService.findDirectRoute(criteria.getDeparture(), criteria.getArrival());
        if (directRoutes.isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        List<YearMonth> monthsToScan = getMonthsBetween(criteria.getDepartureDateTime(), criteria.getArrivalDateTime());

        List<CompletableFuture<List<Flight>>> monthlyFutures = directRoutes.stream()
                .flatMap(route -> monthsToScan.stream().map(month ->
                        CompletableFuture.supplyAsync(() -> {
                            // This logic fetches and processes flights for a single route and month.
                            Schedule schedule = scheduleService.getSchedule(route.getAirportFrom(), route.getAirportTo(), month.getYear(), month.getMonthValue());
                            if (schedule == null || schedule.getDays() == null) {
                                return Collections.<Flight>emptyList();
                            }
                            InternalSchedule internalSchedule = new InternalSchedule(schedule, criteria, route, month);
                            return internalSchedule.getFlightsByDay().values().stream()
                                    .flatMap(List::stream)
                                    .map(this::createDirectFlight)
                                    .toList();
                        }).exceptionally(ex -> {
                            log.error("Failed to fetch direct schedule for route {}-{} and month {}",
                                    route.getAirportFrom(), route.getAirportTo(), month, ex);
                            return Collections.emptyList(); // Return empty list on failure to not break the whole process.
                        })
                ))
                .toList();

        return combineFutures(monthlyFutures);
    }

    /**
     * Finds interconnected flights (with one stop) by asynchronously searching for all valid leg pairs
     * and combining them.
     *
     * @param criteria The search criteria for the flights.
     * @return A list of interconnected flights.
     */
    public CompletableFuture<List<Flight>> findInterconnectedFlights(FlightSearchCriteria criteria) {
        List<RoutePair> candidateRoutePairs = findCandidateRoutePairs(criteria.getDeparture(), criteria.getArrival());

        if (candidateRoutePairs.isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        List<CompletableFuture<List<Flight>>> connectionFutures = candidateRoutePairs.stream()
                .map(pair -> processRoutePair(pair, criteria))
                .toList();

        return combineFutures(connectionFutures);
    }

    private List<RoutePair> findCandidateRoutePairs(String departure, String arrival) {
        List<Route> routesFromDeparture = routeService.findRoutesFrom(departure).stream()
                .filter(route -> !route.getAirportTo().equalsIgnoreCase(arrival))
                .toList();

        Map<String, List<Route>> routesToArrivalByDeparture = routeService.findRoutesTo(arrival).stream()
                .filter(route -> !route.getAirportFrom().equalsIgnoreCase(departure))
                .collect(Collectors.groupingBy(Route::getAirportFrom));

        return routesFromDeparture.stream()
                .flatMap(firstLeg -> routesToArrivalByDeparture
                        .getOrDefault(firstLeg.getAirportTo(), Collections.emptyList())
                        .stream()
                        .map(secondLeg -> new RoutePair(firstLeg, secondLeg)))
                .toList();
    }

    private CompletableFuture<List<Flight>> processRoutePair(RoutePair pair, FlightSearchCriteria originalCriteria) {
        FlightSearchCriteria firstLegCriteria = createFirstLegCriteria(pair.firstLeg(), originalCriteria);
        FlightSearchCriteria secondLegCriteria = createSecondLegCriteria(pair.secondLeg(), originalCriteria);

        CompletableFuture<List<Flight>> firstLegFlightsFuture = findDirectFlights(firstLegCriteria);
        CompletableFuture<List<Flight>> secondLegFlightsFuture = findDirectFlights(secondLegCriteria);

        return firstLegFlightsFuture
                .thenCombine(secondLegFlightsFuture, this::combineFlightLegs)
                .exceptionally(ex -> {
                    log.error("Failed to process interconnected flight for routes {}-{} and {}-{}",
                            pair.firstLeg().getAirportFrom(), pair.firstLeg().getAirportTo(),
                            pair.secondLeg().getAirportFrom(), pair.secondLeg().getAirportTo(), ex);
                    return Collections.emptyList();
                });
    }

    private List<Flight> combineFlightLegs(List<Flight> firstLegFlights, List<Flight> secondLegFlights) {
        if (firstLegFlights.isEmpty() || secondLegFlights.isEmpty()) {
            return Collections.emptyList();
        }

        List<Flight> interconnected = new ArrayList<>();
        for (Flight firstLegFlight : firstLegFlights) {
            for (Flight secondLegFlight : secondLegFlights) {
                FlightLeg firstLeg = firstLegFlight.getLegs().getFirst();
                FlightLeg secondLeg = secondLegFlight.getLegs().getFirst();

                if (validateConnection(firstLeg, secondLeg)) {
                    interconnected.add(buildFlightConnection(firstLeg, secondLeg));
                }
            }
        }
        return interconnected;
    }

    private FlightSearchCriteria createFirstLegCriteria(Route route, FlightSearchCriteria original) {
        return new FlightSearchCriteria(route.getAirportFrom(),
                route.getAirportTo(),
                original.getDepartureDateTime(),
                original.getArrivalDateTime().minusHours(Constants.MINIMUM_LAYOVER_HOURS));
    }

    private FlightSearchCriteria createSecondLegCriteria(Route route, FlightSearchCriteria original) {
        return new FlightSearchCriteria(route.getAirportFrom(),
                route.getAirportTo(),
                original.getDepartureDateTime().plusHours(Constants.MINIMUM_LAYOVER_HOURS),
                original.getArrivalDateTime());
    }

    private <T> CompletableFuture<List<T>> combineFutures(List<CompletableFuture<List<T>>> futures) {
        CompletableFuture<Void> allDoneFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        return allDoneFuture.thenApply(v ->
                futures.stream()
                        .map(CompletableFuture::join)
                        .flatMap(List::stream)
                        .toList()
        );
    }

    private List<YearMonth> getMonthsBetween(LocalDateTime start, LocalDateTime end) {
        List<YearMonth> months = new ArrayList<>();
        YearMonth startMonth = YearMonth.from(start);
        YearMonth endMonth = YearMonth.from(end);

        while (!startMonth.isAfter(endMonth)) {
            months.add(startMonth);
            startMonth = startMonth.plusMonths(1);
        }
        return months;
    }

    private Flight createDirectFlight(FlightLeg flightLeg) {
        Flight flight = new Flight();
        flight.setStops(0);
        flight.setLegs(List.of(flightLeg));
        return flight;
    }

    public boolean validateConnection(FlightLeg firstLeg, FlightLeg secondLeg) {
        // The arrival of the first leg must be at least 2 hours before the departure of the second leg
        LocalDateTime earliestDepartureForSecondLeg = firstLeg.getFlightArrivalTime().plusHours(Constants.MINIMUM_LAYOVER_HOURS);
        return !secondLeg.getFlightDepartureTime().isBefore(earliestDepartureForSecondLeg);
    }

    public Flight buildFlightConnection(FlightLeg firstLeg, FlightLeg secondLeg) {
        Flight flight = new Flight();
        flight.setStops(1);
        flight.setLegs(List.of(firstLeg, secondLeg));
        return flight;
    }
}