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
    
    /**
     * Returns true if this set contains the specified element.
     * More formally, returns true if and only if this set contains an
     * element e such that Objects.equals(element, e).
     * 
     * This operation leverages the underlying CustomHashMap's containsKey() method,
     * which provides O(1) average-case performance.
     * 
     * @param element The element whose presence in this set is to be tested
     * @return true if this set contains the specified element, false otherwise
     * 
     * Time Complexity: O(1) average case, O(n) worst case (must search through chain)
     * Space Complexity: O(1) - no additional space required
     */
    public boolean contains(E element) {
        return map.containsKey(element);
    }
    
    /**
     * Returns the number of elements in this set (its cardinality).
     * 
     * @return The number of elements in this set
     * 
     * Time Complexity: O(1) - size is maintained by the underlying HashMap
     * Space Complexity: O(1)
     */
    public int size() {
        return map.size();
    }
    
    /**
     * Returns true if this set contains no elements.
     * 
     * @return true if this set contains no elements, false otherwise
     * 
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }
    
    /**
     * Removes all of the elements from this set.
     * The set will be empty after this call returns.
     * 
     * This operation resets the underlying HashMap to its default capacity.
     * 
     * Time Complexity: O(1) - HashMap.clear() resets to default state
     * Space Complexity: O(1)
     */
    public void clear() {
        map.clear();
    }
    
    /**
     * Returns an iterator over the elements in this set.
     * The elements are returned in no particular order (due to hash-based storage).
     * 
     * This implementation returns an iterator over the keys of the underlying HashMap.
     * The iterator supports traversal but does not support the remove() operation.
     * 
     * @return An iterator over the elements in this set
     * 
     * Time Complexity: O(1) to create iterator, O(n) to iterate all elements
     * Space Complexity: O(1) for iterator itself
     */
    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }
    
    /**
     * Returns a string representation of this set.
     * The string representation consists of a list of the set's elements
     * in the order they are returned by its iterator, enclosed in square brackets ("[]").
     * Adjacent elements are separated by ", " (comma and space).
     * 
     * This method is useful for debugging and logging purposes.
     * 
     * @return A string representation of this set
     */
    @Override
    public String toString() {
        return map.keySet().toString();
    }
}