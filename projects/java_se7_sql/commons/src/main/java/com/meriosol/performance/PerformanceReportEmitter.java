package com.meriosol.performance;

/**
 * Interface for performance stats generators.
 * @author meriosol
 * @version 0.1
 * @since 21/01/14
 */
interface PerformanceReportEmitter {
    /**
     *
     * @param performanceTracker
     * @return Report with tracking session stats (such as durations, memory consumption).
     */
    String emit(PerformanceTracker performanceTracker);
}
