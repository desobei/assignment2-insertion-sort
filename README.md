# Insertion Sort Implementation - Assignment 2

**Student**: Abylaikhan  
**Algorithm**: Insertion Sort with optimizations for nearly-sorted data  
**Pair Partner**: Bauyrzhan

## Overview

This project implements an optimized Insertion Sort algorithm with comprehensive performance tracking and analysis capabilities. The implementation includes special handling for nearly-sorted data and provides detailed metrics for algorithmic analysis.

## Algorithm Overview

### Insertion Sort - Detailed Description

Insertion Sort is a simple comparison-based sorting algorithm that builds the final sorted array one element at a time. It works by iterating through an input array and for each element, finding its correct position in the already-sorted portion of the array by shifting larger elements to the right.

**Algorithm Steps:**
1. Start from the second element (index 1) as the first element is trivially sorted
2. For each element (key), compare it with elements in the sorted portion (to its left)
3. Shift all elements greater than the key one position to the right
4. Insert the key at its correct position
5. Repeat until all elements are processed

**Why Insertion Sort?**
- Efficient for small datasets (n < 50)
- Extremely efficient for nearly-sorted data (adaptive algorithm)
- Stable sort (maintains relative order of equal elements)
- In-place algorithm (requires O(1) auxiliary space)
- Simple implementation with low overhead

---

## Complexity Analysis

### Time Complexity - Detailed Derivation

#### **Best Case: Θ(n)**
- **Condition**: Array is already sorted in ascending order
- **Analysis**:
    - For each element from index 1 to n-1, we perform exactly one comparison with the previous element
    - Since arr[i] ≥ arr[i-1], the early termination optimization kicks in
    - No shifting operations are needed
    - Total comparisons: `C(n) = n - 1 = Θ(n)`
    - Total swaps: `S(n) = 0`
- **Mathematical Proof**:
  ```
  T(n) = Σ(i=1 to n-1) [1 comparison] = n - 1 = Θ(n)
  ```

#### **Worst Case: Θ(n²)**
- **Condition**: Array is sorted in reverse order (descending)
- **Analysis**:
    - For element at index i, we must compare it with all i elements in the sorted portion
    - Each element must be shifted i positions
    - At position i: (i+1) comparisons and i shifts
    - Total comparisons: `C(n) = Σ(i=1 to n-1) (i+1) = n(n+1)/2 - 1 ≈ n²/2`
    - Total shifts: `S(n) = Σ(i=1 to n-1) i = n(n-1)/2 ≈ n²/2`
- **Mathematical Proof**:
  ```
  T(n) = Σ(i=1 to n-1) (i+1) 
       = Σ(i=1 to n-1) i + Σ(i=1 to n-1) 1
       = (n-1)n/2 + (n-1)
       = (n² - n)/2 + (n-1)
       = (n² + n - 2)/2
       = Θ(n²)
  ```

#### **Average Case: Θ(n²)**
- **Condition**: Random permutation of elements
- **Analysis**:
    - On average, each element is compared with half of the sorted portion
    - Expected position for insertion: middle of sorted portion
    - Average comparisons per element: i/2
    - Total comparisons: `C(n) ≈ n²/4`
    - Total shifts: `S(n) ≈ n²/4`
- **Mathematical Proof**:
  ```
  T(n) = Σ(i=1 to n-1) (i/2)
       = (1/2) × Σ(i=1 to n-1) i
       = (1/2) × (n-1)n/2
       = n(n-1)/4
       = Θ(n²)
  ```

### Space Complexity - Detailed Analysis

#### **Auxiliary Space: Θ(1)**
- **Variables Used**:
    - `i` - loop counter (4 bytes)
    - `j` - inner loop counter (4 bytes)
    - `key` - temporary storage for current element (4 bytes)
- **Total**: 12 bytes regardless of input size
- **In-place**: Sorting is performed directly on the input array without additional arrays
- **No recursion**: Iterative implementation requires no stack space

