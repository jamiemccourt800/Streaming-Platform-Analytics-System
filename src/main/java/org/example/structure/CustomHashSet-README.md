# CustomHashSet Implementation

**Author:** Jamie McCourt (D00265301)  
**Module:** Algorithms & Data Structures  
**Date:** December 2025

---

## Overview

`CustomHashSet<E>` is a generic Set implementation backed by `CustomHashMap`. It provides essential set operations with guaranteed no duplicate elements, leveraging the HashMap's O(1) average-case performance for all core operations.

### Key Features

- **No Duplicates**: Enforced by HashMap key uniqueness
- **Generic Type Support**: Works with any element type `<E>`
- **HashMap-Backed**: Leverages CustomHashMap for O(1) operations
- **Null Element Support**: Can store null via HashMap's null key handling
- **Iterable**: Implements `Iterable<E>` for foreach loop support
- **Full Set API**: Includes `add()`, `remove()`, `contains()`, `size()`, `isEmpty()`, `clear()`, `iterator()`

---

## Design Pattern: Set as HashMap Wrapper

### The Fundamental Relationship

**Core Insight:** A Set is just "keys without values" or equivalently "a Map where all values are the same."

```java
// Set concept:
{Apple, Banana, Cherry}

// Implementation as HashMap:
{Apple=PRESENT, Banana=PRESENT, Cherry=PRESENT}
```

### Why This Design?

**Benefits:**
1. **Code Reuse** - All hashing logic already exists in CustomHashMap
2. **DRY Principle** - Don't Repeat Yourself (no duplicate hash/collision code)
3. **Single Responsibility** - HashMap handles storage, HashSet provides Set semantics
4. **Maintainability** - Bug fixes in HashMap automatically benefit HashSet
5. **Industry Standard** - Java's `HashSet` uses `HashMap` internally (same pattern!)

---

## Implementation Details

### Internal Structure

**Key Components:**
- **map**: CustomHashMap instance handling all storage
- **PRESENT**: Dummy value object (all keys map to this)
- **Iterable<E>**: Interface enabling foreach loops

### Constructor

```java
CustomHashSet<String> set = new CustomHashSet<>();
CustomHashSet<String> set = new CustomHashSet<>(100); // Initial capacity
```

### Core Methods

| Method | Time Complexity | Description |
|--------|----------------|-------------|
| `add(E element)` | O(1) avg, O(n) worst | Add element, returns false if duplicate |
| `remove(E element)` | O(1) avg, O(n) worst | Remove element, returns false if not found |
| `contains(E element)` | O(1) avg, O(n) worst | Check if element exists |
| `size()` | O(1) | Get number of elements |
| `isEmpty()` | O(1) | Check if set is empty |
| `clear()` | O(1) | Remove all elements |
| `iterator()` | O(1) create, O(n) full iteration | Returns iterator for traversal |

---

## Method Implementation Analysis

**What CustomHashMap does for us:**
- Hashng algorithm
- Collision handling with ArrayList chaining
- Bucket management
- Rehashing at 75% load factor
- Duplicate detection (via key uniqueness)

**What CustomHashSet adds:**
- Set-specific API (`add` instead of `put`)
- Set-specific return values (boolean instead of old value)
- Set semantics (element collection, not key-value pairs)

---

## Complexity Analysis

### Time Complexity

| Operation | Average Case | Worst Case | Notes |
|-----------|-------------|-----------|-------|
| `add()` | O(1) | O(n) | Worst: all elements hash to same bucket |
| `remove()` | O(1) | O(n) | Must search through collision chain |
| `contains()` | O(1) | O(n) | HashMap lookup + chain search |
| `size()` | O(1) | O(1) | Maintained by HashMap |
| `isEmpty()` | O(1) | O(1) | Simple size check |
| `clear()` | O(1) | O(1) | HashMap reset |
| `iterator()` | O(1) | O(1) | Creates iterator |
| Full iteration | O(n) | O(n) | Visit all n elements |

**Note:** With 75% load factor and good hash distribution, average chain length is ~3 elements, making worst case extremely rare.

### Space Complexity

- **Storage:** O(n) where n = number of elements
- **Overhead:** O(m) where m = HashMap bucket array size
- **Per element:** ~32 bytes (8 key + 8 value + 16 Entry object + ArrayList overhead)

**For 100 elements:**
- Elements: ~3.2 KB
- HashMap buckets (64): ~0.5 KB
- **Total: ~4 KB**

---

## Integration with InteractionAnalyzer

### Duplicate Detection Mechanism

**How it works:**

1. **Interaction.equals() comparison:**
   ```java
   // Ignores InteractionID, compares 6 fields:
   sessionId, userId, timestamp, actionType, contentId, durationSeconds
   ```

2. **HashMap key uniqueness:**
   - When adding Interaction, HashMap hashes it
   - If an equal Interaction exists (same 6 fields), `add()` returns false
   - Only one copy stored in set

3. **Result:**
   - Dataset_1.txt: 50 interactions
   - Dataset_2.txt: 50 interactions (3 duplicates of Dataset_1)
   - **CustomHashSet: 97 unique interactions**

---

## Verification Test Summary

**Key Validations:**
- Duplicate rejection working
- Size tracking accurate
- Iteration functional
- **Interaction.equals() integration confirmed**

---

## Design Trade-offs

### HashMap-Backed vs Native Implementation

**Chosen Approach: HashMap-Backed (Wrapper)**

**Pros:**
- Reuses CustomHashMap's 400+ lines of code
- Automatic benefit from HashMap optimizations
- Simpler codebase (no duplicate logic)
- Industry standard pattern (matches Java)
- Easier to maintain and debug

**Cons:**
- Slightly more memory (dummy PRESENT values)
- Extra method call overhead (negligible)
- Can't optimize for Set-specific use cases

**Alternative: Native Implementation**

**Would require:**
- Reimplementing hashing, collision handling, rehashing
- ~400 lines of duplicate code
- More bugs, harder maintenance
- No performance gain for this dataset size

**Decision:** HashMap-backed is superior for academic and practical purposes.

---

## Conclusion

This `CustomHashSet` implementation successfully demonstrates:

**Set-Map Relationship** - Understanding that Set is Map with dummy values  
**Delegation Pattern** - Reusing CustomHashMap instead of duplicating code  
**O(1) Average Operations** - Efficient add/remove/contains via hashing  
**Generic Type Support** - Works with any element type  
**Iterable Interface** - Enables foreach loop syntax  
**Real-World Pattern** - Matches Java's HashSet implementation strategy  

---

## References

- **DKIT Lecture Slides:** Hash Tables & Sets
- **Java Collections Framework:** HashSet source code analysis
- **Project Requirements:** DSA-2 Custom HashSet Implementation

---

**Last Updated:** December 29, 2025  
**Version:** 1.0  
**Status:** Complete