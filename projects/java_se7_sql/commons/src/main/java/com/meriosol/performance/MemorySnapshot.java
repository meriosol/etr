package com.meriosol.performance;

/**
 * Keeps memory state (NOTE: returns data in megabytes).
 * @author meriosol
 * @version 0.1
 * @since 21/01/14
 */
public class MemorySnapshot {
    private static final int MB = 1024 * 1024;
    private static final Runtime RUNTIME = Runtime.getRuntime();
    private long maxMemory;
    private long totalMemory;
    private long freeMemory;

    public MemorySnapshot() {
        this.maxMemory = RUNTIME.maxMemory();
        this.totalMemory = RUNTIME.totalMemory();
        this.freeMemory = RUNTIME.freeMemory();
    }

    public long getTotalMemoryInMegs() {
        return adjustToMegs(totalMemory);
    }

    public long getFreeMemoryInMegs() {
        return adjustToMegs(freeMemory);
    }

    public long getMaxMemoryInMegs() {
        return adjustToMegs(maxMemory);
    }

    public long getUsedMemoryInMegs() {
        return adjustToMegs(totalMemory - freeMemory);
    }

    private static long adjustToMegs(long value) {
        return value / MB;
    }
}
