package com.algorithms.algorithms;

import com.algorithms.metrics.PerformanceTracker;

/**
 * Insertion Sort with optimizations for nearly-sorted data
 * Meets requirements: Optimizations for nearly-sorted data, Input validation, Metrics collection
 */
public class InsertionSort {
    private PerformanceTracker tracker;

    public InsertionSort(PerformanceTracker tracker) {
        this.tracker = tracker;
    }

    /**
     * Standard Insertion Sort with early termination optimization for nearly-sorted data
     */
    public void sort(int[] arr) {
        // Input validation as required
        if (arr == null) {
            throw new IllegalArgumentException("Input array cannot be null");
        }

        if (arr.length <= 1) {
            return; // Already sorted
        }

        tracker.startTimer();

        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            tracker.incrementArrayAccesses(1);

            int j = i - 1;

            // Optimization for nearly-sorted data: early termination
            tracker.incrementComparisons();
            if (arr[j] <= key) {
                continue; // Skip inner loop if already in correct position
            }

            // Standard insertion sort inner loop
            while (j >= 0 && arr[j] > key) {
                tracker.incrementComparisons();
                arr[j + 1] = arr[j];
                tracker.incrementArrayAccesses(2);
                tracker.incrementSwaps();
                j--;
            }
            arr[j + 1] = key;
            tracker.incrementArrayAccesses(1);
        }

        tracker.stopTimer();
    }

    /**
     * Binary Insertion Sort variant - uses binary search for insertion point
     */
    public void binaryInsertionSort(int[] arr) {
        if (arr == null) {
            throw new IllegalArgumentException("Input array cannot be null");
        }

        if (arr.length <= 1) {
            return;
        }

        tracker.startTimer();

        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            tracker.incrementArrayAccesses(1);

            // Binary search for optimal insertion point (optimization)
            int insertionPoint = binarySearch(arr, 0, i - 1, key);

            // Shift elements
            for (int j = i - 1; j >= insertionPoint; j--) {
                arr[j + 1] = arr[j];
                tracker.incrementArrayAccesses(2);
                tracker.incrementSwaps();
            }
            arr[insertionPoint] = key;
            tracker.incrementArrayAccesses(1);
        }

        tracker.stopTimer();
    }

    /**
     * Adaptive strategy that chooses the best algorithm based on data characteristics
     */
    public void adaptiveSort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }

        // Detect if array is nearly sorted (optimization for nearly-sorted data)
        if (isNearlySorted(arr)) {
            // Use standard sort with early termination for nearly-sorted data
            sort(arr);
        } else {
            // Use binary insertion for random data
            binaryInsertionSort(arr);
        }
    }

    /**
     * Binary search helper method
     */
    private int binarySearch(int[] arr, int left, int right, int key) {
        while (left <= right) {
            int mid = left + (right - left) / 2;
            tracker.incrementArrayAccesses(1);
            tracker.incrementComparisons();

            if (arr[mid] == key) {
                return mid + 1; // Insert after duplicate
            } else if (arr[mid] < key) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return left;
    }

    /**
     * Detects if array is nearly sorted (less than 10% of elements out of order)
     */
    private boolean isNearlySorted(int[] arr) {
        int outOfOrderCount = 0;
        int threshold = arr.length / 10; // 10% threshold

        for (int i = 1; i < arr.length; i++) {
            tracker.incrementComparisons();
            tracker.incrementArrayAccesses(2);
            if (arr[i] < arr[i - 1]) {
                outOfOrderCount++;
                if (outOfOrderCount > threshold) {
                    return false;
                }
            }
        }
        return true;
    }
}