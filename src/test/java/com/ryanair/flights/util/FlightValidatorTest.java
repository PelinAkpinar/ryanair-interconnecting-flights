package com.ryanair.flights.util;

import com.ryanair.flights.exception.InvalidRequestException;
import com.ryanair.flights.model.dto.FlightLeg;
import com.ryanair.flights.model.internal.FlightSearchCriteria;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class FlightValidatorTest {

    // Mocking the constant for test purposes
    private static final int MINIMUM_LAYOVER_HOURS = 2;

    @Test
    void validateSearchCriteriaWhenValidShouldNotThrowException() {
        LocalDateTime departureTime = LocalDateTime.of(2023, 10, 27, 10, 0);
        LocalDateTime arrivalTime = LocalDateTime.of(2023, 10, 27, 15, 0);
        FlightSearchCriteria criteria = new FlightSearchCriteria("DUB", "WRO", departureTime, arrivalTime);

        assertDoesNotThrow(() -> FlightValidator.validateSearchCriteria(criteria));
    }

    @Test
    void validateSearchCriteriaWhenAirportsAreSameShouldThrowException() {
        LocalDateTime departureTime = LocalDateTime.of(2023, 10, 27, 10, 0);
        LocalDateTime arrivalTime = LocalDateTime.of(2023, 10, 27, 15, 0);
        FlightSearchCriteria criteria = new FlightSearchCriteria("DUB", "DUB", departureTime, arrivalTime);

        assertThatThrownBy(() -> FlightValidator.validateSearchCriteria(criteria))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Departure and arrival airports cannot be the same.");
    }


    @Test
    void validateAirportCodeWhenValidShouldNotThrow() {
        assertDoesNotThrow(() -> FlightValidator.validateAirportCode("DUB"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void validateAirportCodeWhenNullOrEmptyShouldThrowException(String code) {
        assertThatThrownBy(() -> FlightValidator.validateAirportCode(code))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Airport code cannot be null or empty.");
    }

    @ParameterizedTest
    @CsvSource({
            "DU, Invalid airport code: DU",
            "DUBL, Invalid airport code: DUBL",
            "dub, Invalid airport code: dub",
            "D1B, Invalid airport code: D1B"
    })
    void validateAirportCodeWhenMalformedShouldThrowException(String code, String expectedMessage) {
        assertThatThrownBy(() -> FlightValidator.validateAirportCode(code))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage(expectedMessage);
    }


    @Test
    void validateDateTimeRangeWhenStartIsBeforeEndShouldNotThrow() {
        LocalDateTime start = LocalDateTime.of(2023, 10, 27, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 27, 12, 0);
        assertDoesNotThrow(() -> FlightValidator.validateDateTimeRange(start, end));
    }

    @Test
    void validateDateTimeRangeWhenStartIsEqualToEndShouldNotThrow() {
        LocalDateTime start = LocalDateTime.of(2023, 10, 27, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 27, 10, 0);
        assertDoesNotThrow(() -> FlightValidator.validateDateTimeRange(start, end));
    }

    @Test
    void validateDateTimeRangeWhenStartIsAfterEndShouldThrowException() {
        LocalDateTime start = LocalDateTime.of(2023, 10, 27, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 27, 10, 0);
        assertThatThrownBy(() -> FlightValidator.validateDateTimeRange(start, end))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Departure time must be before arrival time.");
    }

    @Test
    void validateDateTimeRangeWhenAnyDateIsNullShouldThrowException() {
        LocalDateTime time = LocalDateTime.now();
        assertThatThrownBy(() -> FlightValidator.validateDateTimeRange(null, time))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Departure and arrival times cannot be null.");

        assertThatThrownBy(() -> FlightValidator.validateDateTimeRange(time, null))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Departure and arrival times cannot be null.");
    }

    private FlightLeg createFlightLeg(LocalDateTime departure, LocalDateTime arrival) {
        FlightLeg leg = new FlightLeg();
        leg.setFlightDepartureTime(departure);
        leg.setFlightArrivalTime(arrival);
        return leg;
    }

    @Test
    void validateConnectionWhenLayoverIsExactlyMinimumShouldReturnTrue() {
        LocalDateTime firstLegArrival = LocalDateTime.of(2023, 10, 27, 10, 0);
        LocalDateTime secondLegDeparture = firstLegArrival.plusHours(MINIMUM_LAYOVER_HOURS);

        FlightLeg firstLeg = createFlightLeg(firstLegArrival.minusHours(2), firstLegArrival);
        FlightLeg secondLeg = createFlightLeg(secondLegDeparture, secondLegDeparture.plusHours(2));

        boolean isValid = FlightValidator.validateConnection(firstLeg, secondLeg);
        assertThat(isValid).isTrue();
    }

    @Test
    void validateConnectionWhenLayoverIsSufficientShouldReturnTrue() {
        LocalDateTime firstLegArrival = LocalDateTime.of(2023, 10, 27, 10, 0);
        LocalDateTime secondLegDeparture = firstLegArrival.plusHours(MINIMUM_LAYOVER_HOURS).plusMinutes(1);

        FlightLeg firstLeg = createFlightLeg(firstLegArrival.minusHours(2), firstLegArrival);
        FlightLeg secondLeg = createFlightLeg(secondLegDeparture, secondLegDeparture.plusHours(2));

        boolean isValid = FlightValidator.validateConnection(firstLeg, secondLeg);
        assertThat(isValid).isTrue();
    }

    @Test
    void validateConnectionWhenLayoverIsInsufficientShouldReturnFalse() {
        LocalDateTime firstLegArrival = LocalDateTime.of(2023, 10, 27, 10, 0);
        LocalDateTime secondLegDeparture = firstLegArrival.plusHours(MINIMUM_LAYOVER_HOURS).minusMinutes(1);

        FlightLeg firstLeg = createFlightLeg(firstLegArrival.minusHours(2), firstLegArrival);
        FlightLeg secondLeg = createFlightLeg(secondLegDeparture, secondLegDeparture.plusHours(2));

        boolean isValid = FlightValidator.validateConnection(firstLeg, secondLeg);
        assertThat(isValid).isFalse();
    }
}