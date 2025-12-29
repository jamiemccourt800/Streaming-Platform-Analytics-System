import org.example.model.Interaction;
import org.example.service.InteractionAnalyzer;
import org.example.util.DataLoader;

import java.util.List;

/**
 * Test for DSA-7: Longest Viewing Span
 * Tests getUserWithLongestViewingSpan() method
 */
public class LongestSpanTest {
    
    public static void main(String[] args) {
        System.out.println("=== DSA-7: Longest Viewing Span Test ===\n");
        
        try {
            // Load both datasets to test with complete data
            System.out.println("Loading datasets...");
            List<Interaction> dataset1 = DataLoader.loadFromFile("data/Dataset_1.txt");
            List<Interaction> dataset2 = DataLoader.loadFromFile("data/Dataset_2.txt");
            
            // Combine datasets
            List<Interaction> allInteractions = new java.util.ArrayList<>();
            allInteractions.addAll(dataset1);
            allInteractions.addAll(dataset2);
            
            System.out.println("Total interactions: " + allInteractions.size() + "\n");
            
            // Create analyzer
            InteractionAnalyzer analyzer = new InteractionAnalyzer(allInteractions);
            
            // Test: Find user with longest viewing span
            System.out.println("Finding user with longest viewing span...\n");
            
            String longestSpanUser = analyzer.getUserWithLongestViewingSpan();
            
            System.out.println("Result: " + longestSpanUser);
            System.out.println("Expected: U002\n");
            
            // Display spans for all users to verify
            System.out.println("Viewing spans for all users:");
            System.out.println("User\tSpan (minutes)");
            System.out.println("----\t--------------");
            
            java.util.Set<String> allUsers = analyzer.getAllUsers();
            java.util.List<String> sortedUsers = new java.util.ArrayList<>(allUsers);
            java.util.Collections.sort(sortedUsers);
            
            for (String userId : sortedUsers) {
                long span = analyzer.getUserViewingSpan(userId);
                System.out.println(userId + "\t" + span);
            }
            
            System.out.println("\nExpected results:");
            System.out.println("U001: 140 minutes");
            System.out.println("U002: 180 minutes ← WINNER");
            System.out.println("U003: 178 minutes");
            System.out.println("U004: 176 minutes");
            System.out.println("U005: 172 minutes");
            System.out.println("U006: 170 minutes");
            System.out.println("U007: 170 minutes");
            System.out.println("U008: 160 minutes");
            System.out.println("U009: 160 minutes");
            System.out.println("U010: 160 minutes");
            
            // Verify the result
            if ("U002".equals(longestSpanUser)) {
                long u002Span = analyzer.getUserViewingSpan("U002");
                System.out.println("\n✓ CORRECT! U002 has longest span: " + u002Span + " minutes");
                System.out.println("=== DSA-7 TEST PASSED! ✓ ===");
            } else {
                System.out.println("\n✗ INCORRECT! Expected U002 but got " + longestSpanUser);
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
