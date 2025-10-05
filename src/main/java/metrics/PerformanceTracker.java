package metrics;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Tracks and records performance metrics for algorithm analysis.
 * Collects comparisons, swaps, array accesses, and timing data.
 * Supports snapshot storage and CSV export for empirical validation.
 */
public class PerformanceTracker {
    private long comparisons;
    private long swaps;
    private long arrayAccesses;
    private long startTime;
    private long endTime;
    private List<MetricSnapshot> snapshots;

    /**
     * Represents a snapshot of metrics at a specific point.
     * Used for storing benchmark results across multiple runs.
     */
    public static class MetricSnapshot {
        public final int inputSize;
        public final long comparisons;
        public final long swaps;
        public final long arrayAccesses;
        public final long timeNanos;
        public final String inputType;

        public MetricSnapshot(int inputSize, long comparisons, long swaps,
                              long arrayAccesses, long timeNanos, String inputType) {
            this.inputSize = inputSize;
            this.comparisons = comparisons;
            this.swaps = swaps;
            this.arrayAccesses = arrayAccesses;
            this.timeNanos = timeNanos;
            this.inputType = inputType;
        }

        @Override
        public String toString() {
            return String.format("Size=%d, Type=%s, Comparisons=%d, Swaps=%d, Time=%.3fms",
                    inputSize, inputType, comparisons, swaps, timeNanos / 1_000_000.0);
        }
    }

    /**
     * Creates a new PerformanceTracker with all metrics initialized to zero.
     */
    public PerformanceTracker() {
        reset();
        this.snapshots = new ArrayList<>();
    }

    /**
     * Resets all metrics to zero.
     * Does not clear saved snapshots.
     */
    public void reset() {
        this.comparisons = 0;
        this.swaps = 0;
        this.arrayAccesses = 0;
        this.startTime = 0;
        this.endTime = 0;
    }

    /**
     * Starts timing measurement using System.nanoTime().
     * Should be called immediately before the algorithm starts.
     */
    public void startTiming() {
        this.startTime = System.nanoTime();
    }

    /**
     * Stops timing measurement.
     * Should be called immediately after the algorithm completes.
     */
    public void stopTiming() {
        this.endTime = System.nanoTime();
    }

    /**
     * Records a single comparison operation.
     * Call this whenever two elements are compared.
     */
    public void recordComparison() {
        this.comparisons++;
    }

    /**
     * Records multiple comparison operations.
     * Useful for batch recording.
     *
     * @param count number of comparisons to record
     */
    public void recordComparisons(int count) {
        this.comparisons += count;
    }

    /**
     * Records a single swap/move operation.
     * Call this whenever an element is moved or swapped.
     */
    public void recordSwap() {
        this.swaps++;
    }

    /**
     * Records multiple swap operations.
     *
     * @param count number of swaps to record
     */
    public void recordSwaps(int count) {
        this.swaps += count;
    }

    /**
     * Records a single array access operation.
     * Call this for both reads and writes to array elements.
     */
    public void recordArrayAccess() {
        this.arrayAccesses++;
    }

    /**
     * Records multiple array access operations.
     *
     * @param count number of array accesses to record
     */
    public void recordArrayAccesses(int count) {
        this.arrayAccesses += count;
    }

    /**
     * Saves current metrics as a snapshot for later analysis.
     * Does not reset the current metrics.
     *
     * @param inputSize the size of the input array
     * @param inputType description of input type (e.g., "Random", "Sorted", "Reverse")
     */
    public void saveSnapshot(int inputSize, String inputType) {
        snapshots.add(new MetricSnapshot(
                inputSize, comparisons, swaps, arrayAccesses,
                getElapsedTimeNanos(), inputType
        ));
    }

    /**
     * Gets the total number of comparisons recorded.
     *
     * @return number of comparisons
     */
    public long getComparisons() {
        return comparisons;
    }

    /**
     * Gets the total number of swaps recorded.
     *
     * @return number of swaps
     */
    public long getSwaps() {
        return swaps;
    }

    /**
     * Gets the total number of array accesses recorded.
     *
     * @return number of array accesses
     */
    public long getArrayAccesses() {
        return arrayAccesses;
    }

    /**
     * Gets the elapsed time in nanoseconds.
     *
     * @return elapsed time in nanoseconds, or 0 if timing not started/stopped
     */
    public long getElapsedTimeNanos() {
        if (startTime == 0 || endTime == 0) {
            return 0;
        }
        return endTime - startTime;
    }

    /**
     * Gets the elapsed time in milliseconds.
     *
     * @return elapsed time in milliseconds with decimal precision
     */
    public double getElapsedTimeMillis() {
        return getElapsedTimeNanos() / 1_000_000.0;
    }

    /**
     * Gets the elapsed time in seconds.
     *
     * @return elapsed time in seconds with decimal precision
     */
    public double getElapsedTimeSeconds() {
        return getElapsedTimeNanos() / 1_000_000_000.0;
    }

