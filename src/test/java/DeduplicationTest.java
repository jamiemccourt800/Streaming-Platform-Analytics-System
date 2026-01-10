import org.example.model.Interaction;
import org.example.service.InteractionAnalyzer;
import org.example.util.DataLoader;
import java.util.List;

public class DeduplicationTest {
    
    public static void main(String[] args) {
        System.out.println("=== Deduplication Test Suite ===\n");
        
        int passed = 0;
        int total = 0;
        
        // Test 1: Load datasets
        total++;
        try {
            List<Interaction> dataset1 = DataLoader.loadFromFile("data/Dataset_1.txt");
            List<Interaction> dataset2 = DataLoader.loadFromFile("data/Dataset_2.txt");
            
            if (dataset1.size() == 50 && dataset2.size() == 50) {
                System.out.println("✓ Test 1: Load datasets - PASSED");
                passed++;
            } else {
                System.out.println("✗ Test 1: Load datasets - FAILED (sizes: " + 
                    dataset1.size() + ", " + dataset2.size() + ")");
            }
        } catch (Exception e) {
            System.out.println("✗ Test 1: Load datasets - EXCEPTION: " + e.getMessage());
        }
        
        // Test 2: Deduplication produces correct count
        total++;
        try {
            List<Interaction> dataset1 = DataLoader.loadFromFile("data/Dataset_1.txt");
            List<Interaction> dataset2 = DataLoader.loadFromFile("data/Dataset_2.txt");
            
            List<Interaction> deduplicated = InteractionAnalyzer.deduplicateDatasets(dataset1, dataset2);
            
            // Expected: 100 total - 3 duplicates = 97 unique
            if (deduplicated.size() == 97) {
                System.out.println("✓ Test 2: Deduplication count - PASSED (97 unique)");
                passed++;
            } else {
                System.out.println("✗ Test 2: Deduplication count - FAILED (got " + 
                    deduplicated.size() + ", expected 97)");
            }
        } catch (Exception e) {
            System.out.println("✗ Test 2: Deduplication count - EXCEPTION: " + e.getMessage());
        }
        
        // Test 3: Known duplicates are removed
        total++;
        try {
            List<Interaction> dataset1 = DataLoader.loadFromFile("data/Dataset_1.txt");
            List<Interaction> dataset2 = DataLoader.loadFromFile("data/Dataset_2.txt");
            
            List<Interaction> deduplicated = InteractionAnalyzer.deduplicateDatasets(dataset1, dataset2);
            
            // Count occurrences of known duplicate interactions
            // I016 = I053, I025 = I055, I043 = I081
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
            
            if (i016Count == 1 && i025Count == 1 && i043Count == 1) {
                System.out.println("✓ Test 3: Known duplicates removed - PASSED");
                passed++;
            } else {
                System.out.println("✗ Test 3: Known duplicates removed - FAILED");
                System.out.println("  I016/I053 count: " + i016Count + " (expected 1)");
                System.out.println("  I025/I055 count: " + i025Count + " (expected 1)");
                System.out.println("  I043/I081 count: " + i043Count + " (expected 1)");
            }
        } catch (Exception e) {
            System.out.println("✗ Test 3: Known duplicates removed - EXCEPTION: " + e.getMessage());
        }
        
        // Test 4: No data loss - all unique records preserved
        total++;
        try {
            List<Interaction> dataset1 = DataLoader.loadFromFile("data/Dataset_1.txt");
            List<Interaction> dataset2 = DataLoader.loadFromFile("data/Dataset_2.txt");
            
            List<Interaction> deduplicated = InteractionAnalyzer.deduplicateDatasets(dataset1, dataset2);
            
            // Check that specific unique IDs are still present
            boolean hasI001 = deduplicated.stream().anyMatch(i -> i.getInteractionId().equals("I001"));
            boolean hasI050 = deduplicated.stream().anyMatch(i -> i.getInteractionId().equals("I050"));
            boolean hasI051 = deduplicated.stream().anyMatch(i -> i.getInteractionId().equals("I051"));
            boolean hasI100 = deduplicated.stream().anyMatch(i -> i.getInteractionId().equals("I100"));
            
            if (hasI001 && hasI050 && hasI051 && hasI100) {
                System.out.println("✓ Test 4: No data loss - PASSED");
                passed++;
            } else {
                System.out.println("✗ Test 4: No data loss - FAILED");
            }
        } catch (Exception e) {
            System.out.println("✗ Test 4: No data loss - EXCEPTION: " + e.getMessage());
        }
        
        // Test 5: Empty dataset handling
        total++;
        try {
            List<Interaction> dataset1 = DataLoader.loadFromFile("data/Dataset_1.txt");
            List<Interaction> emptyList = List.of();
            
            List<Interaction> deduplicated = InteractionAnalyzer.deduplicateDatasets(dataset1, emptyList);
            
            if (deduplicated.size() == 50) {
                System.out.println("✓ Test 5: Empty dataset handling - PASSED");
                passed++;
            } else {
                System.out.println("✗ Test 5: Empty dataset handling - FAILED");
            }
        } catch (Exception e) {
            System.out.println("✗ Test 5: Empty dataset handling - EXCEPTION: " + e.getMessage());
        }
        
        System.out.println("\n=================================");
        System.out.println("Tests Passed: " + passed + "/" + total);
        System.out.println("=================================");
        
        System.out.println("\n=== Deduplication Details ===");
        try {
            List<Interaction> dataset1 = DataLoader.loadFromFile("data/Dataset_1.txt");
            List<Interaction> dataset2 = DataLoader.loadFromFile("data/Dataset_2.txt");
            List<Interaction> deduplicated = InteractionAnalyzer.deduplicateDatasets(dataset1, dataset2);
            
            System.out.println("Dataset 1 size: " + dataset1.size());
            System.out.println("Dataset 2 size: " + dataset2.size());
            System.out.println("Combined total: " + (dataset1.size() + dataset2.size()));
            System.out.println("After deduplication: " + deduplicated.size());
            System.out.println("Duplicates removed: " + ((dataset1.size() + dataset2.size()) - deduplicated.size()));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
