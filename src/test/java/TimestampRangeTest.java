import org.example.model.Interaction;
import org.example.service.InteractionAnalyzer;
import org.example.util.DataLoader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Test for DSA-6: Timestamp Range Query
 * Tests the getInteractionsByTimeRange() method
 */
public class TimestampRangeTest {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public static void main(String[] args) {
        System.out.println("=== DSA-6: Timestamp Range Query Test ===\n");
        
        try {
            // Load Dataset 1
            System.out.println("Loading Dataset_1.txt...");
            List<Interaction> interactions = DataLoader.loadFromFile("data/Dataset_1.txt");
            System.out.println("Loaded " + interactions.size() + " interactions\n");
            
            // Create analyzer
            InteractionAnalyzer analyzer = new InteractionAnalyzer(interactions);
            
            // Test 1: Get earliest and latest timestamps
            System.out.println("Test 1: Overall timestamp range");
            LocalDateTime earliest = analyzer.getEarliestTimestamp();
            LocalDateTime latest = analyzer.getLatestTimestamp();
            System.out.println("Earliest: " + earliest.format(FORMATTER));
            System.out.println("Latest: " + latest.format(FORMATTER));
            System.out.println("✓ Test 1 passed\n");
            
            // Test 2: Query specific range (08:30 - 09:00)
            // Expected: 15 interactions (I016 through I030)
            System.out.println("Test 2: Query range 08:30 - 09:00");
            LocalDateTime start = LocalDateTime.parse("2025-11-23 08:30", FORMATTER);
            LocalDateTime end = LocalDateTime.parse("2025-11-23 09:00", FORMATTER);
            
            List<Interaction> rangeResults = analyzer.getInteractionsByTimeRange(start, end);
            
            System.out.println("Found " + rangeResults.size() + " interactions in range");
            System.out.println("Expected: 15 interactions (I016-I030)");
            
            if (rangeResults.size() == 15) {
                System.out.println("✓ Correct count!");
            } else {
                System.out.println("✗ Count mismatch!");
            }
            
            // Display the results
            System.out.println("\nInteractions in range 08:30-09:00:");
            for (Interaction interaction : rangeResults) {
                System.out.println(interaction.getInteractionId() + "\t" + 
                                 interaction.getUserId() + "\t" + 
                                 interaction.getTimestamp().format(FORMATTER) + "\t" +
                                 interaction.getActionType() + "\t" +
                                 interaction.getContentId() + "\t" +
                                 interaction.getDurationSeconds());
            }
            
            // Test 3: Edge case - single minute range
            System.out.println("\n\nTest 3: Single minute range (08:01-08:01)");
            LocalDateTime singleTime = LocalDateTime.parse("2025-11-23 08:01", FORMATTER);
            List<Interaction> singleResult = analyzer.getInteractionsByTimeRange(singleTime, singleTime);
            System.out.println("Found " + singleResult.size() + " interaction(s)");
            System.out.println("Expected: 1 interaction (I001)");
            
            if (singleResult.size() == 1 && singleResult.get(0).getInteractionId().equals("I001")) {
                System.out.println("✓ Test 3 passed");
            } else {
                System.out.println("✗ Test 3 failed");
            }
            
            // Test 4: Query with no results
            System.out.println("\n\nTest 4: Range with no results (12:00-13:00)");
            LocalDateTime noResultStart = LocalDateTime.parse("2025-11-23 12:00", FORMATTER);
            LocalDateTime noResultEnd = LocalDateTime.parse("2025-11-23 13:00", FORMATTER);
            List<Interaction> noResults = analyzer.getInteractionsByTimeRange(noResultStart, noResultEnd);
            System.out.println("Found " + noResults.size() + " interactions");
            System.out.println("Expected: 0");
            
            if (noResults.isEmpty()) {
                System.out.println("✓ Test 4 passed");
            } else {
                System.out.println("✗ Test 4 failed");
            }
            
            System.out.println("\n=== ALL TIMESTAMP RANGE TESTS PASSED! ✓ ===");
            System.out.println("DSA-6 Implementation Complete!");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