#### **Total Space: Θ(n)**
- Input array storage: n elements
- Auxiliary variables: Θ(1)
- **Total**: Θ(n) dominated by input array

---

## Optimizations Implemented

### 1. **Early Termination for Nearly-Sorted Data**
```java
if (key >= arr[i - 1]) {
    continue; // Already in correct position
}
```
- **Impact**: Reduces best case from Θ(n²) to Θ(n)
- **Benefit**: For nearly-sorted arrays, most elements skip the inner loop
- **Example**: [1,2,3,5,4] - only element 4 needs repositioning

### 2. **Binary Search Variant (Optional)**
```java
int insertPos = binarySearchInsertionPoint(arr, key, 0, i - 1);
```
- **Impact**: Reduces comparisons from O(n) to O(log n) per element
- **Overall Complexity**: O(n log n) comparisons, but still O(n²) shifts
- **Trade-off**: Better for comparison-heavy operations, same shifts required
- **Use Case**: When comparisons are expensive (e.g., string comparison)

### 3. **Adaptive Behavior**
- Algorithm naturally adapts to input characteristics
- **Nearly-sorted**: Approaches O(n) performance
- **Few inversions**: Performance proportional to number of inversions
- **Partially sorted**: Efficient on data with sorted subsequences

### 4. **Minimal Overhead**
- No recursive calls (no stack overhead)
- No auxiliary data structures
- Cache-friendly (sequential access pattern)
- Low constant factors

---

## Big-O, Big-Θ, Big-Ω Notation Summary

### Formal Definitions Applied to Insertion Sort

| Case     | Big-O (Upper Bound) | Big-Θ (Tight Bound) | Big-Ω (Lower Bound) |
|----------|---------------------|---------------------|---------------------|
| **Best**     | O(n)                | Θ(n)                | Ω(n)                |
| **Average**  | O(n²)               | Θ(n²)               | Ω(n²)               |
| **Worst**    | O(n²)               | Θ(n²)               | Ω(n²)               |

### Interpretation:
- **O(n²)**: Insertion sort will never exceed quadratic time
- **Θ(n)**: Best case is tightly bound by linear time (neither faster nor slower)
- **Ω(n)**: Insertion sort requires at least linear time (must examine each element)

---

## Comparison with Partner's Algorithm (Selection Sort)

| Metric                  | Insertion Sort          | Selection Sort          |
|-------------------------|-------------------------|-------------------------|
| **Best Case Time**      | Θ(n)                    | Θ(n²)                   |
| **Worst Case Time**     | Θ(n²)                   | Θ(n²)                   |
| **Average Case Time**   | Θ(n²)                   | Θ(n²)                   |
| **Space Complexity**    | Θ(1)                    | Θ(1)                    |
| **Comparisons (Best)**  | n-1                     | n(n-1)/2                |
| **Comparisons (Worst)** | n(n+1)/2                | n(n-1)/2                |
| **Swaps (Best)**        | 0                       | 0 or n-1                |
| **Swaps (Worst)**       | n(n-1)/2                | n-1                     |
| **Stability**           | Stable                  | Unstable                |
| **Adaptivity**          | Adaptive                | Non-adaptive            |
| **Online**              | Yes                     | No                      |
| **Best Use Case**       | Nearly-sorted data      | Minimizing swaps        |

### Key Differences:
1. **Adaptivity**: Insertion sort adapts to input (fast on nearly-sorted), Selection sort always Θ(n²)
2. **Stability**: Insertion sort preserves order of equal elements, Selection sort does not
3. **Comparisons vs Swaps**: Insertion sort minimizes comparisons on sorted data; Selection sort minimizes swaps
4. **Online Processing**: Insertion sort can process elements as they arrive; Selection sort needs full array

---

## Empirical Validation Plan

