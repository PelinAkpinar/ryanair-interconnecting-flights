package com.ryanair.flights.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateTimeUtilTest {

    private final LocalDateTime criteriaDeparture = LocalDateTime.of(2024, 8, 15, 10, 0); // 10:00 AM
    private final LocalDateTime criteriaArrival = LocalDateTime.of(2024, 8, 15, 22, 0);   // 10:00 PM

    @Test
    void isWithinTimeWindowReturnTrueWhenFlightIsStrictlyWithinCriteria() {
        LocalDateTime flightStart = criteriaDeparture.plusHours(1);   // 11:00 AM
        LocalDateTime flightEnd = criteriaArrival.minusHours(1);    // 09:00 PM

        assertTrue(DateTimeUtil.isWithinTimeWindow(criteriaDeparture, criteriaArrival, flightStart, flightEnd));
    }

    @Test
    void isWithinTimeWindowReturnFalseWhenFlightStartsExactlyOnCriteriaDeparture() {
        LocalDateTime flightStart = criteriaDeparture;              // 10:00 AM
        LocalDateTime flightEnd = criteriaArrival.minusHours(1);    // 09:00 PM

        assertFalse(DateTimeUtil.isWithinTimeWindow(criteriaDeparture, criteriaArrival, flightStart, flightEnd));
    }

    @Test
    void isWithinTimeWindowReturnFalseWhenFlightEndsExactlyOnCriteriaArrival() {
        LocalDateTime flightStart = criteriaDeparture.plusHours(1);   // 11:00 AM
        LocalDateTime flightEnd = criteriaArrival;                  // 10:00 PM

        assertFalse(DateTimeUtil.isWithinTimeWindow(criteriaDeparture, criteriaArrival, flightStart, flightEnd));
    }

    @Test
    void isWithinTimeWindowReturnFalseWhenFlightStartsBeforeCriteria() {
        LocalDateTime flightStart = criteriaDeparture.minusHours(1); // 09:00 AM
        LocalDateTime flightEnd = criteriaArrival.minusHours(1);   // 09:00 PM

        assertFalse(DateTimeUtil.isWithinTimeWindow(criteriaDeparture, criteriaArrival, flightStart, flightEnd));
    }

    @Test
    void isWithinTimeWindowReturnFalseWhenFlightEndsAfterCriteria() {
        LocalDateTime flightStart = criteriaDeparture.plusHours(1);  // 11:00 AM
        LocalDateTime flightEnd = criteriaArrival.plusHours(1);    // 11:00 PM

        assertFalse(DateTimeUtil.isWithinTimeWindow(criteriaDeparture, criteriaArrival, flightStart, flightEnd));
    }

    @Test
    void isWithinTimeWindowReturnFalseWhenFlightIsCompletelyBeforeCriteria() {
        LocalDateTime flightStart = criteriaDeparture.minusHours(2); // 08:00 AM
        LocalDateTime flightEnd = criteriaDeparture.minusHours(1);   // 09:00 AM

        assertFalse(DateTimeUtil.isWithinTimeWindow(criteriaDeparture, criteriaArrival, flightStart, flightEnd));
    }

    @Test
    void isWithinTimeWindowReturnFalseWhenFlightIsCompletelyAfterCriteria() {
        LocalDateTime flightStart = criteriaArrival.plusHours(1);    // 11:00 PM
        LocalDateTime flightEnd = criteriaArrival.plusHours(2);      // 12:00 AM (next day)

        assertFalse(DateTimeUtil.isWithinTimeWindow(criteriaDeparture, criteriaArrival, flightStart, flightEnd));
    }

    @Test
    void isWithinTimeWindowReturnFalseWhenFlightEngulfsCriteria() {
        LocalDateTime flightStart = criteriaDeparture.minusHours(1); // 09:00 AM
        LocalDateTime flightEnd = criteriaArrival.plusHours(1);      // 11:00 PM

        assertFalse(DateTimeUtil.isWithinTimeWindow(criteriaDeparture, criteriaArrival, flightStart, flightEnd));
    }
}