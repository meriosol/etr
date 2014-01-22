package com.meriosol.performance;

/**
 * Keeps sessionName and time for particular code execution milestone.
 * @author meriosol
 * @version 0.1
 * @since 21/01/14
 */
public class Milestone {
    private String name;
    private long time;
    private MemorySnapshot memorySnapshot;

    Milestone(String name) {
        this.name = name;
        // TODO: nano is too sharp time, but generally msecs level estimates are needed..
        //this.time = System.nanoTime();
        this.time = System.currentTimeMillis();
        this.memorySnapshot = new MemorySnapshot();
    }

    public String getName() {
        return name;
    }

    public long getTime() {
        return time;
    }

    public MemorySnapshot getMemorySnapshot() {
        return memorySnapshot;
    }
}
