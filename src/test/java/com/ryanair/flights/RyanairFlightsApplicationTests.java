package com.ryanair.flights;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class RyanairFlightsApplicationTests {

    @Test
    void contextLoads(ApplicationContext applicationContext) {
        assertNotNull(applicationContext, "Application context should not be null");
    }

}