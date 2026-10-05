package org.labs;

import lombok.Getter;

@Getter
public class DefaultDeveloper implements Developer {

    private final int targetPortions;

    private Spoon rightSpoon = null;
    private Spoon leftSpoon = null;

    public DefaultDeveloper(int targetPortions) {
        this.targetPortions = targetPortions;
    }

    @Override
    public void takeSpoons(Spoon spoon, SpoonType type) {
        switch (type) {
            case RIGHT ->  rightSpoon = spoon;
            case LEFT -> leftSpoon = spoon;
        }
    }

    @Override
    public boolean hasRightSpoon() {
        return rightSpoon != null;
    }

    @Override
    public boolean hasLeftSpoon() {
        return leftSpoon != null;
    }

    @Override
    public void dropSpoons() {
        rightSpoon = null;
        leftSpoon = null;
    }

    @Override
    public boolean isWellFed(int countEatenPortions) {
        return targetPortions <= countEatenPortions;
    }

}
