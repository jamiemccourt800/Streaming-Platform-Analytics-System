import org.example.structure.CustomHashMap;
import java.time.LocalDateTime;

/**
 * Test for CustomHashMap.
 * Tests basic operations, collision handling, and rehashing.
 * 
 * @author Jamie McCourt (D00265301)
 */
public class CustomHashMapTest {
    
    public static void main(String[] args) {
        System.out.println("=== CustomHashMap Test ===\n");
        
        // Test 1: Basic put and get
        System.out.println("Test 1: Basic put and get");
        CustomHashMap<String, Integer> map = new CustomHashMap<>();
        map.put("key1", 100);
        map.put("key2", 200);
        System.out.println("Get key1: " + map.get("key1")); // 100
        System.out.println("Get key2: " + map.get("key2")); // 200
        
        // Test 2: Update existing key
        System.out.println("\nTest 2: Update existing key");
        Integer oldValue = map.put("key1", 150);
        System.out.println("Old value: " + oldValue); // 100
        System.out.println("New value: " + map.get("key1")); // 150
        
        // Test 3: Remove key
        System.out.println("\nTest 3: Remove key");
        map.put("key3", 300);
        Integer removed = map.remove("key2");
        System.out.println("Removed: " + removed); // 200
        System.out.println("Size: " + map.size()); // 2
        
        // Test 4: ContainsKey
        System.out.println("\nTest 4: ContainsKey");
        System.out.println("Contains key1: " + map.containsKey("key1")); // true
        System.out.println("Contains key2: " + map.containsKey("key2")); // false
        
        // Test 5: Null key handling
        System.out.println("\nTest 5: Null key handling");
        map.put(null, 999);
        System.out.println("Get null: " + map.get(null)); // 999
        System.out.println("Contains null: " + map.containsKey(null)); // true
        
        // Test 6: LocalDateTime keys
        System.out.println("\nTest 6: LocalDateTime keys (complex objects)");
        CustomHashMap<LocalDateTime, String> timeMap = new CustomHashMap<>();
        LocalDateTime time1 = LocalDateTime.of(2025, 11, 23, 8, 30);
        LocalDateTime time2 = LocalDateTime.of(2025, 11, 23, 9, 0);
        timeMap.put(time1, "Morning");
        timeMap.put(time2, "Late Morning");
        System.out.println("Get time1: " + timeMap.get(time1)); // Morning
        System.out.println("Get time2: " + timeMap.get(time2)); // Late Morning
        
        // Test 7: Clear operation
        System.out.println("\nTest 7: Clear operation");
        map.clear();
        System.out.println("Size after clear: " + map.size()); // 0
        System.out.println("Is empty: " + map.isEmpty()); // true
        
        // Test 8: Collision handling
        System.out.println("\nTest 8: Collision handling");
        CustomHashMap<Integer, String> collisionMap = new CustomHashMap<>(4);
        collisionMap.put(1, "One");
        collisionMap.put(5, "Five");
        collisionMap.put(9, "Nine");
        System.out.println("Get 1: " + collisionMap.get(1)); // One
        System.out.println("Get 5: " + collisionMap.get(5)); // Five
        System.out.println("Get 9: " + collisionMap.get(9)); // Nine
        
        // Test 9: Rehashing (load factor)
        System.out.println("\nTest 9: Rehashing on load factor");
        CustomHashMap<Integer, String> rehashMap = new CustomHashMap<>(4);
        for (int i = 0; i < 20; i++) {
            rehashMap.put(i, "Value" + i);
        }
        System.out.println("Size after adding 20 items: " + rehashMap.size()); // 20
        System.out.println("Get 0: " + rehashMap.get(0)); // Value0
        System.out.println("Get 19: " + rehashMap.get(19)); // Value19
        
        System.out.println("\n=== All CustomHashMap tests complete ===");
    }
}
