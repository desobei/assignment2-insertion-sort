package algorithms;

import metrics.PerformanceTracker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for InsertionSort implementation.
 * Tests correctness, edge cases, and performance characteristics.
 */
class InsertionSortTest {

    private InsertionSort sorter;
    private PerformanceTracker tracker;

    @BeforeEach
    void setUp() {
        tracker = new PerformanceTracker();
        sorter = new InsertionSort(tracker);
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("Test null array throws exception")
    void testNullArray() {
        assertThrows(IllegalArgumentException.class, () -> sorter.sort(null));
    }

    @Test
    @DisplayName("Test empty array")
    void testEmptyArray() {
        int[] arr = {};
        sorter.sort(arr);
        assertEquals(0, arr.length);
        assertTrue(InsertionSort.isSorted(arr));
    }

    @Test
    @DisplayName("Test single element array")
    void testSingleElement() {
        int[] arr = {42};
        sorter.sort(arr);
        assertArrayEquals(new int[]{42}, arr);
        assertTrue(InsertionSort.isSorted(arr));
    }

    @Test
    @DisplayName("Test two elements in order")
    void testTwoElementsInOrder() {
        int[] arr = {1, 2};
        sorter.sort(arr);
        assertArrayEquals(new int[]{1, 2}, arr);
        assertTrue(InsertionSort.isSorted(arr));
    }

    @Test
    @DisplayName("Test two elements out of order")
    void testTwoElementsOutOfOrder() {
        int[] arr = {2, 1};
        sorter.sort(arr);
        assertArrayEquals(new int[]{1, 2}, arr);
        assertTrue(InsertionSort.isSorted(arr));
    }

    // ==================== Basic Sorting ====================

    @Test
    @DisplayName("Test already sorted array")
    void testAlreadySorted() {
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] expected = arr.clone();

        tracker.reset();
        tracker.startTiming();
        sorter.sort(arr);
        tracker.stopTiming();

        assertArrayEquals(expected, arr);
        assertTrue(InsertionSort.isSorted(arr));

        // Best case: O(n) comparisons, minimal operations
        assertTrue(tracker.getComparisons() <= arr.length * 2,
                "Best case should have O(n) comparisons");
    }

