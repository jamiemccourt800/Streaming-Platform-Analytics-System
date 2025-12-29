package org.example.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Custom implementation of a HashMap using collision chaining.
 * Generic key-value storage with automatic resizing at 75% load factor.
 * Uses ArrayList for bucket chaining to handle collisions.
 * 
 * @param <K> Key type
 * @param <V> Value type
 */
public class CustomHashMap<K, V> {
    
    /**
     * Inner class representing a key-value pair entry.
     */
    private static class Entry<K, V> {
        final K key;
        V value;
        
        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public String toString() {
            return key + "=" + value;
        }
    }
    
    // Array of buckets (each bucket is an ArrayList for chaining)
    private ArrayList<Entry<K, V>>[] buckets;
    
    // Number of key-value pairs stored
    private int size;
    
    // Initial capacity
    private static final int DEFAULT_CAPACITY = 16;
    
    // Load factor threshold for resizing (75%)
    private static final double LOAD_FACTOR = 0.75;
    
    /**
     * Constructs an empty CustomHashMap with default capacity.
     */
    @SuppressWarnings("unchecked")
    public CustomHashMap() {
        this.buckets = (ArrayList<Entry<K, V>>[]) new ArrayList[DEFAULT_CAPACITY];
        this.size = 0;
    }
    
    /**
     * Constructs an empty CustomHashMap with specified initial capacity.
     * 
     * @param initialCapacity The initial capacity of the hash map
     */
    @SuppressWarnings("unchecked")
    public CustomHashMap(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.buckets = (ArrayList<Entry<K, V>>[]) new ArrayList[initialCapacity];
        this.size = 0;
    }
    
    /**
     * Calculates the bucket index for a given key.
     * Uses Objects.hash() which handles null keys and complex objects like LocalDateTime.
     * 
     * @param key The key to hash
     * @return The bucket index
     */
    private int getBucketIndex(K key) {
        if (key == null) {
            return 0; // Null keys go to bucket 0
        }
        int hash = Objects.hash(key);
        // Use bitwise AND with positive mask to ensure positive index
        return (hash & 0x7FFFFFFF) % buckets.length;
    }
    
    /**
     * Adds or updates a key-value pair in the map.
     * If the key exists, updates the value. Otherwise, adds a new entry.
     * Triggers rehashing if load factor exceeds 75%.
     * 
     * @param key The key
     * @param value The value
     * @return The previous value associated with key, or null if none existed
     */
    public V put(K key, V value) {
        // Check if rehashing is needed
        if ((double) size / buckets.length >= LOAD_FACTOR) {
            rehash();
        }
        
        int bucketIndex = getBucketIndex(key);
        
        // Initialize bucket if null
        if (buckets[bucketIndex] == null) {
            buckets[bucketIndex] = new ArrayList<>();
        }
        
        // Check if key already exists in this bucket (update scenario)
        for (Entry<K, V> entry : buckets[bucketIndex]) {
            if (Objects.equals(entry.key, key)) {
                V oldValue = entry.value;
                entry.value = value;
                return oldValue; // Return old value
            }
        }
        
        // Key doesn't exist, add new entry
        buckets[bucketIndex].add(new Entry<>(key, value));
        size++;
        return null;
    }
    
    /**
     * Retrieves the value associated with the specified key.
     * 
     * @param key The key to look up
     * @return The value associated with the key, or null if not found
     */
    public V get(K key) {
        int bucketIndex = getBucketIndex(key);
        
        if (buckets[bucketIndex] == null) {
            return null;
        }
        
        // Search through the chain for the key
        for (Entry<K, V> entry : buckets[bucketIndex]) {
            if (Objects.equals(entry.key, key)) {
                return entry.value;
            }
        }
        
        return null; // Key not found
    }
    
