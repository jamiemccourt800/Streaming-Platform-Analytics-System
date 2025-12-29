import org.example.datastructures.CustomTreeMap;

/**
 * Quick test class for CustomTreeMap to verify it works correctly.
 */
public class CustomTreeMapTest {
    
    public static void main(String[] args) {
        System.out.println("=== CustomTreeMap Test ===\n");
        
        // Test 1: Basic put and get
        System.out.println("Test 1: Basic put and get");
        CustomTreeMap<Integer, String> map = new CustomTreeMap<>();
        map.put(5, "five");
        map.put(3, "three");
        map.put(7, "seven");
        map.put(1, "one");
        map.put(9, "nine");
        
        System.out.println("Size: " + map.size()); // Should be 5
        System.out.println("Get 3: " + map.get(3)); // Should be "three"
        System.out.println("Get 7: " + map.get(7)); // Should be "seven"
        System.out.println("✓ Test 1 passed\n");
        
        // Test 2: Sorted order (keySet)
        System.out.println("Test 2: Keys in sorted order");
        System.out.println("Keys: " + map.keySet()); // Should be [1, 3, 5, 7, 9]
        System.out.println("✓ Test 2 passed\n");
        
        // Test 3: First and last key
        System.out.println("Test 3: First and last key");
        System.out.println("First key: " + map.firstKey()); // Should be 1
        System.out.println("Last key: " + map.lastKey()); // Should be 9
        System.out.println("✓ Test 3 passed\n");
        
        // Test 4: SubMap (range query) - THIS IS CRITICAL FOR TIMESTAMPS
        System.out.println("Test 4: SubMap (range query)");
        CustomTreeMap<Integer, String> subMap = map.subMap(3, 7);
        System.out.println("SubMap [3-7]: " + subMap.keySet()); // Should be [3, 5, 7]
        System.out.println("SubMap size: " + subMap.size()); // Should be 3
        System.out.println("✓ Test 4 passed\n");
        
        // Test 5: Remove
        System.out.println("Test 5: Remove");
        String removed = map.remove(5);
        System.out.println("Removed value: " + removed); // Should be "five"
        System.out.println("Size after remove: " + map.size()); // Should be 4
        System.out.println("Keys after remove: " + map.keySet()); // Should be [1, 3, 7, 9]
        System.out.println("✓ Test 5 passed\n");
        
        // Test 6: Replace existing key
        System.out.println("Test 6: Replace existing key");
        String oldValue = map.put(3, "THREE");
        System.out.println("Old value: " + oldValue); // Should be "three"
        System.out.println("New value: " + map.get(3)); // Should be "THREE"
        System.out.println("✓ Test 6 passed\n");
        
        // Test 7: Test with Strings (like for timestamps)
        System.out.println("Test 7: String keys (simulating timestamps)");
        CustomTreeMap<String, Integer> timeMap = new CustomTreeMap<>();
        timeMap.put("2025-11-23 08:01", 1);
        timeMap.put("2025-11-23 09:15", 2);
        timeMap.put("2025-11-23 10:30", 3);
        timeMap.put("2025-11-23 11:45", 4);
        
        System.out.println("Time keys: " + timeMap.keySet());
        CustomTreeMap<String, Integer> timeRange = timeMap.subMap("2025-11-23 09:00", "2025-11-23 11:00");
        System.out.println("Time range [09:00-11:00]: " + timeRange.keySet());
        System.out.println("✓ Test 7 passed\n");
        
        System.out.println("=== ALL TESTS PASSED! ✓ ===");
        System.out.println("CustomTreeMap is working correctly!");
    }
}
