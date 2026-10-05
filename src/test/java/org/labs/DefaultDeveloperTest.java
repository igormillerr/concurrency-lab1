package org.labs;

import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;

class DefaultDeveloperTest {

    @Test
    void shouldNotHaveSpoonsAfterCreation() {
        DefaultDeveloper developer = new DefaultDeveloper(5);

        assertFalse(developer.hasLeftSpoon());
        assertFalse(developer.hasRightSpoon());
    }

    @Test
    void shouldTakeRightSpoon() {
        DefaultDeveloper developer = new DefaultDeveloper(5);
        Spoon spoon = new Spoon(0, new ReentrantLock());

        developer.takeSpoons(spoon, SpoonType.RIGHT);

        assertTrue(developer.hasRightSpoon());
        assertFalse(developer.hasLeftSpoon());
    }

    @Test
    void shouldTakeLeftSpoon() {
        DefaultDeveloper developer = new DefaultDeveloper(5);
        Spoon spoon = new Spoon(0, new ReentrantLock());

        developer.takeSpoons(spoon, SpoonType.LEFT);

        assertTrue(developer.hasLeftSpoon());
        assertFalse(developer.hasRightSpoon());
    }

    @Test
    void shouldHaveBothSpoonsAfterTakingBoth() {
        DefaultDeveloper developer = new DefaultDeveloper(5);

        Spoon leftSpoon = new Spoon(0, new ReentrantLock());
        Spoon rightSpoon = new Spoon(1, new ReentrantLock());

        developer.takeSpoons(leftSpoon, SpoonType.LEFT);
        developer.takeSpoons(rightSpoon, SpoonType.RIGHT);

        assertTrue(developer.hasLeftSpoon());
        assertTrue(developer.hasRightSpoon());
    }

    @Test
    void shouldDropBothSpoons() {
        DefaultDeveloper developer = new DefaultDeveloper(5);

        Spoon leftSpoon = new Spoon(0, new ReentrantLock());
        Spoon rightSpoon = new Spoon(1, new ReentrantLock());

        developer.takeSpoons(leftSpoon, SpoonType.LEFT);
        developer.takeSpoons(rightSpoon, SpoonType.RIGHT);

        developer.dropSpoons();

        assertFalse(developer.hasLeftSpoon());
        assertFalse(developer.hasRightSpoon());
    }

    @Test
    void shouldNotBeWellFedBeforeTargetPortionCount() {
        DefaultDeveloper developer = new DefaultDeveloper(5);

        assertFalse(developer.isWellFed(4));
    }

    @Test
    void shouldBeWellFedWhenTargetPortionCountReached() {
        DefaultDeveloper developer = new DefaultDeveloper(5);

        assertTrue(developer.isWellFed(5));
    }

    @Test
    void shouldRemainWellFedWhenTargetPortionCountExceeded() {
        DefaultDeveloper developer = new DefaultDeveloper(5);

        assertTrue(developer.isWellFed(6));
    }

}