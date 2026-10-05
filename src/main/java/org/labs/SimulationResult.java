package org.labs;

import java.util.List;

public record SimulationResult(
        List<Integer> eatenPortionsByDeveloper,
        int remainingPortions
) {

    public SimulationResult {
        eatenPortionsByDeveloper = List.copyOf(eatenPortionsByDeveloper);
    }

}
