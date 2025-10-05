package algorithms;

import metrics.PerformanceTracker;

/**
 * Optimized Insertion Sort implementation with special handling for nearly-sorted data.
 *
 * Time Complexity:
 * - Best Case: O(n) - when array is already sorted
 * - Average Case: O(n²) - random permutation
 * - Worst Case: O(n²) - reverse sorted array
 *
 * Space Complexity: O(1) - sorts in-place
 *
 * Optimizations:
 * 1. Binary search for insertion position in sorted portion (reduces comparisons)
 * 2. Early termination when element is already in correct position
 * 3. Adaptive behavior for nearly-sorted data
 */
public class InsertionSort {

    private PerformanceTracker tracker;

    public InsertionSort() {
        this.tracker = new PerformanceTracker();
    }

    public InsertionSort(PerformanceTracker tracker) {
        this.tracker = tracker;
    }

    /**
     * Sorts an array of integers in ascending order using insertion sort.
     *
     * @param arr the array to sort
     * @throws IllegalArgumentException if array is null
     */
    public void sort(int[] arr) {
        if (arr == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }

        if (arr.length <= 1) {
            return; // Already sorted
        }

        standardInsertionSort(arr);
    }

    /**
     * Standard insertion sort with optimizations for nearly-sorted data.
     * Uses early termination when element is already in correct position.
     */
    private void standardInsertionSort(int[] arr) {
        int n = arr.length;

        for (int i = 1; i < n; i++) {
            int key = arr[i];
            tracker.recordArrayAccess();

            // Optimization: Check if element is already in correct position
            tracker.recordComparison();
            tracker.recordArrayAccess();
            if (key >= arr[i - 1]) {
                continue; // Already in correct position
            }

            // Find insertion position and shift elements
            int j = i - 1;

            // Shift elements that are greater than key
            while (j >= 0 && arr[j] > key) {
                tracker.recordComparison();
                tracker.recordArrayAccess();

                arr[j + 1] = arr[j];
                tracker.recordArrayAccess();
                tracker.recordSwap(); // Count as swap (element movement)
                j--;
            }

            // Record final comparison if we exited due to arr[j] <= key
            if (j >= 0) {
                tracker.recordComparison();
                tracker.recordArrayAccess();
            }

            // Insert the key at correct position
            arr[j + 1] = key;
            tracker.recordArrayAccess();
        }
    }

    /**
     * Insertion sort using binary search to find insertion position.
     * Reduces number of comparisons but not swaps/shifts.
     *
     * @param arr the array to sort
     */
    public void sortWithBinarySearch(int[] arr) {
        if (arr == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }

        if (arr.length <= 1) {
            return;
        }

        int n = arr.length;

        for (int i = 1; i < n; i++) {
            int key = arr[i];
            tracker.recordArrayAccess();

            // Find insertion position using binary search
            int insertPos = binarySearchInsertionPoint(arr, key, 0, i - 1);

            // Shift elements to make room
            for (int j = i - 1; j >= insertPos; j--) {
                arr[j + 1] = arr[j];
                tracker.recordArrayAccess();
                tracker.recordArrayAccess();
                tracker.recordSwap();
            }

            // Insert key at found position
            arr[insertPos] = key;
            tracker.recordArrayAccess();
        }
    }

    /**
     * Binary search to find the correct insertion position.
     * Returns the index where the key should be inserted.
     */
    private int binarySearchInsertionPoint(int[] arr, int key, int left, int right) {
        while (left <= right) {
            int mid = left + (right - left) / 2;
            tracker.recordArrayAccess();
            tracker.recordComparison();

            if (arr[mid] == key) {
                return mid + 1;
            } else if (arr[mid] < key) {
                tracker.recordComparison();
                left = mid + 1;
            } else {
                tracker.recordComparison();
                right = mid - 1;
            }
        }
        return left;
    }

    /**
     * Sorts with detailed metrics tracking enabled.
     *
     * @param arr the array to sort
     * @param useBinarySearch whether to use binary search optimization
     * @return the performance tracker with collected metrics
     */
    public PerformanceTracker sortWithMetrics(int[] arr, boolean useBinarySearch) {
        tracker.reset();
        tracker.startTiming();

        if (useBinarySearch) {
            sortWithBinarySearch(arr);
        } else {
            sort(arr);
        }

        tracker.stopTiming();
        return tracker;
    }

    /**
     * Checks if an array is sorted in ascending order.
     *
     * @param arr the array to check
     * @return true if sorted, false otherwise
     */
    public static boolean isSorted(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return true;
        }

        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < arr[i - 1]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a copy of the array and sorts it.
     *
     * @param arr the array to sort
     * @return a new sorted array
     */
    public int[] sortCopy(int[] arr) {
        if (arr == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }

        int[] copy = arr.clone();
        sort(copy);
        return copy;
    }

    public PerformanceTracker getTracker() {
        return tracker;
    }

    public void resetTracker() {
        tracker.reset();
    }
}