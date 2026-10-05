package org.labs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiningSimulationTest {

    @Test
    @Timeout(5)
    void shouldDistributeThirtyPortionsBetweenSevenDevelopers() throws InterruptedException {

        AppConfiguration configuration = new AppConfiguration(7, 2, 30);
        DiningSimulation simulation = new DiningSimulation(configuration);
        SimulationResult result = simulation.run();

        assertEquals(
                List.of(5, 5, 4, 4, 4, 4, 4),
                result.eatenPortionsByDeveloper()
        );

        assertSimulationInvariants(result, configuration);
    }

    @Test
    @Timeout(5)
    void shouldFinishWhenEachDeveloperHasOnlyInitialPortion() throws InterruptedException {

        AppConfiguration configuration = new AppConfiguration(7, 2, 7);
        DiningSimulation simulation = new DiningSimulation(configuration);
        SimulationResult result = simulation.run();

        assertEquals(
                List.of(1, 1, 1, 1, 1, 1, 1),
                result.eatenPortionsByDeveloper()
        );

        assertSimulationInvariants(result, configuration);
    }

    @Test
    @Timeout(5)
    void shouldDistributeRemainderToFirstDevelopers() throws InterruptedException {

        AppConfiguration configuration = new AppConfiguration(3, 1, 10);
        DiningSimulation simulation = new DiningSimulation(configuration);
        SimulationResult result = simulation.run();

        assertEquals(
                List.of(4, 3, 3),
                result.eatenPortionsByDeveloper()
        );

        assertSimulationInvariants(result, configuration);
    }

    @Test
    @Timeout(5)
    void shouldDistributePortionsEquallyWhenThereIsNoRemainder() throws InterruptedException {

        AppConfiguration configuration = new AppConfiguration(4, 2, 20);
        DiningSimulation simulation = new DiningSimulation(configuration);
        SimulationResult result = simulation.run();

        assertEquals(
                List.of(5, 5, 5, 5),
                result.eatenPortionsByDeveloper()
        );

        assertSimulationInvariants(result, configuration);
    }

    @Test
    @Timeout(10)
    void shouldRemainCorrectAcrossRepeatedRuns() throws InterruptedException {

        AppConfiguration configuration = new AppConfiguration(7, 2, 30);

        for (int i = 0; i < 50; i++) {
            DiningSimulation simulation = new DiningSimulation(configuration);
            SimulationResult result = simulation.run();
            assertSimulationInvariants(result, configuration);

            assertEquals(
                    List.of(5, 5, 4, 4, 4, 4, 4),
                    result.eatenPortionsByDeveloper()
            );
        }
    }

    private void assertSimulationInvariants(SimulationResult result, AppConfiguration configuration) {
        List<Integer> eatenPortions = result.eatenPortionsByDeveloper();

        assertEquals(
                configuration.programmersCount(),
                eatenPortions.size(),
                "Result must contain one entry for each developer"
        );

        int totalEaten = eatenPortions.stream()
                .mapToInt(Integer::intValue)
                .sum();

        assertEquals(
                configuration.totalPortionCount(),
                totalEaten,
                "All configured portions must be eaten"
        );

        assertEquals(
                0,
                result.remainingPortions(),
                "No portions should remain after simulation"
        );

        int minEaten = eatenPortions.stream()
                .mapToInt(Integer::intValue)
                .min()
                .orElseThrow();

        int maxEaten = eatenPortions.stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElseThrow();

        assertTrue(
                maxEaten - minEaten <= 1,
                "Difference between developers must not exceed one portion"
        );

        assertTrue(
                eatenPortions.stream().allMatch(portions -> portions > 0),
                "Every developer must eat at least one portion"
        );
    }
}