    /**
     * Exports all snapshots to a CSV file for analysis and plotting.
     * Creates a file with headers and one row per snapshot.
     *
     * @param filename the name of the CSV file to create
     * @throws IOException if file cannot be written
     */
    public void exportToCSV(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write header
            writer.println("InputSize,InputType,Comparisons,Swaps,ArrayAccesses,TimeNanos,TimeMillis");

            // Write each snapshot
            for (MetricSnapshot snapshot : snapshots) {
                writer.printf("%d,%s,%d,%d,%d,%d,%.3f%n",
                        snapshot.inputSize,
                        snapshot.inputType,
                        snapshot.comparisons,
                        snapshot.swaps,
                        snapshot.arrayAccesses,
                        snapshot.timeNanos,
                        snapshot.timeNanos / 1_000_000.0
                );
            }
        }
    }

    /**
     * Exports snapshots to CSV with custom delimiter.
     *
     * @param filename the name of the file to create
     * @param delimiter the delimiter to use (e.g., "," or ";")
     * @throws IOException if file cannot be written
     */
    public void exportToCSV(String filename, String delimiter) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write header
            writer.println(String.join(delimiter,
                    "InputSize", "InputType", "Comparisons", "Swaps",
                    "ArrayAccesses", "TimeNanos", "TimeMillis"));

            // Write each snapshot
            for (MetricSnapshot snapshot : snapshots) {
                writer.printf("%d%s%s%s%d%s%d%s%d%s%d%s%.3f%n",
                        snapshot.inputSize, delimiter,
                        snapshot.inputType, delimiter,
                        snapshot.comparisons, delimiter,
                        snapshot.swaps, delimiter,
                        snapshot.arrayAccesses, delimiter,
                        snapshot.timeNanos, delimiter,
                        snapshot.timeNanos / 1_000_000.0
                );
            }
        }
    }

    /**
     * Returns a formatted string of current metrics.
     * Useful for console output and debugging.
     *
     * @return human-readable summary of metrics
     */
    public String getMetricsSummary() {
        return String.format(
                "Comparisons: %,d | Swaps: %,d | Array Accesses: %,d | Time: %.3f ms",
                comparisons, swaps, arrayAccesses, getElapsedTimeMillis()
        );
    }

    /**
     * Returns a detailed formatted string including all metrics.
     *
     * @return detailed metrics summary
     */
    public String getDetailedSummary() {
        return String.format(
                "Performance Metrics:\n" +
                        "  Comparisons:    %,15d\n" +
                        "  Swaps:          %,15d\n" +
                        "  Array Accesses: %,15d\n" +
                        "  Time (ns):      %,15d\n" +
                        "  Time (ms):      %,15.3f\n" +
                        "  Time (s):       %,15.6f",
                comparisons, swaps, arrayAccesses,
                getElapsedTimeNanos(), getElapsedTimeMillis(), getElapsedTimeSeconds()
        );
    }

    /**
     * Clears all saved snapshots but keeps current metrics.
     */
    public void clearSnapshots() {
        snapshots.clear();
    }

    /**
     * Gets a copy of all saved snapshots.
     *
     * @return list of metric snapshots
     */
    public List<MetricSnapshot> getSnapshots() {
        return new ArrayList<>(snapshots);
    }

    /**
     * Gets the number of saved snapshots.
     *
     * @return number of snapshots
     */
    public int getSnapshotCount() {
        return snapshots.size();
    }

    /**
     * Calculates the ratio of current metrics to a baseline.
     * Useful for comparing optimizations.
     *
     * @param baseline the baseline tracker to compare against
     * @return string showing ratios
     */
    public String compareToBaseline(PerformanceTracker baseline) {
        double compRatio = (double) this.comparisons / baseline.comparisons;
        double swapRatio = (double) this.swaps / baseline.swaps;
        double timeRatio = (double) this.getElapsedTimeNanos() / baseline.getElapsedTimeNanos();

        return String.format(
                "Comparison Ratio: %.2fx | Swap Ratio: %.2fx | Time Ratio: %.2fx",
                compRatio, swapRatio, timeRatio
        );
    }

    /**
     * Prints all snapshots to console in a formatted table.
     */
    public void printSnapshotsTable() {
        System.out.println("=".repeat(100));
        System.out.printf("%-10s | %-15s | %12s | %12s | %15s | %12s%n",
                "Size", "Type", "Comparisons", "Swaps", "ArrayAccesses", "Time (ms)");
        System.out.println("-".repeat(100));

        for (MetricSnapshot snapshot : snapshots) {
            System.out.printf("%-10d | %-15s | %,12d | %,12d | %,15d | %,12.3f%n",
                    snapshot.inputSize,
                    snapshot.inputType,
                    snapshot.comparisons,
                    snapshot.swaps,
                    snapshot.arrayAccesses,
                    snapshot.timeNanos / 1_000_000.0
            );
        }
        System.out.println("=".repeat(100));
    }

    /**
     * Gets average metrics across all snapshots.
     * Useful for summarizing multiple benchmark runs.
     *
     * @return string with average values
     */
    public String getAverageMetrics() {
        if (snapshots.isEmpty()) {
            return "No snapshots available";
        }

        long totalComparisons = 0;
        long totalSwaps = 0;
        long totalArrayAccesses = 0;
        long totalTime = 0;

        for (MetricSnapshot snapshot : snapshots) {
            totalComparisons += snapshot.comparisons;
            totalSwaps += snapshot.swaps;
            totalArrayAccesses += snapshot.arrayAccesses;
            totalTime += snapshot.timeNanos;
        }

        int count = snapshots.size();
        return String.format(
                "Average across %d runs:\n" +
                        "  Comparisons: %,d | Swaps: %,d | Array Accesses: %,d | Time: %.3f ms",
                count,
                totalComparisons / count,
                totalSwaps / count,
                totalArrayAccesses / count,
                (totalTime / count) / 1_000_000.0
        );
    }
}