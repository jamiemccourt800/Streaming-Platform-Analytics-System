import org.example.structure.CustomHashSet;
import org.example.model.Interaction;
import java.time.LocalDateTime;

public class CustomHashSetTest {
    
    public static void main(String[] args) {
        System.out.println("=== CustomHashSet Test Suite ===\n");
        
        int passed = 0;
        int total = 0;
        
        // Test 1: Basic add operation
        total++;
        try {
            CustomHashSet<String> set = new CustomHashSet<>();
            boolean added1 = set.add("element1");
            boolean added2 = set.add("element2");
            
            if (added1 && added2 && set.size() == 2) {
                System.out.println("✓ Test 1: Basic add operation - PASSED");
                passed++;
            } else {
                System.out.println("✗ Test 1: Basic add operation - FAILED");
            }
        } catch (Exception e) {
            System.out.println("✗ Test 1: Basic add operation - EXCEPTION: " + e.getMessage());
        }
        
        // Test 2: Duplicate prevention
        total++;
        try {
            CustomHashSet<String> set = new CustomHashSet<>();
            set.add("element1");
            boolean addedDuplicate = set.add("element1");
            
            if (!addedDuplicate && set.size() == 1) {
                System.out.println("✓ Test 2: Duplicate prevention - PASSED");
                passed++;
            } else {
                System.out.println("✗ Test 2: Duplicate prevention - FAILED");
            }
        } catch (Exception e) {
            System.out.println("✗ Test 2: Duplicate prevention - EXCEPTION: " + e.getMessage());
        }
        
        // Test 3: Contains operation
        total++;
        try {
            CustomHashSet<String> set = new CustomHashSet<>();
            set.add("element1");
            set.add("element2");
            
            if (set.contains("element1") && set.contains("element2") && !set.contains("element3")) {
                System.out.println("✓ Test 3: Contains operation - PASSED");
                passed++;
            } else {
                System.out.println("✗ Test 3: Contains operation - FAILED");
            }
        } catch (Exception e) {
            System.out.println("✗ Test 3: Contains operation - EXCEPTION: " + e.getMessage());
        }
        
        // Test 4: Remove operation
        total++;
        try {
            CustomHashSet<String> set = new CustomHashSet<>();
            set.add("element1");
            set.add("element2");
            boolean removed = set.remove("element1");
            
            if (removed && !set.contains("element1") && set.contains("element2") && set.size() == 1) {
                System.out.println("✓ Test 4: Remove operation - PASSED");
                passed++;
            } else {
                System.out.println("✗ Test 4: Remove operation - FAILED");
            }
        } catch (Exception e) {
            System.out.println("✗ Test 4: Remove operation - EXCEPTION: " + e.getMessage());
        }
        
        // Test 5: Remove non-existent element
        total++;
        try {
            CustomHashSet<String> set = new CustomHashSet<>();
            set.add("element1");
            boolean removed = set.remove("element2");
            
            if (!removed && set.size() == 1) {
                System.out.println("✓ Test 5: Remove non-existent - PASSED");
                passed++;
            } else {
                System.out.println("✗ Test 5: Remove non-existent - FAILED");
            }
        } catch (Exception e) {
            System.out.println("✗ Test 5: Remove non-existent - EXCEPTION: " + e.getMessage());
        }
        
        // Test 6: Clear operation
        total++;
        try {
            CustomHashSet<String> set = new CustomHashSet<>();
            set.add("element1");
            set.add("element2");
            set.add("element3");
            set.clear();
            
            if (set.isEmpty() && set.size() == 0 && !set.contains("element1")) {
                System.out.println("✓ Test 6: Clear operation - PASSED");
                passed++;
            } else {
                System.out.println("✗ Test 6: Clear operation - FAILED");
            }
        } catch (Exception e) {
            System.out.println("✗ Test 6: Clear operation - EXCEPTION: " + e.getMessage());
        }
        
        // Test 7: Null element handling
        total++;
        try {
            CustomHashSet<String> set = new CustomHashSet<>();
            set.add(null);
            
            if (set.contains(null) && set.size() == 1) {
                System.out.println("✓ Test 7: Null element handling - PASSED");
                passed++;
            } else {
                System.out.println("✗ Test 7: Null element handling - FAILED");
            }
        } catch (Exception e) {
            System.out.println("✗ Test 7: Null element handling - EXCEPTION: " + e.getMessage());
        }
        
        // Test 8: Custom objects (Interaction deduplication)
        total++;
        try {
            CustomHashSet<Interaction> set = new CustomHashSet<>();
            Interaction i1 = new Interaction("I001", "S001", "U001", 
                LocalDateTime.of(2025, 11, 23, 8, 0), "PLAY", "C001", 120);
            Interaction i2 = new Interaction("I002", "S001", "U001", 
                LocalDateTime.of(2025, 11, 23, 8, 0), "PLAY", "C001", 120);
            
            set.add(i1);
            boolean addedDup = set.add(i2); // Should be considered duplicate (equals ignores ID)
            
            if (!addedDup && set.size() == 1) {
                System.out.println("✓ Test 8: Interaction deduplication - PASSED");
                passed++;
            } else {
                System.out.println("✗ Test 8: Interaction deduplication - FAILED");
            }
        } catch (Exception e) {
            System.out.println("✗ Test 8: Interaction deduplication - EXCEPTION: " + e.getMessage());
        }
        
        // Test 9: Iterator functionality
        total++;
        try {
            CustomHashSet<String> set = new CustomHashSet<>();
            set.add("A");
            set.add("B");
            set.add("C");
            
            int count = 0;
            for (String element : set) {
                count++;
            }
            
            if (count == 3) {
                System.out.println("✓ Test 9: Iterator functionality - PASSED");
                passed++;
            } else {
                System.out.println("✗ Test 9: Iterator functionality - FAILED");
            }
        } catch (Exception e) {
            System.out.println("✗ Test 9: Iterator functionality - EXCEPTION: " + e.getMessage());
        }
        
        // Test 10: Large dataset (stress test)
        total++;
        try {
            CustomHashSet<Integer> set = new CustomHashSet<>();
            for (int i = 0; i < 1000; i++) {
                set.add(i);
            }
            
            boolean allPresent = true;
            for (int i = 0; i < 1000; i++) {
                if (!set.contains(i)) {
                    allPresent = false;
                    break;
                }
            }
            
            if (allPresent && set.size() == 1000) {
                System.out.println("✓ Test 10: Large dataset - PASSED");
                passed++;
            } else {
                System.out.println("✗ Test 10: Large dataset - FAILED");
            }
        } catch (Exception e) {
            System.out.println("✗ Test 10: Large dataset - EXCEPTION: " + e.getMessage());
        }
        
        System.out.println("\n=================================");
        System.out.println("Tests Passed: " + passed + "/" + total);
        System.out.println("=================================");
    }
}
