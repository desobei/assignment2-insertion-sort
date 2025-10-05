package com.algorithms.algorithms;

import metrics.PerformanceTracker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InsertionSort - meets all testing requirements
 */
class InsertionSortTest {
    private PerformanceTracker tracker;

    @BeforeEach
    void setUp() {
        tracker = new PerformanceTracker();
    }

    // ===== CORRECTNESS TESTS - Edge cases =====

    @Test
    void testSort_NullArray_ThrowsException() {
        InsertionSort sorter = new InsertionSort(tracker);
        assertThrows(IllegalArgumentException.class, () -> sorter.sort(null));
    }

    @Test
    void testSort_EmptyArray_NoChanges() {
        int[] arr = {};
        InsertionSort sorter = new InsertionSort(tracker);
        sorter.sort(arr);
        assertEquals(0, arr.length);
    }

    @Test
    void testSort_SingleElement_NoChanges() {
        int[] arr = {5};
        InsertionSort sorter = new InsertionSort(tracker);
        sorter.sort(arr);
        assertArrayEquals(new int[]{5}, arr);
    }

    @Test
    void testSort_AlreadySorted_Preserved() {
        int[] arr = {1, 2, 3, 4, 5};
        InsertionSort sorter = new InsertionSort(tracker);
        sorter.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    void testSort_ReverseSorted_CorrectlySorted() {
        int[] arr = {5, 4, 3, 2, 1};
        InsertionSort sorter = new InsertionSort(tracker);
        sorter.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    void testSort_WithDuplicates_CorrectlySorted() {
        int[] arr = {3, 1, 4, 1, 5, 9, 2, 6, 5};
        InsertionSort sorter = new InsertionSort(tracker);
        sorter.sort(arr);
        assertArrayEquals(new int[]{1, 1, 2, 3, 4, 5, 5, 6, 9}, arr);
    }

    @Test
    void testSort_AllDuplicates_CorrectlySorted() {
        int[] arr = {5, 5, 5, 5, 5};
        InsertionSort sorter = new InsertionSort(tracker);
        sorter.sort(arr);
        assertArrayEquals(new int[]{5, 5, 5, 5, 5}, arr);
    }

    @Test
    void testSort_NegativeNumbers_CorrectlySorted() {
        int[] arr = {-3, -1, -7, -2, -5};
        InsertionSort sorter = new InsertionSort(tracker);
        sorter.sort(arr);
        assertArrayEquals(new int[]{-7, -5, -3, -2, -1}, arr);
    }

    @Test
    void testSort_MixedPositiveNegative_CorrectlySorted() {
        int[] arr = {3, -1, 0, -5, 2};
        InsertionSort sorter = new InsertionSort(tracker);
        sorter.sort(arr);
        assertArrayEquals(new int[]{-5, -1, 0, 2, 3}, arr);
    }

    @Test
    void testSort_NearlySorted_CorrectlySorted() {
        int[] arr = {1, 2, 4, 3, 5, 6};
        InsertionSort sorter = new InsertionSort(tracker);
        sorter.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5, 6}, arr);
    }

    // ===== CROSS-VALIDATION TESTS =====

    @Test
    void testSort_CrossValidationWithJavaSort_SmallArray() {
        int[] arr = {5, 2, 8, 1, 9, 3, 7, 4, 6};
        int[] javaSorted = Arrays.copyOf(arr, arr.length);
        int[] ourSorted = Arrays.copyOf(arr, arr.length);

        Arrays.sort(javaSorted);
        InsertionSort sorter = new InsertionSort(tracker);
        sorter.sort(ourSorted);

        assertArrayEquals(javaSorted, ourSorted);
    }

    @Test
    void testSort_CrossValidationWithJavaSort_MediumArray() {
        int[] arr = generateRandomArray(100, 42);
        int[] javaSorted = Arrays.copyOf(arr, arr.length);
        int[] ourSorted = Arrays.copyOf(arr, arr.length);

        Arrays.sort(javaSorted);
        InsertionSort sorter = new InsertionSort(tracker);
        sorter.sort(ourSorted);

        assertArrayEquals(javaSorted, ourSorted);
    }

    // ===== PROPERTY-BASED TESTS =====

    @RepeatedTest(20)
    void testSort_PropertyBased_RandomArrays() {
        Random random = new Random();
        int size = random.nextInt(500) + 100;
        int[] arr = generateRandomArray(size, random.nextInt());
        int[] copy = Arrays.copyOf(arr, arr.length);

        InsertionSort sorter = new InsertionSort(tracker);
        sorter.sort(arr);
        Arrays.sort(copy);

        assertArrayEquals(copy, arr, "Should sort random array of size " + size);
    }

    @RepeatedTest(10)
    void testBinaryInsertionSort_PropertyBased_RandomArrays() {
        Random random = new Random();
        int size = random.nextInt(300) + 50;
        int[] arr = generateRandomArray(size, random.nextInt());
        int[] copy = Arrays.copyOf(arr, arr.length);

        InsertionSort sorter = new InsertionSort(tracker);
        sorter.binaryInsertionSort(arr);
        Arrays.sort(copy);

        assertArrayEquals(copy, arr, "Binary insertion should sort random array of size " + size);
    }

    @RepeatedTest(10)
    void testAdaptiveSort_PropertyBased_RandomArrays() {
        Random random = new Random();
        int size = random.nextInt(300) + 50;
        int[] arr = generateRandomArray(size, random.nextInt());
        int[] copy = Arrays.copyOf(arr, arr.length);

        InsertionSort sorter = new InsertionSort(tracker);
        sorter.adaptiveSort(arr);
        Arrays.sort(copy);

        assertArrayEquals(copy, arr, "Adaptive sort should sort random array of size " + size);
    }

    // ===== PERFORMANCE METRIC TESTS =====

    @Test
    void testPerformanceMetrics_AreRecorded() {
        int[] arr = {3, 1, 2};
        InsertionSort sorter = new InsertionSort(tracker);
        sorter.sort(arr);

        assertTrue(tracker.getComparisons() > 0);
        assertTrue(tracker.getSwaps() > 0);
        assertTrue(tracker.getArrayAccesses() > 0);
        assertTrue(tracker.getElapsedTimeNanos() > 0);
    }

    @Test
    void testPerformanceTracker_Reset() {
        tracker.incrementComparisons(10);
        tracker.incrementSwaps(5);
        tracker.reset();

        assertEquals(0, tracker.getComparisons());
        assertEquals(0, tracker.getSwaps());
    }

    private int[] generateRandomArray(int size, int seed) {
        Random random = new Random(seed);
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = random.nextInt(1000);
        }
        return arr;
    }
}