package com.snapreserve.snapreserve;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;

import java.util.Optional;

public class NoOpLockProvider implements LockProvider {

    @Override
    public Optional<SimpleLock> lock(net.javacrumbs.shedlock.core.LockConfiguration lockConfiguration) {
        // Always return a lock, effectively disabling the locking mechanism
        return Optional.of(new NoOpSimpleLock());
    }

    private static class NoOpSimpleLock implements SimpleLock {
        @Override
        public void unlock() {
            // Do nothing - no locking mechanism in tests
        }
    }
}
