package org.example.structure;

import java.util.Iterator;

/**
 * Custom implementation of a HashSet backed by CustomHashMap.
 * Provides essential set operations with no duplicate elements.
 * 
 * This implementation uses a CustomHashMap internally where each element
 * is stored as a key with a dummy value. This leverages the HashMap's
 * O(1) average-case operations for add, remove, and contains.
 * 
 * Key characteristics:
 * - No duplicate elements (enforced by HashMap keys)
 * - Unordered collection (hash-based storage)
 * - Null element support (via HashMap's null key handling)
 * - Generic type parameter for flexibility
 * 
 * Time Complexity:
 * - add(): O(1) average, O(n) worst case
 * - remove(): O(1) average, O(n) worst case
 * - contains(): O(1) average, O(n) worst case
 * - size(), isEmpty(): O(1)
 * 
 * Space Complexity: O(n) where n is the number of elements
 * 
 * @param <E> The type of elements maintained by this set
 * 
 * @author Jamie McCourt (D00265301)
 * @version 1.0
 */
public class CustomHashSet<E> implements Iterable<E> {
    
    // Internal storage - uses CustomHashMap with dummy values
    private final CustomHashMap<E, Object> map;
    
    // Dummy value to associate with keys in the backing map
    // All keys map to this single object to minimize memory usage
    private static final Object PRESENT = new Object();
    
    /**
     * Constructs an empty CustomHashSet with default initial capacity (16).
     * The set will automatically resize when load factor exceeds 75%.
     */
    public CustomHashSet() {
        this.map = new CustomHashMap<>();
    }
    
    /**
     * Constructs an empty CustomHashSet with specified initial capacity.
     * Use this constructor when you know the approximate size in advance
     * to minimize rehashing operations.
     * 
     * @param initialCapacity The initial capacity of the hash set
     * @throws IllegalArgumentException if initialCapacity is non-positive
     */
    public CustomHashSet(int initialCapacity) {
        this.map = new CustomHashMap<>(initialCapacity);
    }
    
    /**
     * Adds the specified element to this set if it is not already present.
     * If this set already contains the element, the call leaves the set
     * unchanged and returns false.
     * 
     * This operation leverages the underlying CustomHashMap's put() method,
     * which provides O(1) average-case performance.
     * 
     * @param element The element to be added to this set
     * @return true if the set did not already contain the specified element,
     *         false if the element was already present
     * 
     * Time Complexity: O(1) average case, O(n) worst case (all elements collide)
     * Space Complexity: O(1) per element
     */
    public boolean add(E element) {
        Object previous = map.put(element, PRESENT);
        return previous == null; // true if element was new, false if already existed
    }
    
    /**
     * Removes the specified element from this set if it is present.
     * If this set does not contain the element, the call leaves the set
     * unchanged and returns false.
     * 
     * This operation leverages the underlying CustomHashMap's remove() method,
     * which provides O(1) average-case performance.
     * 
     * @param element The element to be removed from this set, if present
     * @return true if the set contained the specified element and it was removed,
     *         false if the element was not present
     * 
     * Time Complexity: O(1) average case, O(n) worst case (must search through chain)
     * Space Complexity: O(1) - no additional space required
     */
    public boolean remove(E element) {
        Object previous = map.remove(element);
        return previous != null; // true if element existed and was removed
    }
    
    // TODO: Implement contains, size, isEmpty, clear methods
    // TODO: Implement iterator() for Iterable interface
    // TODO: Add toString() for debugging
}