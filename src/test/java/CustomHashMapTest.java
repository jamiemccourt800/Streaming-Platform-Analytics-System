import org.example.structure.CustomHashMap;
import java.time.LocalDateTime;

public class CustomHashMapTest {
    
    public static void main(String[] args) {
        System.out.println("=== CustomHashMap Test Suite ===\n");
        
        int passed = 0;
        int total = 0;
        
        // Test 1: Basic put and get
        total++;
        try {
            CustomHashMap<String, Integer> map = new CustomHashMap<>();
            map.put("key1", 100);
            map.put("key2", 200);
            
            if (map.get("key1") == 100 && map.get("key2") == 200) {
                System.out.println("✓ Test 1: Basic put and get - PASSED");
                passed++;
            } else {
                System.out.println("✗ Test 1: Basic put and get - FAILED");
            }
        } catch (Exception e) {
            System.out.println("✗ Test 1: Basic put and get - EXCEPTION: " + e.getMessage());
        }
        
        // Test 2: Update existing key
        total++;
        try {
            CustomHashMap<String, Integer> map = new CustomHashMap<>();
            map.put("key1", 100);
            Integer oldValue = map.put("key1", 150);
            
            if (oldValue == 100 && map.get("key1") == 150) {
                System.out.println("✓ Test 2: Update existing key - PASSED");
                passed++;
            } else {
                System.out.println("✗ Test 2: Update existing key - FAILED");
            }
        } catch (Exception e) {
            System.out.println("✗ Test 2: Update existing key - EXCEPTION: " + e.getMessage());
        }
        
        // Test 3: Remove key
        total++;
        try {
            CustomHashMap<String, Integer> map = new CustomHashMap<>();
            map.put("key1", 100);
            map.put("key2", 200);
            Integer removed = map.remove("key1");
            
            if (removed == 100 && map.get("key1") == null && map.get("key2") == 200) {
                System.out.println("✓ Test 3: Remove key - PASSED");
                passed++;
            } else {
                System.out.println("✗ Test 3: Remove key - FAILED");
            }
        } catch (Exception e) {
            System.out.println("✗ Test 3: Remove key - EXCEPTION: " + e.getMessage());
        }
        
        // Test 4: ContainsKey
        total++;
        try {
            CustomHashMap<String, Integer> map = new CustomHashMap<>();
            map.put("key1", 100);
            
            if (map.containsKey("key1") && !map.containsKey("key2")) {
                System.out.println("✓ Test 4: ContainsKey - PASSED");
                passed++;
            } else {
                System.out.println("✗ Test 4: ContainsKey - FAILED");
            }
        } catch (Exception e) {
            System.out.println("✗ Test 4: ContainsKey - EXCEPTION: " + e.getMessage());
        }
        
        // Test 5: Size tracking
        total++;
        try {
            CustomHashMap<String, Integer> map = new CustomHashMap<>();
            if (map.size() != 0) {
                System.out.println("✗ Test 5: Size tracking - FAILED (initial size)");
            } else {
                map.put("key1", 100);
                map.put("key2", 200);
                map.put("key3", 300);
                
                if (map.size() == 3) {
                    map.remove("key2");
                    if (map.size() == 2) {
                        System.out.println("✓ Test 5: Size tracking - PASSED");
                        passed++;
                    } else {
                        System.out.println("✗ Test 5: Size tracking - FAILED (after remove)");
                    }
                } else {
                    System.out.println("✗ Test 5: Size tracking - FAILED (after puts)");
                }
            }
        } catch (Exception e) {
            System.out.println("✗ Test 5: Size tracking - EXCEPTION: " + e.getMessage());
        }
        
        // Test 6: Null key handling
        total++;
        try {
            CustomHashMap<String, Integer> map = new CustomHashMap<>();
            map.put(null, 999);
            
            if (map.get(null) == 999 && map.containsKey(null)) {
                System.out.println("✓ Test 6: Null key handling - PASSED");
                passed++;
            } else {
                System.out.println("✗ Test 6: Null key handling - FAILED");
            }
        } catch (Exception e) {
            System.out.println("✗ Test 6: Null key handling - EXCEPTION: " + e.getMessage());
        }
        
        // Test 7: LocalDateTime keys (complex objects)
        total++;
        try {
            CustomHashMap<LocalDateTime, String> map = new CustomHashMap<>();
            LocalDateTime time1 = LocalDateTime.of(2025, 11, 23, 8, 30);
            LocalDateTime time2 = LocalDateTime.of(2025, 11, 23, 9, 0);
            
            map.put(time1, "Morning");
            map.put(time2, "Late Morning");
            
            if (map.get(time1).equals("Morning") && map.get(time2).equals("Late Morning")) {
                System.out.println("✓ Test 7: LocalDateTime keys - PASSED");
                passed++;
            } else {
                System.out.println("✗ Test 7: LocalDateTime keys - FAILED");
            }
        } catch (Exception e) {
            System.out.println("✗ Test 7: LocalDateTime keys - EXCEPTION: " + e.getMessage());
        }
        
        // Test 8: Clear operation
        total++;
        try {
            CustomHashMap<String, Integer> map = new CustomHashMap<>();
            map.put("key1", 100);
            map.put("key2", 200);
            map.clear();
            
            if (map.size() == 0 && map.isEmpty() && map.get("key1") == null) {
                System.out.println("✓ Test 8: Clear operation - PASSED");
                passed++;
            } else {
                System.out.println("✗ Test 8: Clear operation - FAILED");
            }
        } catch (Exception e) {
            System.out.println("✗ Test 8: Clear operation - EXCEPTION: " + e.getMessage());
        }
        
        // Test 9: Collision handling (multiple keys to same bucket)
        total++;
        try {
            CustomHashMap<Integer, String> map = new CustomHashMap<>(4); // Small capacity to force collisions
            map.put(1, "One");
            map.put(5, "Five");
            map.put(9, "Nine");
            map.put(13, "Thirteen");
            
            if (map.get(1).equals("One") && map.get(5).equals("Five") && 
                map.get(9).equals("Nine") && map.get(13).equals("Thirteen")) {
                System.out.println("✓ Test 9: Collision handling - PASSED");
                passed++;
            } else {
                System.out.println("✗ Test 9: Collision handling - FAILED");
            }
        } catch (Exception e) {
            System.out.println("✗ Test 9: Collision handling - EXCEPTION: " + e.getMessage());
        }
        
        // Test 10: Rehashing (load factor test)
        total++;
        try {
            CustomHashMap<Integer, String> map = new CustomHashMap<>(4);
            // Add enough elements to trigger rehashing
            for (int i = 0; i < 20; i++) {
                map.put(i, "Value" + i);
            }
            
            // Verify all elements still accessible after rehash
            boolean allPresent = true;
            for (int i = 0; i < 20; i++) {
                if (!map.get(i).equals("Value" + i)) {
                    allPresent = false;
                    break;
                }
            }
            
            if (allPresent && map.size() == 20) {
                System.out.println("✓ Test 10: Rehashing on load factor - PASSED");
                passed++;
            } else {
                System.out.println("✗ Test 10: Rehashing on load factor - FAILED");
            }
        } catch (Exception e) {
            System.out.println("✗ Test 10: Rehashing on load factor - EXCEPTION: " + e.getMessage());
        }
        
        System.out.println("\n=================================");
        System.out.println("Tests Passed: " + passed + "/" + total);
        System.out.println("=================================");
    }
}