    @Test
    @DisplayName("Test reverse sorted array")
    void testReverseSorted() {
        int[] arr = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        int[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        sorter.sort(arr);

        assertArrayEquals(expected, arr);
        assertTrue(InsertionSort.isSorted(arr));
    }

    @Test
    @DisplayName("Test random array")
    void testRandomArray() {
        int[] arr = {5, 2, 8, 1, 9, 3, 7, 4, 6};
        int[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9};

        sorter.sort(arr);

        assertArrayEquals(expected, arr);
        assertTrue(InsertionSort.isSorted(arr));
    }

    // ==================== Duplicates ====================

    @Test
    @DisplayName("Test array with all duplicate elements")
    void testAllDuplicates() {
        int[] arr = {5, 5, 5, 5, 5};
        int[] expected = {5, 5, 5, 5, 5};

        sorter.sort(arr);

        assertArrayEquals(expected, arr);
        assertTrue(InsertionSort.isSorted(arr));
    }

    @Test
    @DisplayName("Test array with some duplicates")
    void testSomeDuplicates() {
        int[] arr = {3, 1, 4, 1, 5, 9, 2, 6, 5, 3};
        sorter.sort(arr);

        assertTrue(InsertionSort.isSorted(arr));

        // Verify all elements are present
        int[] sortedExpected = {1, 1, 2, 3, 3, 4, 5, 5, 6, 9};
        assertArrayEquals(sortedExpected, arr);
    }

    @Test
    @DisplayName("Test array with adjacent duplicates")
    void testAdjacentDuplicates() {
        int[] arr = {1, 2, 2, 3, 3, 3, 4, 4, 4, 4};
        int[] expected = arr.clone();

        sorter.sort(arr);

        assertArrayEquals(expected, arr);
        assertTrue(InsertionSort.isSorted(arr));
    }

    // ==================== Special Values ====================

    @Test
    @DisplayName("Test array with negative numbers")
    void testNegativeNumbers() {
        int[] arr = {-5, 3, -2, 8, -9, 1, 0};
        int[] expected = {-9, -5, -2, 0, 1, 3, 8};

        sorter.sort(arr);

        assertArrayEquals(expected, arr);
        assertTrue(InsertionSort.isSorted(arr));
    }

    @Test
    @DisplayName("Test array with zeros")
    void testWithZeros() {
        int[] arr = {0, 5, 0, -3, 0, 2};
        int[] expected = {-3, 0, 0, 0, 2, 5};

        sorter.sort(arr);

        assertArrayEquals(expected, arr);
        assertTrue(InsertionSort.isSorted(arr));
    }

    @Test
    @DisplayName("Test array with Integer.MAX_VALUE and Integer.MIN_VALUE")
    void testExtremeValues() {
        int[] arr = {Integer.MAX_VALUE, 0, Integer.MIN_VALUE, 100, -100};

        sorter.sort(arr);

        assertTrue(InsertionSort.isSorted(arr));
        assertEquals(Integer.MIN_VALUE, arr[0]);
        assertEquals(Integer.MAX_VALUE, arr[arr.length - 1]);
    }

    // ==================== Nearly Sorted Data ====================

    @Test
    @DisplayName("Test nearly sorted array (one element out of place)")
    void testNearlySortedOneElement() {
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 9, 8, 10};
        int[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        tracker.reset();
        tracker.startTiming();
        sorter.sort(arr);
        tracker.stopTiming();

        assertArrayEquals(expected, arr);
        assertTrue(InsertionSort.isSorted(arr));

        // Should be efficient for nearly sorted data
        System.out.println("Nearly sorted (1 element): " + tracker.getMetricsSummary());
    }

    @Test
    @DisplayName("Test nearly sorted array (few swaps needed)")
    void testNearlySortedFewSwaps() {
        int[] arr = {1, 3, 2, 5, 4, 7, 6, 9, 8, 10};

        tracker.reset();
        sorter.sort(arr);

        assertTrue(InsertionSort.isSorted(arr));
        System.out.println("Nearly sorted (few swaps): " + tracker.getMetricsSummary());
    }

    // ==================== Property-Based Testing ====================

    @ParameterizedTest
    @MethodSource("randomArrayProvider")
    @DisplayName("Property test: sorting random arrays maintains all elements")
    void testSortingPreservesElements(int[] arr) {
        int[] original = arr.clone();
        Arrays.sort(original); // Java's sort for comparison

        sorter.sort(arr);

        assertArrayEquals(original, arr);
        assertTrue(InsertionSort.isSorted(arr));
    }

    static Stream<int[]> randomArrayProvider() {
        Random rand = new Random(42); // Fixed seed for reproducibility
        return Stream.generate(() -> {
            int size = rand.nextInt(50) + 10; // Arrays of size 10-60
            int[] arr = new int[size];
            for (int i = 0; i < size; i++) {
                arr[i] = rand.nextInt(200) - 100; // Values from -100 to 99
            }
            return arr;
        }).limit(20); // Test 20 random arrays
    }

    // ==================== Performance Characteristics ====================

    @Test
    @DisplayName("Verify best case O(n) for sorted array")
    void testBestCaseComplexity() {
        int[] sizes = {100, 200, 400, 800};
        long[] comparisons = new long[sizes.length];

        for (int i = 0; i < sizes.length; i++) {
            int[] arr = new int[sizes[i]];
            for (int j = 0; j < sizes[i]; j++) {
                arr[j] = j; // Already sorted
            }

            tracker.reset();
            sorter.sort(arr);
            comparisons[i] = tracker.getComparisons();
        }

        // Best case should be linear: doubling size should roughly double comparisons
        for (int i = 1; i < sizes.length; i++) {
            double ratio = (double) comparisons[i] / comparisons[i - 1];
            assertTrue(ratio < 2.5,
                    String.format("Best case not O(n): ratio %.2f at size %d (comp: %d vs %d)",
                            ratio, sizes[i], comparisons[i], comparisons[i-1]));
        }
    }

    @Test
    @DisplayName("Verify worst case O(n²) for reverse sorted array")
    void testWorstCaseComplexity() {
        int[] sizes = {50, 100, 200};
        long[] comparisons = new long[sizes.length];

        for (int i = 0; i < sizes.length; i++) {
            int[] arr = new int[sizes[i]];
            for (int j = 0; j < sizes[i]; j++) {
                arr[j] = sizes[i] - j; // Reverse sorted
            }

            tracker.reset();
            sorter.sort(arr);
            comparisons[i] = tracker.getComparisons();
        }

        // Worst case should be quadratic: doubling size should ~4x comparisons
        for (int i = 1; i < sizes.length; i++) {
            double ratio = (double) comparisons[i] / comparisons[i - 1];
            assertTrue(ratio > 3.5 && ratio < 4.5,
                    String.format("Worst case not O(n²): ratio %.2f at size %d (comp: %d vs %d)",
                            ratio, sizes[i], comparisons[i], comparisons[i-1]));
        }
    }

    // ==================== Binary Search Variant ====================

    @Test
    @DisplayName("Test binary search variant correctness")
    void testBinarySearchVariant() {
        int[] arr = {5, 2, 8, 1, 9, 3, 7, 4, 6};
        int[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9};

        sorter.sortWithBinarySearch(arr);

        assertArrayEquals(expected, arr);
        assertTrue(InsertionSort.isSorted(arr));
    }

    @Test
    @DisplayName("Compare standard vs binary search comparisons")
    void testBinarySearchReducesComparisons() {
        // Use larger array for more pronounced difference
        int[] arr1 = new int[50];
        Random rand = new Random(123);
        for (int i = 0; i < arr1.length; i++) {
            arr1[i] = rand.nextInt(100);
        }
        int[] arr2 = arr1.clone();

        PerformanceTracker tracker1 = new PerformanceTracker();
        PerformanceTracker tracker2 = new PerformanceTracker();

        InsertionSort sorter1 = new InsertionSort(tracker1);
        InsertionSort sorter2 = new InsertionSort(tracker2);

        sorter1.sortWithMetrics(arr1, false); // Standard
        sorter2.sortWithMetrics(arr2, true);  // Binary search

        System.out.printf("Standard: %d comparisons%n", tracker1.getComparisons());
        System.out.printf("Binary Search: %d comparisons%n", tracker2.getComparisons());

        assertArrayEquals(arr1, arr2);
        // Binary search should have fewer comparisons for larger arrays
        assertTrue(tracker2.getComparisons() <= tracker1.getComparisons(),
                String.format("Binary search comparisons (%d) should be <= standard (%d)",
                        tracker2.getComparisons(), tracker1.getComparisons()));
    }

    // ==================== Utility Methods ====================

    @Test
    @DisplayName("Test sortCopy creates new sorted array")
    void testSortCopy() {
        int[] arr = {5, 2, 8, 1, 9};
        int[] original = arr.clone();

        int[] sorted = sorter.sortCopy(arr);

        assertArrayEquals(original, arr, "Original should be unchanged");
        assertTrue(InsertionSort.isSorted(sorted));
        assertNotSame(arr, sorted, "Should create new array");
    }

    @Test
    @DisplayName("Test isSorted utility method")
    void testIsSortedUtility() {
        assertTrue(InsertionSort.isSorted(new int[]{}));
        assertTrue(InsertionSort.isSorted(new int[]{1}));
        assertTrue(InsertionSort.isSorted(new int[]{1, 2, 3}));
        assertFalse(InsertionSort.isSorted(new int[]{3, 2, 1}));
        assertTrue(InsertionSort.isSorted(new int[]{1, 1, 1}));
        assertFalse(InsertionSort.isSorted(new int[]{1, 3, 2}));
    }

    // ==================== Metrics Tracking ====================

    @Test
    @DisplayName("Test metrics are tracked correctly")
    void testMetricsTracking() {
        int[] arr = {3, 1, 2};

        tracker.reset();
        tracker.startTiming();
        sorter.sort(arr);
        tracker.stopTiming();

        assertTrue(tracker.getComparisons() > 0, "Should record comparisons");
        assertTrue(tracker.getSwaps() > 0, "Should record swaps");
        assertTrue(tracker.getArrayAccesses() > 0, "Should record array accesses");
        assertTrue(tracker.getElapsedTimeNanos() > 0, "Should record time");
    }

    @Test
    @DisplayName("Test tracker reset")
    void testTrackerReset() {
        int[] arr = {3, 1, 2};
        sorter.sort(arr);

        tracker.reset();

        assertEquals(0, tracker.getComparisons());
        assertEquals(0, tracker.getSwaps());
        assertEquals(0, tracker.getArrayAccesses());
    }
}