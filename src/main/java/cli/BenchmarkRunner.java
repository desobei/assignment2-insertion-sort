package cli;

import algorithms.InsertionSort;
import metrics.PerformanceTracker;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * Command-line interface for benchmarking InsertionSort with various input configurations.
 * Supports different input sizes and distributions for comprehensive performance analysis.
 *
 * Usage:
 *   java cli.BenchmarkRunner [sizes...]
 *   mvn exec:java
 *   mvn exec:java -Dexec.args="100 1000 10000"
 */
public class BenchmarkRunner {

    private static final int[] DEFAULT_SIZES = {100, 1000, 10000, 100000};
    private static final int WARMUP_ITERATIONS = 3;
    private static final int MEASUREMENT_ITERATIONS = 5;
    private static final Random RANDOM = new Random(42); // Fixed seed for reproducibility

    public static void main(String[] args) {
        printHeader();

        if (args.length > 0 && args[0].equals("--help")) {
            printUsage();
            return;
        }

        int[] sizes = parseSizes(args);

        BenchmarkRunner runner = new BenchmarkRunner();
        runner.runComprehensiveBenchmark(sizes);
    }

    /**
     * Prints the application header.
     */
    private static void printHeader() {
        System.out.println("=".repeat(80));
        System.out.println("Insertion Sort Benchmark Runner");
        System.out.println("Comprehensive Performance Analysis Tool");
        System.out.println("=".repeat(80));
        System.out.println();
    }

    /**
     * Prints usage information.
     */
    private static void printUsage() {
        System.out.println("Usage: java -cp target/classes cli.BenchmarkRunner [sizes...]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  sizes    Optional space-separated list of input sizes to benchmark");
        System.out.println("           Default: 100 1000 10000 100000");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -cp target/classes cli.BenchmarkRunner");
        System.out.println("  java -cp target/classes cli.BenchmarkRunner 500 5000 50000");
        System.out.println();
        System.out.println("Maven Examples:");
        System.out.println("  mvn exec:java");
        System.out.println("  mvn exec:java -Dexec.args=\"500 5000 50000\"");
        System.out.println();
        System.out.println("Input Types Tested:");
        System.out.println("  - Random:        Randomly distributed values");
        System.out.println("  - Sorted:        Already sorted (best case)");
        System.out.println("  - Reverse:       Reverse sorted (worst case)");
        System.out.println("  - Nearly-Sorted: 90% sorted with random perturbations");
        System.out.println("  - Few-Unique:    Many duplicate values");
        System.out.println();
        System.out.println("Output:");
        System.out.println("  - Console: Formatted performance metrics");
        System.out.println("  - CSV:     benchmark_results.csv for plotting and analysis");
    }

