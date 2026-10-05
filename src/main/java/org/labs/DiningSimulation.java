package org.labs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.currentThread;
import static org.labs.PortionState.HAS_PORTION;
import static org.labs.PortionState.NO_MORE_PORTIONS;
import static org.labs.PortionState.WAITING;

public class DiningSimulation {

    private final int programmersCount;
    private final int waitersCount;

    private final int basePortionCount;
    private final int extraPortionCount;

    private final int[] eatenPortionsByDeveloper;
    private final List<Spoon> spoons;
    private final List<DeveloperState> developerStates;
    private final Queue<Integer> orders;

    private final ReentrantLock lock;
    private final Condition hasOrder;

    private int remainingPortions;
    private boolean lunchFinished;
    private int finishedDevelopers;

    public DiningSimulation(AppConfiguration configuration) {
        this.programmersCount = configuration.programmersCount();
        this.waitersCount = configuration.waiterCount();

        int totalPortionCount = configuration.totalPortionCount();
        this.basePortionCount = totalPortionCount / programmersCount;
        this.extraPortionCount = totalPortionCount % programmersCount;
        this.remainingPortions = totalPortionCount - programmersCount;

        this.eatenPortionsByDeveloper = new int[programmersCount];

        this.spoons = new ArrayList<>();
        this.developerStates = new ArrayList<>();
        this.orders = new ArrayDeque<>();

        this.lock = new ReentrantLock();
        this.hasOrder = lock.newCondition();

        this.lunchFinished = false;
        this.finishedDevelopers = 0;
    }

    public SimulationResult run() throws InterruptedException {
        for (int i = 0; i < programmersCount; i++) {
            spoons.add(new Spoon(i, new ReentrantLock()));
            developerStates.add(new DeveloperState(i, HAS_PORTION, lock.newCondition()));
        }

        System.out.println("App is started");
        Thread[] developers = createDevelopers(programmersCount);
        Thread[] waiters = createWaiters(waitersCount);

        waitForThreads(developers);
        waitForThreads(waiters);

        return new SimulationResult(getEatenPortionsResult(), remainingPortions);
    }

    private Thread[] createDevelopers(int count) {
        Thread[] developers = new Thread[count];
        for (int i = 0; i < count; i++) {
            final int id = i;
            int[] spoonIds = calculateSpoonId(id);

            developers[i] = new Thread(() -> {
                DefaultDeveloper developer = new DefaultDeveloper(calculateTargetPortionCount(id));
                int countEatenPortions = 0;

                try {
                    boolean wellFed = false;
                    while (!wellFed) {
                        if (!waitForPortionIfNeeded(id)) {
                            break;
                        }

                        if (developer.hasRightSpoon() && developer.hasLeftSpoon()) {
                            lock.lock();
                            try {
                                countEatenPortions++;
                                System.out.println("developer-" + id + " ate " + countEatenPortions);

                                wellFed = eatPortion(id, developer, countEatenPortions);
                            } finally {
                                lock.unlock();
                            }

                            releaseSpoons(developer, spoonIds);
                            System.out.println("developer-" + id + " dropped spoons");
                        } else {
                            takeSpoons(id, developer, spoonIds);
                        }
                    }
                    System.out.println("developer-" + id + " total ate: " + countEatenPortions);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    eatenPortionsByDeveloper[id] = countEatenPortions;
                    developerFinished();
                }
            }, "developer-" + id);
            developers[i].start();
        }
        return developers;
    }

    private int[] calculateSpoonId(int developerId) {
        return new int[] {
                (developerId - 1 + spoons.size()) % spoons.size(),
                developerId
        };
    }

    private int calculateTargetPortionCount(int developerId) {
        return developerId < extraPortionCount
                ? basePortionCount + 1
                : basePortionCount;
    }

    private boolean waitForPortionIfNeeded(int developerId) throws InterruptedException {
        lock.lock();
        try {
            if (developerStates.get(developerId).getPortionState() == WAITING) {
                System.out.println("developer-" + developerId + " wait soup");
                orders.add(developerId);
                hasOrder.signal();

                while (developerStates.get(developerId).getPortionState() == WAITING) {
                    developerStates.get(developerId).getPortionReadyCondition().await();
                }
            }

            return developerStates.get(developerId).getPortionState() != NO_MORE_PORTIONS;
        } finally {
            lock.unlock();
        }
    }

    private boolean eatPortion(int developerId, DefaultDeveloper developer, int countEatenPortions) {
        boolean wellFed = developer.isWellFed(countEatenPortions);
        if (!wellFed) {
            developerStates.get(developerId).setPortionState(WAITING);
        }
        return wellFed;
    }

    private void releaseSpoons(DefaultDeveloper developer, int[] spoonIds) {
        spoons.get(spoonIds[0]).lock().unlock();
        spoons.get(spoonIds[1]).lock().unlock();
        developer.dropSpoons();
    }

    private void takeSpoons(int developerId, DefaultDeveloper developer, int[] spoonIds) {
        Spoon right = spoons.get(spoonIds[1]);
        Spoon left = spoons.get(spoonIds[0]);
        if (developerId % 2 == 0) {
            right.lock().lock();
            left.lock().lock();
            developer.takeSpoons(right, SpoonType.RIGHT);
            developer.takeSpoons(left, SpoonType.LEFT);
            System.out.println("developer-" + developerId + " took spoons");
        } else {
            left.lock().lock();
            right.lock().lock();
            developer.takeSpoons(left, SpoonType.LEFT);
            developer.takeSpoons(right, SpoonType.RIGHT);
            System.out.println("developer-" + developerId + " took spoons");
        }
    }

    private void developerFinished() {
        lock.lock();
        try {
            finishedDevelopers++;
            if (finishedDevelopers == programmersCount) {
                lunchFinished = true;
                hasOrder.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    private Thread[] createWaiters(int count) {
        Thread[] waiters = new Thread[count];
        for (int i = 0; i < count; i++) {
            final int id = i + 1;
            waiters[i] = new Thread(() -> {
                while (true) {
                    Integer developerId = waitForOrder();
                    if (developerId == null) {
                        break;
                    }

                    serveOrder(id, developerId);
                }
            }, "waiter-" + id);
            waiters[i].start();
        }
        return waiters;
    }

    private Integer waitForOrder() {
        lock.lock();
        try {
            while (orders.isEmpty() && !lunchFinished) {
                hasOrder.await();
            }

            if (lunchFinished && orders.isEmpty()) {
                return null;
            }

            return orders.poll();
        } catch (InterruptedException e) {
            currentThread().interrupt();
        } finally {
            lock.unlock();
        }
        return null;
    }

    private void serveOrder(int waiterId, int developerId) {
        lock.lock();
        try {
            DeveloperState state = developerStates.get(developerId);
            if (remainingPortions > 0) {
                remainingPortions--;
                state.setPortionState(HAS_PORTION);
                System.out.println("waiter-" + waiterId + " bring soup. Portions left: " + remainingPortions);
            } else {
                state.setPortionState(NO_MORE_PORTIONS);
            }
            developerStates.get(developerId).getPortionReadyCondition().signal();
        } finally {
            lock.unlock();
        }
    }

    private void waitForThreads(Thread[] threads) throws InterruptedException {
        for (Thread thread : threads) {
            thread.join();
        }
    }

    private List<Integer> getEatenPortionsResult() {
        List<Integer> result = new ArrayList<>(eatenPortionsByDeveloper.length);

        for (int eatenPortions : eatenPortionsByDeveloper) {
            result.add(eatenPortions);
        }

        return result;
    }

}
