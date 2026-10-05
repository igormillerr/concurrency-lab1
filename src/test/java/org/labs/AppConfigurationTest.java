package org.labs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppConfigurationTest {

    @Test
    void shouldCreateConfigurationWithValidValues() {
        AppConfiguration configuration =
                new AppConfiguration(7, 2, 30);

        assertEquals(7, configuration.programmersCount());
        assertEquals(2, configuration.waiterCount());
        assertEquals(30, configuration.totalPortionCount());
    }

    @Test
    void shouldAllowPortionCountEqualToProgrammersCount() {
        AppConfiguration configuration =
                new AppConfiguration(7, 2, 7);

        assertEquals(7, configuration.programmersCount());
        assertEquals(2, configuration.waiterCount());
        assertEquals(7, configuration.totalPortionCount());
    }

    @Test
    void shouldThrowExceptionWhenProgrammersCountIsZero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AppConfiguration(0, 2, 30)
        );
    }

    @Test
    void shouldThrowExceptionWhenProgrammersCountIsNegative() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AppConfiguration(-1, 2, 30)
        );
    }

    @Test
    void shouldThrowExceptionWhenWaiterCountIsZero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AppConfiguration(7, 0, 30)
        );
    }

    @Test
    void shouldThrowExceptionWhenWaiterCountIsNegative() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AppConfiguration(7, -1, 30)
        );
    }

    @Test
    void shouldThrowExceptionWhenTotalPortionCountIsLessThanProgrammersCount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AppConfiguration(7, 2, 6)
        );
    }

}