    /**
     * Parses command-line arguments for input sizes.
     */
    private static int[] parseSizes(String[] args) {
        if (args.length == 0) {
            return DEFAULT_SIZES;
        }

        int[] sizes = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            try {
                sizes[i] = Integer.parseInt(args[i]);
                if (sizes[i] <= 0) {
                    System.err.println("Error: Size must be positive: " + args[i]);
                    System.exit(1);
                }
            } catch (NumberFormatException e) {
                System.err.println("Error: Invalid size: " + args[i]);
                System.err.println("Run with --help for usage information");
                System.exit(1);
            }
        }
        return sizes;
    }

    /**
     * Runs comprehensive benchmark across all input sizes and distributions.
     */
    public void runComprehensiveBenchmark(int[] sizes) {
        PerformanceTracker tracker = new PerformanceTracker();
        InsertionSort sorter = new InsertionSort(tracker);

        System.out.println("Running comprehensive benchmark...");
        System.out.println("Input sizes: " + Arrays.toString(sizes));
        System.out.println("Warmup iterations: " + WARMUP_ITERATIONS);
        System.out.println("Measurement iterations: " + MEASUREMENT_ITERATIONS);
        System.out.println();

        long totalStartTime = System.currentTimeMillis();

        // Test each input size
        for (int size : sizes) {
            System.out.println("-".repeat(80));
            System.out.printf("Benchmarking size: %,d%n", size);
            System.out.println("-".repeat(80));

            // Test different input distributions
            benchmarkInputType(sorter, tracker, size, "Random", this::generateRandomArray);
            benchmarkInputType(sorter, tracker, size, "Sorted", this::generateSortedArray);
            benchmarkInputType(sorter, tracker, size, "Reverse", this::generateReverseArray);
            benchmarkInputType(sorter, tracker, size, "Nearly-Sorted", this::generateNearlySortedArray);
            benchmarkInputType(sorter, tracker, size, "Few-Unique", this::generateFewUniqueArray);

            System.out.println();
        }

        long totalEndTime = System.currentTimeMillis();
        double totalTimeSeconds = (totalEndTime - totalStartTime) / 1000.0;

        // Print summary table
        System.out.println();
        System.out.println("=".repeat(80));
        System.out.println("BENCHMARK SUMMARY");
        System.out.println("=".repeat(80));
        tracker.printSnapshotsTable();

        // Export results to CSV
        try {
            String filename = "benchmark_results.csv";
            tracker.exportToCSV(filename);
            System.out.println();
            System.out.println("✓ Results exported to: " + filename);
            System.out.println("  Use this file for creating performance plots and analysis");
        } catch (IOException e) {
            System.err.println("✗ Error exporting results: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=".repeat(80));
        System.out.printf("Benchmark Complete - Total time: %.2f seconds%n", totalTimeSeconds);
        System.out.printf("Total snapshots collected: %d%n", tracker.getSnapshotCount());
        System.out.println("=".repeat(80));
    }

    /**
     * Benchmarks a specific input type with warmup and multiple iterations.
     */
    private void benchmarkInputType(InsertionSort sorter, PerformanceTracker tracker,
                                    int size, String inputType, ArrayGenerator generator) {
        // Warmup phase - JVM optimization
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            int[] arr = generator.generate(size);
            sorter.sort(arr);
        }

        // Measurement phase
        long totalComparisons = 0;
        long totalSwaps = 0;
        long totalArrayAccesses = 0;
        long totalTimeNanos = 0;

        for (int i = 0; i < MEASUREMENT_ITERATIONS; i++) {
            int[] arr = generator.generate(size);

            tracker.reset();
            tracker.startTiming();
            sorter.sort(arr);
            tracker.stopTiming();

            totalComparisons += tracker.getComparisons();
            totalSwaps += tracker.getSwaps();
            totalArrayAccesses += tracker.getArrayAccesses();
            totalTimeNanos += tracker.getElapsedTimeNanos();

            // Verify correctness
            if (!InsertionSort.isSorted(arr)) {
                System.err.println("ERROR: Array not sorted correctly!");
                System.err.println("  Size: " + size + ", Type: " + inputType);
                return;
            }
        }

        // Calculate averages
        long avgComparisons = totalComparisons / MEASUREMENT_ITERATIONS;
        long avgSwaps = totalSwaps / MEASUREMENT_ITERATIONS;
        long avgArrayAccesses = totalArrayAccesses / MEASUREMENT_ITERATIONS;
        long avgTimeNanos = totalTimeNanos / MEASUREMENT_ITERATIONS;

        // Create snapshot with average values
        PerformanceTracker.MetricSnapshot snapshot = new PerformanceTracker.MetricSnapshot(
                size, avgComparisons, avgSwaps, avgArrayAccesses, avgTimeNanos, inputType
        );

        // Manually add snapshot (workaround for average tracking)
        tracker.reset();
        for (int i = 0; i < avgComparisons; i++) tracker.recordComparison();
        for (int i = 0; i < avgSwaps; i++) tracker.recordSwap();
        for (int i = 0; i < avgArrayAccesses; i++) tracker.recordArrayAccess();
        tracker.saveSnapshot(size, inputType);

        // Print results
        printBenchmarkResult(inputType, avgComparisons, avgSwaps, avgArrayAccesses, avgTimeNanos);

        // Additional analysis for specific cases
        if (inputType.equals("Sorted")) {
            analyzeComplexity(size, avgComparisons, "Best Case O(n)", size);
        } else if (inputType.equals("Reverse")) {
            analyzeComplexity(size, avgComparisons, "Worst Case O(n²)", (long)size * size / 2);
        }
    }

    /**
     * Prints formatted benchmark result.
     */
    private void printBenchmarkResult(String inputType, long comparisons, long swaps,
                                      long arrayAccesses, long timeNanos) {
        double timeMillis = timeNanos / 1_000_000.0;

        System.out.printf("  %-15s | Comp: %,12d | Swaps: %,12d | Access: %,12d | Time: %,8.3f ms%n",
                inputType, comparisons, swaps, arrayAccesses, timeMillis);
    }

    /**
     * Analyzes and prints complexity verification.
     */
    private void analyzeComplexity(int size, long actual, String expected, long theoretical) {
        double ratio = (double) actual / theoretical;
        String status = (ratio >= 0.5 && ratio <= 2.0) ? "✓" : "⚠";
        System.out.printf("    %s Complexity check: %s (ratio: %.2f, theoretical: %,d)%n",
                status, expected, ratio, theoretical);
    }

    /**
     * Runs a quick single-size benchmark for testing.
     */
    public void quickBenchmark(int size) {
        System.out.printf("Quick benchmark for size %,d:%n%n", size);

        PerformanceTracker tracker = new PerformanceTracker();
        InsertionSort sorter = new InsertionSort(tracker);

        System.out.println("Testing Random array:");
        int[] arr = generateRandomArray(size);

        tracker.reset();
        tracker.startTiming();
        sorter.sort(arr);
        tracker.stopTiming();

        System.out.println(tracker.getDetailedSummary());
        System.out.println("Sorted correctly: " + InsertionSort.isSorted(arr));
    }

    /**
     * Compares standard insertion sort vs binary search variant.
     */
    public void compareVariants(int size) {
        System.out.printf("Comparing Insertion Sort variants for size %,d:%n%n", size);

        PerformanceTracker tracker1 = new PerformanceTracker();
        PerformanceTracker tracker2 = new PerformanceTracker();

        InsertionSort sorter1 = new InsertionSort(tracker1);
        InsertionSort sorter2 = new InsertionSort(tracker2);

        // Generate same array for fair comparison
        int[] arr1 = generateRandomArray(size);
        int[] arr2 = arr1.clone();

        // Test standard insertion sort
        System.out.println("Standard Insertion Sort:");
        tracker1.startTiming();
        sorter1.sort(arr1);
        tracker1.stopTiming();
        System.out.println(tracker1.getMetricsSummary());

        System.out.println();

        // Test binary search variant
        System.out.println("Binary Search Variant:");
        tracker2.startTiming();
        sorter2.sortWithBinarySearch(arr2);
        tracker2.stopTiming();
        System.out.println(tracker2.getMetricsSummary());

        System.out.println();
        System.out.println("Comparison:");
        System.out.println(tracker2.compareToBaseline(tracker1));

        // Verify both sorted correctly
        System.out.println();
        System.out.println("Both sorted correctly: " +
                (InsertionSort.isSorted(arr1) && InsertionSort.isSorted(arr2)));
    }

    // ==================== Array Generation Methods ====================

    /**
     * Generates a random array of specified size.
     * Values range from 0 to size*10.
     */
    private int[] generateRandomArray(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = RANDOM.nextInt(size * 10);
        }
        return arr;
    }

    /**
     * Generates an already sorted array.
     * Best case for insertion sort - O(n) performance.
     */
    private int[] generateSortedArray(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = i;
        }
        return arr;
    }

    /**
     * Generates a reverse sorted array.
     * Worst case for insertion sort - O(n²) performance.
     */
    private int[] generateReverseArray(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = size - i;
        }
        return arr;
    }

    /**
     * Generates a nearly sorted array (90% sorted, 10% random swaps).
     * Tests the nearly-sorted optimization of insertion sort.
     */
    private int[] generateNearlySortedArray(int size) {
        int[] arr = generateSortedArray(size);

        // Randomly swap 10% of elements
        int swaps = Math.max(size / 10, 1);
        for (int i = 0; i < swaps; i++) {
            int idx1 = RANDOM.nextInt(size);
            int idx2 = RANDOM.nextInt(size);
            int temp = arr[idx1];
            arr[idx1] = arr[idx2];
            arr[idx2] = temp;
        }
        return arr;
    }

    /**
     * Generates an array with few unique values (lots of duplicates).
     * Tests handling of duplicate elements.
     */
    private int[] generateFewUniqueArray(int size) {
        int[] arr = new int[size];
        int uniqueValues = Math.max(size / 100, 5); // ~1% unique values

        for (int i = 0; i < size; i++) {
            arr[i] = RANDOM.nextInt(uniqueValues);
        }
        return arr;
    }

    /**
     * Generates an array with specific pattern for testing.
     */
    private int[] generatePatternArray(int size, String pattern) {
        int[] arr = new int[size];

        switch (pattern.toLowerCase()) {
            case "sawtooth":
                // Repeating pattern: 0,1,2,...,k,0,1,2,...,k
                int period = Math.min(size / 10, 100);
                for (int i = 0; i < size; i++) {
                    arr[i] = i % period;
                }
                break;

            case "alternating":
                // Alternating high/low: max,0,max,0,...
                for (int i = 0; i < size; i++) {
                    arr[i] = (i % 2 == 0) ? size : 0;
                }
                break;

            case "organ-pipe":
                // Ascending then descending: 0,1,2,...,mid,...,2,1,0
                int mid = size / 2;
                for (int i = 0; i < size; i++) {
                    arr[i] = (i < mid) ? i : (size - i - 1);
                }
                break;

            default:
                return generateRandomArray(size);
        }

        return arr;
    }

    /**
     * Functional interface for array generation.
     */
    @FunctionalInterface
    private interface ArrayGenerator {
        int[] generate(int size);
    }

    /**
     * Entry point for quick testing.
     */
    public static void quickTest() {
        BenchmarkRunner runner = new BenchmarkRunner();
        runner.quickBenchmark(1000);
    }

    /**
     * Entry point for variant comparison.
     */
    public static void compareTest() {
        BenchmarkRunner runner = new BenchmarkRunner();
        runner.compareVariants(5000);
    }
}