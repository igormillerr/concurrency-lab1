package org.labs;

public record AppConfiguration(int programmersCount, int waiterCount, int totalPortionCount) {

    private static final String PROGRAMMERS_COUNTER_ENV = "PROGRAMMERS_COUNT";

    private static final String WAITER_COUNTER_ENV = "WAITER_COUNT";

    private static final String PORTION_COUNTER_ENV = "PORTION_COUNT";

    private static final int PROGRAMMERS_COUNT = 7;

    private static final int WAITER_COUNT = 2;

    private static final int PORTION_COUNT = 1_000_000;

    public AppConfiguration {
        validate(programmersCount, waiterCount, totalPortionCount);
    }

    private static void validate(int programmersCount, int waiterCount, int totalPortionCount) {
        if (programmersCount <= 0) {
            throw new IllegalArgumentException("Programmers count must be greater than 0");
        }

        if (waiterCount <= 0) {
            throw new IllegalArgumentException("Waiter count must be greater than 0");
        }

        if (totalPortionCount < programmersCount) {
            throw new IllegalArgumentException("Total portion count must be at least equal to programmers count");
        }
    }

    public static AppConfiguration fromEnvironment() {
        int programmersCount = getEnv(PROGRAMMERS_COUNT, PROGRAMMERS_COUNTER_ENV);
        int waiterCount = getEnv(WAITER_COUNT, WAITER_COUNTER_ENV);
        int totalPortionCount = getEnv(PORTION_COUNT, PORTION_COUNTER_ENV);

        return new AppConfiguration(programmersCount, waiterCount, totalPortionCount);
    }

    private static int getEnv(int defaultValue, String envName) {
        String env = System.getenv(envName);
        if (env != null) {
            try {
                return Integer.parseInt(env);
            } catch (NumberFormatException e) {
                System.out.printf("Parsing error of " + envName + "\n");
            }
        }
        return defaultValue;
    }

}