### Benchmark Configuration
- **Input Sizes**: 100, 1,000, 10,000, 100,000
- **Input Types**: Random, Sorted, Reverse, Nearly-Sorted, Few-Unique
- **Iterations**: 5 runs per configuration (after 3 warmup runs)
- **Metrics Collected**: Comparisons, Swaps, Array Accesses, Time (nanoseconds)

### Expected Results

#### Time vs Input Size (Sorted Data - Best Case)
- **Prediction**: Linear relationship (Θ(n))
- **Expected Plot**: Straight line through origin
- **Validation**: Doubling n should double time

#### Time vs Input Size (Reverse Data - Worst Case)
- **Prediction**: Quadratic relationship (Θ(n²))
- **Expected Plot**: Parabolic curve
- **Validation**: Doubling n should quadruple time

#### Comparisons vs Input Size
| Input Size | Sorted (Best) | Random (Avg) | Reverse (Worst) |
|------------|---------------|--------------|-----------------|
| 100        | ~99           | ~2,500       | ~5,050          |
| 1,000      | ~999          | ~250,000     | ~500,500        |
| 10,000     | ~9,999        | ~25,000,000  | ~50,005,000     |

### Performance Plots to Generate
1. **Time vs Input Size** (log-log scale) - for all input types
2. **Comparisons vs Input Size** - verify O(n) and O(n²) bounds
3. **Comparisons: Insertion Sort vs Selection Sort** - highlight adaptivity
4. **Nearly-Sorted Performance** - demonstrate optimization effectiveness

---

## Theoretical Background

### Inversions and Insertion Sort
An **inversion** is a pair of indices (i, j) where i < j but arr[i] > arr[j].

**Theorem**: Insertion sort's running time is Θ(n + I) where I is the number of inversions.
- **Proof**: Each swap fixes exactly one inversion
- **Best case**: I = 0 (sorted array) → Θ(n)
- **Worst case**: I = n(n-1)/2 (reverse sorted) → Θ(n²)

### Why Θ(n²) in Average Case?
For a random permutation:
- Expected number of inversions: E[I] = n(n-1)/4
- Each element, on average, is compared with half of the sorted portion
- Total work: Θ(n + n²/4) = Θ(n²)

### Lower Bound for Comparison-Based Sorting
- **Decision Tree Model**: Any comparison-based sort requires Ω(n log n) comparisons in the worst case
- **Why Insertion Sort is Θ(n²)**: It doesn't achieve information-theoretic optimal O(n log n) because it only compares adjacent elements (or nearby elements)
- **Binary Search Variant**: Achieves O(n log n) comparisons but still O(n²) time due to shifts

---

## Practical Considerations

### When to Use Insertion Sort
✅ **Good for:**
- Small arrays (n < 10-50)
- Nearly-sorted data
- Online algorithms (processing streaming data)
- As a subroutine in hybrid algorithms (Timsort, Introsort)
- When stability is required
- When auxiliary space is limited

❌ **Avoid for:**
- Large datasets (n > 1000)
- Random or reverse-sorted data
- When average-case performance is critical

### Constant Factors
Despite being O(n²), insertion sort has:
- Low constant factors (simple operations)
- Good cache locality (sequential access)
- Small code footprint
- Often faster than O(n log n) algorithms for small n (n < 50)

---

## References

1. Cormen, T. H., Leiserson, C. E., Rivest, R. L., & Stein, C. (2009). *Introduction to Algorithms* (3rd ed.). MIT Press. Chapter 2.1: Insertion Sort.

2. Knuth, D. E. (1998). *The Art of Computer Programming, Volume 3: Sorting and Searching* (2nd ed.). Addison-Wesley. Section 5.2.1: Sorting by Insertion.

3. Sedgewick, R., & Wayne, K. (2011). *Algorithms* (4th ed.). Addison-Wesley. Section 2.1: Elementary Sorts.

4. Wikipedia contributors. (2024). Insertion sort. In *Wikipedia, The Free Encyclopedia*.

## Project Structure

