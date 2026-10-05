package org.labs;

import java.util.concurrent.locks.ReentrantLock;

public record Spoon(int positionNumber, ReentrantLock lock) {
}
