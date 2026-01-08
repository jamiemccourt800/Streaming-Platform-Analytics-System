package org.example;

import org.example.model.Interaction;
import org.example.service.InteractionAnalyzer;
import org.example.util.DataLoader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Main application for Streaming Platform Analytics.
 * This runs all the operations required for the DSA project.
 * 
 * @author Shauna Mc Guinness
 */
public class Main {
    
    // Date format for displaying timestamps
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    /**
     * Runs the program.
     * 
     * @param args not used
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  STREAMING PLATFORM ANALYTICS");
        System.out.println("  DSA Project");
        System.out.println("========================================\n");
        
        try {
            // ==========================================
            // STEP 1: LOAD DATASETS
            // ==========================================
            System.out.println("========================================");
            System.out.println("  LOADING DATA");
            System.out.println("========================================\n");
            
            // Load both datasets from text files
            List<Interaction> dataset1 = DataLoader.loadFromFile("data/Dataset_1.txt");
            List<Interaction> dataset2 = DataLoader.loadFromFile("data/Dataset_2.txt");
            
            System.out.println("\nData loaded!\n");
            
            // ==========================================
            // PART 1: SINGLE DATASET ANALYSIS
            // ==========================================
            System.out.println("========================================");
            System.out.println("  PART 1: SINGLE DATASET ANALYSIS");
            System.out.println("========================================\n");
            
            // Create analyzer for first dataset
            InteractionAnalyzer analyzer1 = new InteractionAnalyzer(dataset1);
            
            // ==========================================
            // OPERATION 1.2: Reverse Chronological Order
            // ==========================================
            System.out.println("----------------------------------------");
            System.out.println("1.2: Reverse Chronological Order");
            System.out.println("----------------------------------------");
            
            // Get interactions sorted newest to oldest
            List<Interaction> reverseChronoInteractions = analyzer1.getInteractionsReverseChronological();
            System.out.println("Showing " + reverseChronoInteractions.size() + " interactions (newest to oldest):");
            
            // Display first 10 (most recent)
            System.out.println("\nFirst 10 (newest):");
            for (int i = 0; i < Math.min(10, reverseChronoInteractions.size()); i++) {
                Interaction inter = reverseChronoInteractions.get(i);
                System.out.printf("%s\t%s\t%s\t%s\t%s\t%d\n",
                    inter.getInteractionId(), inter.getUserId(),
                    inter.getTimestamp().format(FORMATTER),
                    inter.getActionType(), inter.getContentId(), 
                    inter.getDurationSeconds());
            }
            
            System.out.println("...");
            
            // Display last 3 (oldest)
            System.out.println("Last 3 (oldest):");
            int size = reverseChronoInteractions.size();
            for (int i = Math.max(0, size - 3); i < size; i++) {
                Interaction inter = reverseChronoInteractions.get(i);
                System.out.printf("%s\t%s\t%s\t%s\t%s\t%d\n",
                    inter.getInteractionId(), inter.getUserId(),
                    inter.getTimestamp().format(FORMATTER),
                    inter.getActionType(), inter.getContentId(), 
                    inter.getDurationSeconds());
            }
            System.out.println();
            
            // Operation 1.3: Most diverse user
            System.out.println("----------------------------------------");
            System.out.println("1.3: User with Most Different Actions");
            System.out.println("----------------------------------------");
            
            String mostDiverseUser = analyzer1.getUserWithMostDiverseActions();
            Map<String, Integer> diversityCounts = analyzer1.getUserActionTypeCounts();
            
            if (mostDiverseUser != null) {
                int maxDiversity = diversityCounts.get(mostDiverseUser);
                LocalDateTime earliestTimestamp = analyzer1.getUserEarliestTimestamp(mostDiverseUser);
                
                System.out.println("Winner: " + mostDiverseUser);
                System.out.println("Unique action types: " + maxDiversity);
                System.out.println("Earliest interaction: " + earliestTimestamp.format(FORMATTER));
                
                // Show tied users (if any)
                System.out.println("\nTied users (all have " + maxDiversity + " unique actions):");
                for (Map.Entry<String, Integer> entry : diversityCounts.entrySet()) {
                    if (entry.getValue() == maxDiversity) {
                        LocalDateTime timestamp = analyzer1.getUserEarliestTimestamp(entry.getKey());
                        String tiebreaker = entry.getKey().equals(mostDiverseUser) ? " ← WINNER (tie-breaker)" : "";
                        System.out.println("  " + entry.getKey() + ": earliest at " + 
                                         timestamp.format(FORMATTER) + tiebreaker);
                    }
                }
                
                // Show other users for comparison
                System.out.println("\nOther users:");
                for (Map.Entry<String, Integer> entry : diversityCounts.entrySet()) {
                    if (entry.getValue() < maxDiversity) {
                        System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " unique actions");
                    }
                }
            } else {
                System.out.println("No users found in dataset.");
            }
            System.out.println();
            
            // Operation 1.4: Filter by user
            System.out.println("----------------------------------------");
            System.out.println("1.4: Filter by User (U004)");
            System.out.println("----------------------------------------");
            List<Interaction> userInteractions = analyzer1.getInteractionsByUser("U004");
            System.out.println("Found " + userInteractions.size() + " interactions for U004:");
            for (Interaction i : userInteractions) {
                System.out.printf("%s\t%s\t%s\t%s\t%s\t%d\n",
                    i.getInteractionId(), i.getSessionId(), 
                    i.getTimestamp().format(FORMATTER),
                    i.getActionType(), i.getContentId(), i.getDurationSeconds());
            }
            System.out.println();
            
            // Part 1.5: Filter by Action Type
            System.out.println("----------------------------------------");
            System.out.println("1.5: Filter by Action (SKIP)");
            System.out.println("----------------------------------------");
            List<Interaction> skipInteractions = analyzer1.getInteractionsByAction("SKIP");
            for (Interaction i : skipInteractions) {
                System.out.printf("%s\t%s\t%s\t%s\t%d\n",
                    i.getInteractionId(), i.getUserId(), 
                    i.getTimestamp().format(FORMATTER),
                    i.getContentId(), i.getDurationSeconds());
            }
            System.out.println();
            
            // ==========================================
            // OPERATION 1.6: Timestamp Range Query (MY IMPLEMENTATION)
            // ==========================================
            System.out.println("----------------------------------------");
            System.out.println("1.6: Time Range Query (08:30-09:00)");
            System.out.println("----------------------------------------");
            
            // Define time range for peak hours
            LocalDateTime rangeStart = LocalDateTime.of(2025, 11, 23, 8, 30);
            LocalDateTime rangeEnd = LocalDateTime.of(2025, 11, 23, 9, 0);
            
            // Use CustomTreeMap for efficient range query
            List<Interaction> rangeResults = analyzer1.getInteractionsByTimeRange(rangeStart, rangeEnd);
            System.out.println("Found " + rangeResults.size() + " interactions in range:");
            
            // Display all interactions in the time window
            for (Interaction i : rangeResults) {
                System.out.printf("%s\t%s\t%s\t%s\t%s\t%d\n",
                    i.getInteractionId(), i.getUserId(), 
                    i.getTimestamp().format(FORMATTER),
                    i.getActionType(), i.getContentId(), i.getDurationSeconds());
            }
            System.out.println();
            
            // ==========================================
            // OPERATION 1.7: Longest Viewing Span (MY IMPLEMENTATION)
            // ==========================================
            System.out.println("----------------------------------------");
            System.out.println("1.7: Longest Viewing Span (Dataset 1 Only)");
            System.out.println("----------------------------------------");
            
            // Part 1: Use only Dataset 1 (original log file)
            String longestUser = analyzer1.getUserWithLongestViewingSpan();
            long minutes = analyzer1.getUserViewingSpan(longestUser);
            
            System.out.println("Winner: " + longestUser);
            System.out.println("Span: " + minutes + " minutes");
            System.out.println("(Time between first and last interaction in Dataset 1)");
            
            // Display all user spans for comparison  
            System.out.println("\nAll users in Dataset 1:");
            java.util.Set<String> users = analyzer1.getAllUsers();
            java.util.List<String> sortedUsers = new java.util.ArrayList<>(users);
            java.util.Collections.sort(sortedUsers);
            
            for (String user : sortedUsers) {
                long span = analyzer1.getUserViewingSpan(user);
                System.out.println(user + ": " + span + " minutes");
            }
            System.out.println();
            
            // Part 2
            System.out.println("========================================");
            System.out.println("  PART 2: MULTI-DATASET ANALYSIS");
            System.out.println("========================================\n");
            
            // Operation 2.2: Remove duplicates
            System.out.println("----------------------------------------");
            System.out.println("2.2: Remove Duplicates");
            System.out.println("----------------------------------------");
            System.out.println("NOTE: Jamie will do this part");
            System.out.println("Should find 97 unique interactions\n");
            
            // Operation 2.3: Most interactions
            System.out.println("----------------------------------------");
            System.out.println("2.3: User with Most Interactions");
            System.out.println("----------------------------------------");
            System.out.println("NOTE: Jamie will do this part");
            System.out.println("Should show: U005\n");
            
            // ==========================================
            // SUMMARY: Project Contributions
            // ==========================================
            System.out.println("========================================");
            System.out.println("  PROJECT SUMMARY");
            System.out.println("========================================\n");
            
            System.out.println("✓ COMPLETED BY SHAUNA:");
            System.out.println("  • CustomTreeMap (BST-based sorted map)");
            System.out.println("  • Timestamp Range Query (Operation 1.6)");
            System.out.println("  • Longest Viewing Span (Operation 1.7)");
            System.out.println("  • Main Application");
            System.out.println("  • All Testing");
            System.out.println();
            
            System.out.println("✓ COMPLETED BY JAMIE:");
            System.out.println("  • CustomHashMap (hash-based storage)");
            System.out.println("  • CustomHashSet (unique elements)");
            System.out.println("  • Reverse Chronological Sort (Operation 1.2)");
            System.out.println("  • Most Diverse User (Operation 1.3)");
            System.out.println("  • Deduplication (Operation 2.2)");
            System.out.println("  • Most Interactions User (Operation 2.3)");
            System.out.println();
            
            System.out.println("========================================");
            System.out.println("  ALL OPERATIONS COMPLETE");
            System.out.println("========================================\n");
            
        } catch (Exception e) {
            System.err.println("\n✗ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}