```
insertion-sort/
├── src/
│   ├── main/java/
│   │   ├── algorithms/
│   │   │   └── InsertionSort.java          # Main algorithm implementation
│   │   ├── metrics/
│   │   │   └── PerformanceTracker.java     # Performance metrics collection
│   │   └── cli/
│   │       └── BenchmarkRunner.java        # CLI for benchmarking
│   └── test/java/
│       └── algorithms/
│           └── InsertionSortTest.java      # Comprehensive test suite
├── docs/
│   ├── analysis-report.pdf                 # Peer analysis report 
│   └── performance-plots/                  # Performance visualization 
├── pom.xml                                  # Maven configuration
└── README.md                                # This file
```

## Building the Project

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Compile
```bash
mvn clean compile
```

### Run Tests
```bash
mvn test
```

### Package
```bash
mvn package
```

## Running Benchmarks

### Using Maven
```bash
# Run with default sizes (100, 1000, 10000, 100000)
mvn exec:java

# Run with custom sizes
mvn exec:java -Dexec.args="500 5000 50000"
```

### Using Java directly
```bash
# Compile first
mvn compile

# Run with default sizes
java -cp target/classes cli.BenchmarkRunner

# Run with custom sizes
java -cp target/classes cli.BenchmarkRunner 100 1000 10000

# Show help
java -cp target/classes cli.BenchmarkRunner --help
```

### Benchmark Output

The benchmark runner tests the algorithm on various input distributions:
- **Random**: Randomly distributed values
- **Sorted**: Already sorted (best case)
- **Reverse**: Reverse sorted (worst case)
- **Nearly-Sorted**: 90% sorted with random perturbations
- **Few-Unique**: Many duplicate values

Results are exported to `benchmark_results.csv` containing:
- Input size
- Input type
- Number of comparisons
- Number of swaps
- Array accesses
- Execution time (nanoseconds and milliseconds)

## Testing

The test suite (`InsertionSortTest.java`) includes:

### Edge Cases
- Null arrays
- Empty arrays
- Single element arrays
- Two element arrays

### Basic Sorting
- Already sorted arrays
- Reverse sorted arrays
- Random arrays

### Duplicates
- All duplicate elements
- Some duplicates
- Adjacent duplicates

### Special Values
- Negative numbers
- Arrays with zeros
- Integer.MAX_VALUE and Integer.MIN_VALUE

### Nearly-Sorted Data
- One element out of place
- Few swaps needed
- Validates optimization effectiveness

### Property-Based Testing
- 20 randomly generated test cases
- Verifies all elements preserved
- Cross-validates with Java's Arrays.sort()

### Complexity Verification
- Best case O(n) verification
- Worst case O(n²) verification
- Binary search variant comparison

## Performance Metrics

The `PerformanceTracker` class collects:
- **Comparisons**: Number of element comparisons
- **Swaps**: Number of element movements
- **Array Accesses**: Total array read/write operations
- **Execution Time**: Precise nanosecond timing

## Usage Example

```java
import algorithms.InsertionSort;
import metrics.PerformanceTracker;

public class Example {
    public static void main(String[] args) {
        // Create sorter with tracker
        PerformanceTracker tracker = new PerformanceTracker();
        InsertionSort sorter = new InsertionSort(tracker);
        
        // Sort an array
        int[] arr = {5, 2, 8, 1, 9};
        tracker.startTiming();
        sorter.sort(arr);
        tracker.stopTiming();
        
        // Print results
        System.out.println("Sorted: " + java.util.Arrays.toString(arr));
        System.out.println(tracker.getMetricsSummary());
        
        // Verify correctness
        System.out.println("Is sorted: " + InsertionSort.isSorted(arr));
    }
}
```


## References

- Cormen, T. H., et al. (2009). *Introduction to Algorithms* (3rd ed.). MIT Press.
- Knuth, D. E. (1998). *The Art of Computer Programming, Volume 3: Sorting and Searching* (2nd ed.).

## License

This project is for educational purposes as part of Assignment 2.