    /**
     * Removes the entry for the specified key if it exists.
     * 
     * @param key The key to remove
     * @return The value that was associated with the key, or null if not found
     */
    public V remove(K key) {
        int bucketIndex = getBucketIndex(key);
        
        if (buckets[bucketIndex] == null) {
            return null;
        }
        
        // Search and remove from the chain
        for (int i = 0; i < buckets[bucketIndex].size(); i++) {
            Entry<K, V> entry = buckets[bucketIndex].get(i);
            if (Objects.equals(entry.key, key)) {
                buckets[bucketIndex].remove(i);
                size--;
                return entry.value;
            }
        }
        
        return null; // Key not found
    }
    
    /**
     * Checks if the map contains the specified key.
     * 
     * @param key The key to check
     * @return true if the key exists, false otherwise
     */
    public boolean containsKey(K key) {
        return get(key) != null || (get(key) == null && hasNullValue(key));
    }
    
    /**
     * Helper method to distinguish between "key not found" and "key exists with null value".
     */
    private boolean hasNullValue(K key) {
        int bucketIndex = getBucketIndex(key);
        
        if (buckets[bucketIndex] == null) {
            return false;
        }
        
        for (Entry<K, V> entry : buckets[bucketIndex]) {
            if (Objects.equals(entry.key, key)) {
                return true; // Key exists
            }
        }
        
        return false;
    }
    
    /**
     * Returns the number of key-value pairs in the map.
     * 
     * @return The size of the map
     */
    public int size() {
        return size;
    }
    
    /**
     * Checks if the map is empty.
     * 
     * @return true if the map contains no entries, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * If the key is not present, associates it with the default value.
     * Returns the value associated with the key (either existing or newly set).
     * 
     * Simplified alternative to Java's computeIfAbsent that doesn't require lambdas.
     * 
     * @param key The key
     * @param defaultValue The value to set if key is absent
     * @return The value associated with the key
     */
    public V computeIfAbsent(K key, V defaultValue) {
        V value = get(key);
        if (value == null && !containsKey(key)) {
            put(key, defaultValue);
            return defaultValue;
        }
        return value;
    }
    
    /**
     * Returns a list of all keys in the map.
     * 
     * @return List of keys
     */
    public List<K> keySet() {
        List<K> keys = new ArrayList<>();
        
        for (ArrayList<Entry<K, V>> bucket : buckets) {
            if (bucket != null) {
                for (Entry<K, V> entry : bucket) {
                    keys.add(entry.key);
                }
            }
        }
        
        return keys;
    }
    
    /**
     * Returns a list of all entries (key-value pairs) in the map.
     * 
     * @return List of entries
     */
    public List<Entry<K, V>> entrySet() {
        List<Entry<K, V>> entries = new ArrayList<>();
        
        for (ArrayList<Entry<K, V>> bucket : buckets) {
            if (bucket != null) {
                entries.addAll(bucket);
            }
        }
        
        return entries;
    }
    
    /**
     * Doubles the capacity of the hash map and rehashes all entries.
     * Called automatically when load factor exceeds 75%.
     */
    @SuppressWarnings("unchecked")
    private void rehash() {
        ArrayList<Entry<K, V>>[] oldBuckets = buckets;
        buckets = (ArrayList<Entry<K, V>>[]) new ArrayList[oldBuckets.length * 2];
        size = 0; // Reset size, will be recalculated during re-insertion
        
        // Rehash all existing entries
        for (ArrayList<Entry<K, V>> bucket : oldBuckets) {
            if (bucket != null) {
                for (Entry<K, V> entry : bucket) {
                    put(entry.key, entry.value);
                }
            }
        }
    }
    
    /**
     * Clears all entries from the map.
     */
    @SuppressWarnings("unchecked")
    public void clear() {
        buckets = (ArrayList<Entry<K, V>>[]) new ArrayList[DEFAULT_CAPACITY];
        size = 0;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        
        for (ArrayList<Entry<K, V>> bucket : buckets) {
            if (bucket != null) {
                for (Entry<K, V> entry : bucket) {
                    if (!first) {
                        sb.append(", ");
                    }
                    sb.append(entry);
                    first = false;
                }
            }
        }
        
        sb.append("}");
        return sb.toString();
    }
}