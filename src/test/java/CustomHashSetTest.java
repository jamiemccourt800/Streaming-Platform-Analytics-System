import org.example.structure.CustomHashSet;
import org.example.model.Interaction;
import java.time.LocalDateTime;

/**
 * Test for CustomHashSet.
 * Tests basic set operations and deduplication.
 * 
 * @author Jamie McCourt (D00265301)
 */
public class CustomHashSetTest {
    
    public static void main(String[] args) {
        System.out.println("=== CustomHashSet Test ===\n");
        
        // Test 1: Basic add operation
        System.out.println("Test 1: Basic add operation");
        CustomHashSet<String> set = new CustomHashSet<>();
        set.add("element1");
        set.add("element2");
        System.out.println("Size: " + set.size()); // 2
        
        // Test 2: Duplicate prevention
        System.out.println("\nTest 2: Duplicate prevention");
        boolean addedDuplicate = set.add("element1");
        System.out.println("Added duplicate: " + addedDuplicate); // false
        System.out.println("Size: " + set.size()); // 2
        
        // Test 3: Contains operation
        System.out.println("\nTest 3: Contains operation");
        System.out.println("Contains element1: " + set.contains("element1")); // true
        System.out.println("Contains element3: " + set.contains("element3")); // false
        
        // Test 4: Remove operation
        System.out.println("\nTest 4: Remove operation");
        boolean removed = set.remove("element1");
        System.out.println("Removed element1: " + removed); // true
        System.out.println("Size: " + set.size()); // 1
        System.out.println("Contains element1: " + set.contains("element1")); // false
        
        // Test 5: Remove non-existent element
        System.out.println("\nTest 5: Remove non-existent");
        boolean removedNonExistent = set.remove("element99");
        System.out.println("Removed non-existent: " + removedNonExistent); // false
        
        // Test 6: Clear operation
        System.out.println("\nTest 6: Clear operation");
        set.add("A");
        set.add("B");
        set.clear();
        System.out.println("Size after clear: " + set.size()); // 0
        System.out.println("Is empty: " + set.isEmpty()); // true
        
        // Test 7: Null element handling
        System.out.println("\nTest 7: Null element handling");
        set.add(null);
        System.out.println("Contains null: " + set.contains(null)); // true
        System.out.println("Size: " + set.size()); // 1
        
        // Test 8: Custom objects (Interaction deduplication)
        System.out.println("\nTest 8: Interaction deduplication");
        CustomHashSet<Interaction> interactionSet = new CustomHashSet<>();
        Interaction i1 = new Interaction("I001", "S001", "U001", 
            LocalDateTime.of(2025, 11, 23, 8, 0), "PLAY", "C001", 120);
        Interaction i2 = new Interaction("I002", "S001", "U001", 
            LocalDateTime.of(2025, 11, 23, 8, 0), "PLAY", "C001", 120);
        
        interactionSet.add(i1);
        boolean addedDup = interactionSet.add(i2);
        System.out.println("Added duplicate interaction: " + addedDup); // false
        System.out.println("Set size: " + interactionSet.size()); // 1
        
        // Test 9: Iterator functionality
        System.out.println("\nTest 9: Iterator functionality");
        CustomHashSet<String> iterSet = new CustomHashSet<>();
        iterSet.add("A");
        iterSet.add("B");
        iterSet.add("C");
        System.out.print("Elements: ");
        for (String element : iterSet) {
            System.out.print(element + " ");
        }
        System.out.println();
        
        // Test 10: Large dataset
        System.out.println("\nTest 10: Large dataset");
        CustomHashSet<Integer> largeSet = new CustomHashSet<>();
        for (int i = 0; i < 1000; i++) {
            largeSet.add(i);
        }
        System.out.println("Size: " + largeSet.size()); // 1000
        System.out.println("Contains 500: " + largeSet.contains(500)); // true
        
        System.out.println("\n=== All CustomHashSet tests complete ===");
    }
}
