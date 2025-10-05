package com.algorithms.cli;

import com.algorithms.algorithms.InsertionSort;
import com.algorithms.metrics.PerformanceTracker;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * CLI for benchmarking - meets requirements: CLI interface, Performance testing across sizes
 */
public class BenchmarkRunner {

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String command = args[0].toLowerCase();

        switch (command) {
            case "test":
                runCorrectnessTests();
                break;
            case "benchmark":
                runPerformanceBenchmark();
                break;
            case "validate":
                runCrossValidation();
                break;
            case "single":
                if (args.length >= 3) {
                    runSingleTest(args);
                } else {
                    System.out.println("Usage: single <size> <data-type> [algorithm]");
                }
                break;
            default:
                System.out.println("Unknown command: " + command);
                printUsage();
        }
    }

    /**
     * Correctness validation - meets unit testing requirements
     */
    private static void runCorrectnessTests() {
        System.out.println("=== CORRECTNESS VALIDATION ===");

        PerformanceTracker tracker = new PerformanceTracker();
        InsertionSort sorter = new InsertionSort(tracker);

        // Test empty array
        testCase(sorter, new int[]{}, "Empty array");

        // Test single element
        testCase(sorter, new int[]{5}, "Single element");

        // Test already sorted
        testCase(sorter, new int[]{1, 2, 3, 4, 5}, "Already sorted");

        // Test reverse sorted
        testCase(sorter, new int[]{5, 4, 3, 2, 1}, "Reverse sorted");

        // Test with duplicates
        testCase(sorter, new int[]{3, 1, 4, 1, 5}, "With duplicates");

        // Test with negative numbers
        testCase(sorter, new int[]{-3, -1, -7, -2}, "Negative numbers");

        // Test mixed positive/negative
        testCase(sorter, new int[]{3, -1, 0, -5, 2}, "Mixed numbers");

        System.out.println("✓ All correctness tests completed");
    }

    /**
     * Performance benchmarking - meets scalability and distribution testing requirements
     */
    private static void runPerformanceBenchmark() {
        System.out.println("=== PERFORMANCE BENCHMARK ===");

        // Input sizes from 10^2 to 10^5 as required
        int[] sizes = {100, 500, 1000, 5000, 10000, 25000, 50000, 100000};
        String[] dataTypes = {"random", "sorted", "reverse", "nearly-sorted"};
        String[] algorithms = {"optimized", "binary", "adaptive"};

        try (FileWriter writer = new FileWriter("benchmark_results.csv")) {
            writer.write("Algorithm,Size,DataType,Time(ms),Comparisons,Swaps,ArrayAccesses,Memory\n");

            int testCount = 0;
            for (int size : sizes) {
                for (String dataType : dataTypes) {
                    // Skip very large sizes for slow cases (O(n²) becomes impractical)
                    if (size > 10000 && (dataType.equals("random") || dataType.equals("reverse"))) {
                        continue;
                    }

                    for (String algorithm : algorithms) {
                        testCount++;
                        System.out.printf("Test %d: %s sort, n=%d, %s data\n",
                                testCount, algorithm, size, dataType);

                        PerformanceTracker tracker = new PerformanceTracker();
                        InsertionSort sorter = new InsertionSort(tracker);

                        int[] data = generateData(size, dataType);
                        int[] dataCopy = Arrays.copyOf(data, data.length);

                        boolean success = runAlgorithm(sorter, algorithm, dataCopy);

                        if (success && isSorted(dataCopy)) {
                            writer.write(String.format("%s,%d,%s,%s\n",
                                    algorithm, size, dataType, tracker.toCSV()));
                            writer.flush();
                            System.out.printf("  ✓ %.3f ms, %d comparisons\n",
                                    tracker.getElapsedTimeMillis(), tracker.getComparisons());
                        } else {
                            System.out.println("  ✗ Failed");
                        }
                    }
                }
            }

            System.out.println("✓ Benchmark completed! Results saved to benchmark_results.csv");

        } catch (IOException e) {
            System.err.println("Error writing results: " + e.getMessage());
        }
    }

    /**
     * Cross-validation with Java's built-in sort - meets cross-validation requirement
     */
    private static void runCrossValidation() {
        System.out.println("=== CROSS-VALIDATION WITH JAVA SORT ===");

        Random random = new Random(42);
        int[] sizes = {100, 1000, 5000, 10000};
        int testsPassed = 0;
        int totalTests = 0;

        for (int size : sizes) {
            for (int i = 0; i < 5; i++) { // Multiple tests per size
                totalTests++;
                int[] original = generateRandomArray(size, random);
                int[] ourSorted = Arrays.copyOf(original, original.length);
                int[] javaSorted = Arrays.copyOf(original, original.length);

                PerformanceTracker tracker = new PerformanceTracker();
                InsertionSort sorter = new InsertionSort(tracker);
                sorter.sort(ourSorted);
                Arrays.sort(javaSorted);

                if (Arrays.equals(ourSorted, javaSorted)) {
                    testsPassed++;
                    System.out.printf("✓ n=%d, test %d: PASSED (%.3f ms)\n", size, i+1,
                            tracker.getElapsedTimeMillis());
                } else {
                    System.out.printf("✗ n=%d, test %d: FAILED\n", size, i+1);
                }
            }
        }

        System.out.printf("\nCross-validation: %d/%d tests passed (%.1f%%)\n",
                testsPassed, totalTests, (testsPassed * 100.0 / totalTests));
    }

    private static void runSingleTest(String[] args) {
        int size = Integer.parseInt(args[1]);
        String dataType = args[2];
        String algorithm = args.length > 3 ? args[3] : "optimized";

        System.out.printf("=== SINGLE TEST: %s sort on %s data (n=%d) ===\n",
                algorithm, dataType, size);

        PerformanceTracker tracker = new PerformanceTracker();
        InsertionSort sorter = new InsertionSort(tracker);

        int[] data = generateData(size, dataType);
        int[] dataCopy = Arrays.copyOf(data, data.length);

        System.out.println("First 10 elements: " + Arrays.toString(Arrays.copyOf(data, Math.min(10, data.length))));

        boolean success = runAlgorithm(sorter, algorithm, dataCopy);

        if (success && isSorted(dataCopy)) {
            System.out.println("✓ Sort successful");
            System.out.println(tracker.toString());

            // Cross-validate
            int[] javaSorted = Arrays.copyOf(data, data.length);
            Arrays.sort(javaSorted);
            if (Arrays.equals(dataCopy, javaSorted)) {
                System.out.println("✓ Cross-validation with Java sort: PASSED");
            } else {
                System.out.println("✗ Cross-validation with Java sort: FAILED");
            }
        } else {
            System.out.println("✗ Sort failed");
        }
    }

    private static void testCase(InsertionSort sorter, int[] input, String description) {
        int[] copy = Arrays.copyOf(input, input.length);
        int[] expected = Arrays.copyOf(input, input.length);
        Arrays.sort(expected);

        sorter.sort(copy);

        if (Arrays.equals(copy, expected)) {
            System.out.println("✓ " + description);
        } else {
            System.out.println("✗ " + description);
            System.out.println("  Expected: " + Arrays.toString(expected));
            System.out.println("  Got: " + Arrays.toString(copy));
        }
    }

    private static boolean runAlgorithm(InsertionSort sorter, String algorithm, int[] data) {
        try {
            switch (algorithm) {
                case "optimized":
                    sorter.sort(data);
                    return true;
                case "binary":
                    sorter.binaryInsertionSort(data);
                    return true;
                case "adaptive":
                    sorter.adaptiveSort(data);
                    return true;
                default:
                    System.out.println("Unknown algorithm: " + algorithm);
                    return false;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    private static int[] generateData(int size, String type) {
        Random random = new Random(42);
        int[] data = new int[size];

        switch (type) {
            case "sorted":
                for (int i = 0; i < size; i++) data[i] = i;
                break;
            case "reverse":
                for (int i = 0; i < size; i++) data[i] = size - i;
                break;
            case "random":
                for (int i = 0; i < size; i++) data[i] = random.nextInt(size * 2) - size;
                break;
            case "nearly-sorted":
                for (int i = 0; i < size; i++) data[i] = i;
                // Introduce 10% randomness
                int swaps = size / 10;
                for (int i = 0; i < swaps; i++) {
                    int idx1 = random.nextInt(size);
                    int idx2 = random.nextInt(size);
                    int temp = data[idx1];
                    data[idx1] = data[idx2];
                    data[idx2] = temp;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown data type: " + type);
        }
        return data;
    }

    private static int[] generateRandomArray(int size, Random random) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = random.nextInt(size * 2) - size;
        }
        return arr;
    }

    private static boolean isSorted(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1]) {
                return false;
            }
        }
        return true;
    }

    private static void printUsage() {
        System.out.println("Insertion Sort Testing Framework");
        System.out.println("Commands:");
        System.out.println("  test       - Run correctness tests (edge cases)");
        System.out.println("  benchmark  - Run performance benchmarks");
        System.out.println("  validate   - Cross-validate with Java's Arrays.sort()");
        System.out.println("  single <size> <data-type> [algorithm] - Run single test");
        System.out.println();
        System.out.println("Data types: random, sorted, reverse, nearly-sorted");
        System.out.println("Algorithms: optimized, binary, adaptive");
    }
}