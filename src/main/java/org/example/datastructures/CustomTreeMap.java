package org.example.datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * My own TreeMap using a tree.
 * Keys are stored in order so I can search for ranges quickly.
 * 
 * @param <K> The key type (needs to be comparable)
 * @param <V> The value type
 * 
 * @author Shauna
 */
public class CustomTreeMap<K extends Comparable<? super K>, V> {
    
    // A node in the tree
    private class Node {
        K key;
        V value;
        Node left;
        Node right;
        
        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }
    
    private Node root;
    private int size;
    
    /**
     * Make a new empty tree map.
     */
    public CustomTreeMap() {
        this.root = null;
        this.size = 0;
    }
    
    /**
     * Add a key and value to the map.
     * If the key is already there, replace the old value.
     * 
     * @param key The key
     * @param value The value
     * @return The old value if the key existed, or null
     */
    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        
        // If tree is empty, make the root
        if (root == null) {
            root = new Node(key, value);
            size++;
            return null;
        }
        
        // Otherwise find the right spot
        return putHelper(root, key, value);
    }
    
    // Helper method to add a node
    private V putHelper(Node current, K key, V value) {
        int comparison = key.compareTo(current.key);
        
        if (comparison == 0) {
            // Key already exists, replace value
            V oldValue = current.value;
            current.value = value;
            return oldValue;
        } else if (comparison < 0) {
            // Go left
            if (current.left == null) {
                current.left = new Node(key, value);
                size++;
                return null;
            }
            return putHelper(current.left, key, value);
        } else {
            // Go right
            if (current.right == null) {
                current.right = new Node(key, value);
                size++;
                return null;
            }
            return putHelper(current.right, key, value);
        }
    }
    
    /**
     * Get the value for a key.
     * 
     * @param key The key to look for
     * @return The value, or null if not found
     */
    public V get(K key) {
        if (key == null) {
            return null;
        }
        
        Node node = findNode(root, key);
        return node == null ? null : node.value;
    }
    
    // Helper to find a node
    private Node findNode(Node current, K key) {
        if (current == null) {
            return null;
        }
        
        int comparison = key.compareTo(current.key);
        
        if (comparison == 0) {
            return current;
        } else if (comparison < 0) {
            return findNode(current.left, key);
        } else {
            return findNode(current.right, key);
        }
    }
    
    /**
     * Check if a key is in the map.
     * 
     * @param key The key to check
     * @return true if found
     */
    public boolean containsKey(K key) {
        return get(key) != null || (findNode(root, key) != null);
    }
    
    /**
     * Remove a key from the map.
     * 
     * @param key The key to remove
     * @return The old value, or null
     */
    public V remove(K key) {
        if (key == null || root == null) {
            return null;
        }
        
        V oldValue = get(key);
        if (oldValue != null) {
            root = removeHelper(root, key);
            size--;
        }
        return oldValue;
    }
    
    // Helper to remove a node
    private Node removeHelper(Node current, K key) {
        if (current == null) {
            return null;
        }
        
        int comparison = key.compareTo(current.key);
        
        if (comparison < 0) {
            current.left = removeHelper(current.left, key);
        } else if (comparison > 0) {
            current.right = removeHelper(current.right, key);
        } else {
            // Found it!
            
            // No children
            if (current.left == null && current.right == null) {
                return null;
            }
            
            // One child
            if (current.left == null) {
                return current.right;
            }
            if (current.right == null) {
                return current.left;
            }
            
            // Two children - find the smallest on the right
            Node smallest = findSmallest(current.right);
            current.key = smallest.key;
            current.value = smallest.value;
            current.right = removeHelper(current.right, smallest.key);
        }
        
        return current;
    }
    
    // Find the smallest node
    private Node findSmallest(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }
    
    /**
     * Get the number of items in the map.
     * 
     * @return The size
     */
    public int size() {
        return size;
    }
    
    /**
     * Check if map is empty.
     * 
     * @return true if empty
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Get the smallest key.
     * 
     * @return The first key, or null if empty
     */
    public K firstKey() {
        if (root == null) {
            return null;
        }
        Node min = findSmallest(root);
        return min.key;
    }
    
    /**
     * Get the biggest key.
     * 
     * @return The last key, or null if empty
     */
    public K lastKey() {
        if (root == null) {
            return null;
        }
        Node node = root;
        while (node.right != null) {
            node = node.right;
        }
        return node.key;
    }
    
    /**
     * Get all keys in order.
     * 
     * @return List of keys
     */
    public List<K> keySet() {
        List<K> keys = new ArrayList<>();
        collectKeysInOrder(root, keys);
        return keys;
    }
    
    // Visit nodes in order (left, middle, right)
    private void collectKeysInOrder(Node node, List<K> keys) {
        if (node == null) {
            return;
        }
        collectKeysInOrder(node.left, keys);
        keys.add(node.key);
        collectKeysInOrder(node.right, keys);
    }
    
    /**
     * Get all items between two keys.
     * This is for finding interactions in a time range.
     * 
     * @param fromKey Start key
     * @param toKey End key
     * @return New map with only items in that range
     */
    public CustomTreeMap<K, V> subMap(K fromKey, K toKey) {
        if (fromKey == null || toKey == null) {
            throw new IllegalArgumentException("Keys cannot be null");
        }
        
        CustomTreeMap<K, V> result = new CustomTreeMap<>();
        collectInRange(root, fromKey, toKey, result);
        return result;
    }
    
    // Helper to collect nodes in a range
    private void collectInRange(Node node, K fromKey, K toKey, CustomTreeMap<K, V> result) {
        if (node == null) {
            return;
        }
        
        // Check left side
        if (node.key.compareTo(fromKey) > 0) {
            collectInRange(node.left, fromKey, toKey, result);
        }
        
        // Check if this node is in range
        if (node.key.compareTo(fromKey) >= 0 && node.key.compareTo(toKey) <= 0) {
            result.put(node.key, node.value);
        }
        
        // Check right side
        if (node.key.compareTo(toKey) < 0) {
            collectInRange(node.right, fromKey, toKey, result);
        }
    }
    
    /**
     * Get all values.
     * 
     * @return List of values
     */
    public List<V> values() {
        List<V> valueList = new ArrayList<>();
        collectValues(root, valueList);
        return valueList;
    }
    
    // Helper to collect values
    private void collectValues(Node node, List<V> valueList) {
        if (node == null) {
            return;
        }
        collectValues(node.left, valueList);
        valueList.add(node.value);
        collectValues(node.right, valueList);
    }
    
    /**
     * Clear the map.
     */
    public void clear() {
        root = null;
        size = 0;
    }
    
    /**
     * Convert to string for printing.
     */
    @Override
    public String toString() {
        if (isEmpty()) {
            return "{}";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        
        List<K> keys = keySet();
        for (int i = 0; i < keys.size(); i++) {
            K key = keys.get(i);
            sb.append(key).append("=").append(get(key));
            if (i < keys.size() - 1) {
                sb.append(", ");
            }
        }
        
        sb.append("}");
        return sb.toString();
    }
}
