package com.algorithms.metrics;

/**
 * Tracks performance metrics for algorithm analysis
 * Meets requirements: Metrics collection, Memory profiling
 */
public class PerformanceTracker {
    private long comparisons;
    private long swaps;
    private long arrayAccesses;
    private long startTime;
    private long endTime;
    private long memoryBefore;
    private long memoryAfter;

    public PerformanceTracker() {
        reset();
    }

    public void reset() {
        comparisons = 0;
        swaps = 0;
        arrayAccesses = 0;
        startTime = 0;
        endTime = 0;
        memoryBefore = 0;
        memoryAfter = 0;
    }

    public void startTimer() {
        // Memory profiling as required
        System.gc();
        try { Thread.sleep(10); } catch (InterruptedException e) {}
        memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        startTime = System.nanoTime();
    }

    public void stopTimer() {
        endTime = System.nanoTime();
        System.gc();
        try { Thread.sleep(10); } catch (InterruptedException e) {}
        memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    public void incrementComparisons() {
        comparisons++;
    }

    public void incrementComparisons(int count) {
        comparisons += count;
    }

    public void incrementSwaps() {
        swaps++;
    }

    public void incrementArrayAccesses() {
        arrayAccesses++;
    }

    public void incrementArrayAccesses(int count) {
        arrayAccesses += count;
    }

    // Getters
    public long getComparisons() { return comparisons; }
    public long getSwaps() { return swaps; }
    public long getArrayAccesses() { return arrayAccesses; }
    public long getElapsedTimeNanos() { return endTime - startTime; }
    public double getElapsedTimeMillis() { return (endTime - startTime) / 1_000_000.0; }
    public long getMemoryUsed() { return Math.max(0, memoryAfter - memoryBefore); }

    @Override
    public String toString() {
        return String.format(
                "Time: %.3f ms, Comparisons: %d, Swaps: %d, Array Accesses: %d, Memory: %d bytes",
                getElapsedTimeMillis(), comparisons, swaps, arrayAccesses, getMemoryUsed()
        );
    }

    public String toCSV() {
        return String.format("%.3f,%d,%d,%d,%d",
                getElapsedTimeMillis(), comparisons, swaps, arrayAccesses, getMemoryUsed());
    }
}