package com.ryanair.flights.util;

import com.ryanair.flights.exception.InvalidRequestException;
import com.ryanair.flights.model.internal.FlightSearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FlightValidatorTest {

    private FlightValidator flightValidator;
    private final LocalDateTime start = LocalDateTime.of(2024, 8, 15, 10, 0);
    private final LocalDateTime end = LocalDateTime.of(2024, 8, 16, 10, 0);

    @BeforeEach
    void setUp() {
        flightValidator = new FlightValidator();
    }

    @Test
    void validateSearchCriteriaPassWhenCriteriaIsValid() {
        FlightSearchCriteria criteria = new FlightSearchCriteria(
                "DUB",
                "WRO",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );

        assertDoesNotThrow(() -> flightValidator.validateSearchCriteria(criteria));
    }

    @Test
    void validateSearchCriteriaThrowExceptionWhenDepartureIsInvalid() {
        FlightSearchCriteria criteria = new FlightSearchCriteria(
                "DUBLIN",
                "WRO",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> flightValidator.validateSearchCriteria(criteria));
        assertEquals("Invalid airport code: DUBLIN", exception.getMessage());
    }

    @Test
    void validateSearchCriteriaThrowExceptionWhenDateRangeIsInvalid() {
        FlightSearchCriteria criteria = new FlightSearchCriteria(
                "DUB",
                "WRO",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now()
        );

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> flightValidator.validateSearchCriteria(criteria));
        assertEquals("Departure time must be before arrival time.", exception.getMessage());
    }


    @Test
    void validateAirportCodePassForValidCode() {
        assertDoesNotThrow(() -> flightValidator.validateAirportCode("DUB"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void validateAirportCodeThrowExceptionForNullOrEmptyCode(String invalidCode) {
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> flightValidator.validateAirportCode(invalidCode));
        assertEquals("Airport code cannot be null or empty.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"AB", "ABCD", "123"})
    void validateAirportCodeThrowExceptionForInvalidLength(String invalidCode) {
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> flightValidator.validateAirportCode(invalidCode));
        assertEquals("Invalid airport code: " + invalidCode, exception.getMessage());
    }


    @Test
    void validateDateTimeRangePassWhenStartIsBeforeEnd() {
        assertDoesNotThrow(() -> flightValidator.validateDateTimeRange(start, end));
    }

    @Test
    void validateDateTimeRangePassWhenStartIsEqualToEnd() {
        assertDoesNotThrow(() -> flightValidator.validateDateTimeRange(start, start));
    }

    @Test
    void validateDateTimeRangeThrowExceptionWhenStartIsNull() {
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> flightValidator.validateDateTimeRange(null, end));
        assertEquals("Departure and arrival times cannot be null.", exception.getMessage());
    }

    @Test
    void validateDateTimeRangeThrowExceptionWhenEndIsNull() {
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> flightValidator.validateDateTimeRange(start, null));
        assertEquals("Departure and arrival times cannot be null.", exception.getMessage());
    }

    @Test
    void validateDateTimeRangeThrowExceptionWhenStartIsAfterEnd() {
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> flightValidator.validateDateTimeRange(end, start));
        assertEquals("Departure time must be before arrival time.", exception.getMessage());
    }

}