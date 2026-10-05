package org.labs;

public class Main {

    public static void main(String[] args) {
        AppConfiguration configuration = AppConfiguration.fromEnvironment();

        DiningSimulation simulation = new DiningSimulation(configuration);
        try {
            SimulationResult result = simulation.run();
            System.out.println(result.eatenPortionsByDeveloper());
            System.out.println(result.remainingPortions());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}