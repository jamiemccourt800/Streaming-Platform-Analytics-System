import org.example.model.Interaction;
import org.example.service.InteractionAnalyzer;
import org.example.util.DataLoader;
import java.util.List;

/**
 * Test for deduplication logic.
 * Verifies that duplicate interactions are correctly removed when merging datasets.
 * 
 * @author Jamie McCourt (D00265301)
 */
public class DeduplicationTest {
    
    public static void main(String[] args) throws Exception {
        System.out.println("=== Deduplication Test ===\n");
        
        // Load datasets
        System.out.println("Test 1: Load datasets");
        List<Interaction> dataset1 = DataLoader.loadFromFile("data/Dataset_1.txt");
        List<Interaction> dataset2 = DataLoader.loadFromFile("data/Dataset_2.txt");
        System.out.println("Dataset 1 size: " + dataset1.size()); // 50
        System.out.println("Dataset 2 size: " + dataset2.size()); // 50
        
        // Test 2: Deduplication count
        System.out.println("\nTest 2: Deduplication produces correct count");
        List<Interaction> deduplicated = InteractionAnalyzer.deduplicateDatasets(dataset1, dataset2);
        System.out.println("Combined total: " + (dataset1.size() + dataset2.size())); // 100
        System.out.println("After deduplication: " + deduplicated.size()); // 97
        System.out.println("Duplicates removed: " + ((dataset1.size() + dataset2.size()) - deduplicated.size())); // 3
        
        // Test 3: Known duplicates removed
        System.out.println("\nTest 3: Known duplicates removed");
        int i016Count = 0, i025Count = 0, i043Count = 0;
        for (Interaction i : deduplicated) {
            if (i.getInteractionId().equals("I016") || i.getInteractionId().equals("I053")) {
                i016Count++;
            }
            if (i.getInteractionId().equals("I025") || i.getInteractionId().equals("I055")) {
                i025Count++;
            }
            if (i.getInteractionId().equals("I043") || i.getInteractionId().equals("I081")) {
                i043Count++;
            }
        }
        System.out.println("I016/I053 count: " + i016Count + " (expected 1)");
        System.out.println("I025/I055 count: " + i025Count + " (expected 1)");
        System.out.println("I043/I081 count: " + i043Count + " (expected 1)");
        
        // Test 4: No data loss
        System.out.println("\nTest 4: No data loss - unique records preserved");
        boolean hasI001 = deduplicated.stream().anyMatch(i -> i.getInteractionId().equals("I001"));
        boolean hasI050 = deduplicated.stream().anyMatch(i -> i.getInteractionId().equals("I050"));
        boolean hasI051 = deduplicated.stream().anyMatch(i -> i.getInteractionId().equals("I051"));
        boolean hasI100 = deduplicated.stream().anyMatch(i -> i.getInteractionId().equals("I100"));
        System.out.println("Has I001: " + hasI001); // true
        System.out.println("Has I050: " + hasI050); // true
        System.out.println("Has I051: " + hasI051); // true
        System.out.println("Has I100: " + hasI100); // true
        
        // Test 5: Empty dataset handling
        System.out.println("\nTest 5: Empty dataset handling");
        List<Interaction> emptyList = List.of();
        List<Interaction> dedup2 = InteractionAnalyzer.deduplicateDatasets(dataset1, emptyList);
        System.out.println("Dataset1 + empty: " + dedup2.size()); // 50
        
        System.out.println("\n=== All Deduplication tests complete ===");
        System.out.println("\nExpected duplicates (3 total):");
        System.out.println("- I016 (Dataset 1) = I053 (Dataset 2)");
        System.out.println("- I025 (Dataset 1) = I055 (Dataset 2)");
        System.out.println("- I043 (Dataset 1) = I081 (Dataset 2)");
    }
}
