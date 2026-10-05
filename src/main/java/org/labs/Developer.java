package org.labs;

public interface Developer {

    void takeSpoons(Spoon spoon, SpoonType type);

    boolean hasRightSpoon();

    boolean hasLeftSpoon();

    void dropSpoons();

    boolean isWellFed(int countEatenPortions);

}
