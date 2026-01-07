package org.example;

import org.example.model.Interaction;
import org.example.service.InteractionAnalyzer;
import org.example.util.DataLoader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Main program for the Streaming Platform project.
 * This runs all the different analysis operations.
 * 
 * @author Shauna
 */
public class Main {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  STREAMING PLATFORM ANALYTICS");
        System.out.println("  DSA Project");
        System.out.println("========================================\n");
        
        try {
            // Load the data files
            System.out.println("========================================");
            System.out.println("  LOADING DATA");
            System.out.println("========================================\n");
            
            List<Interaction> dataset1 = DataLoader.loadFromFile("data/Dataset_1.txt");
            List<Interaction> dataset2 = DataLoader.loadFromFile("data/Dataset_2.txt");
            
            System.out.println("\nData loaded!\n");
            
            // Part 1: Analyze the first dataset
            System.out.println("========================================");
            System.out.println("  PART 1: SINGLE DATASET ANALYSIS");
            System.out.println("========================================\n");
            
            InteractionAnalyzer analyzer1 = new InteractionAnalyzer(dataset1);
            
            // Operation 1.2: Show newest to oldest
            System.out.println("----------------------------------------");
            System.out.println("1.2: Reverse Chronological Order");
            System.out.println("----------------------------------------");
            List<Interaction> reverseChronoInteractions = analyzer1.getInteractionsReverseChronological();
            System.out.println("Showing " + reverseChronoInteractions.size() + " interactions (newest to oldest):");
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
            System.out.println("NOTE: Jamie will do this part");
            System.out.println("Should show: U002\n");
            
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
            
            // Part 1.6: Timestamp Range Query (Shauna's implementation)
            System.out.println("----------------------------------------");
            System.out.println("1.6: Time Range Query (08:30-09:00)");
            System.out.println("----------------------------------------");
            LocalDateTime rangeStart = LocalDateTime.of(2025, 11, 23, 8, 30);
            LocalDateTime rangeEnd = LocalDateTime.of(2025, 11, 23, 9, 0);
            List<Interaction> rangeResults = analyzer1.getInteractionsByTimeRange(rangeStart, rangeEnd);
            System.out.println("Found " + rangeResults.size() + " interactions in range:");
            for (Interaction i : rangeResults) {
                System.out.printf("%s\t%s\t%s\t%s\t%s\t%d\n",
                    i.getInteractionId(), i.getUserId(), 
                    i.getTimestamp().format(FORMATTER),
                    i.getActionType(), i.getContentId(), i.getDurationSeconds());
            }
            System.out.println();
            
            // Operation 1.7: Longest span (MY IMPLEMENTATION)
            System.out.println("----------------------------------------");
            System.out.println("1.7: Longest Viewing Span");
            System.out.println("----------------------------------------");
            
            // Need both datasets for complete data
            List<Interaction> allData = new java.util.ArrayList<>();
            allData.addAll(dataset1);
            allData.addAll(dataset2);
            InteractionAnalyzer allAnalyzer = new InteractionAnalyzer(allData);
            
            String longestUser = allAnalyzer.getUserWithLongestViewingSpan();
            long minutes = allAnalyzer.getUserViewingSpan(longestUser);
            
            System.out.println("User: " + longestUser);
            System.out.println("Span: " + minutes + " minutes");
            
            System.out.println("\nAll users:");
            java.util.Set<String> users = allAnalyzer.getAllUsers();
            java.util.List<String> sortedUsers = new java.util.ArrayList<>(users);
            java.util.Collections.sort(sortedUsers);
            
            for (String user : sortedUsers) {
                long span = allAnalyzer.getUserViewingSpan(user);
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
            
            System.out.println("Dataset_1: " + dataset1.size() + " interactions");
            System.out.println("Dataset_2: " + dataset2.size() + " interactions");
            System.out.println("Combined: " + (dataset1.size() + dataset2.size()) + " total interactions\n");
            
            // Deduplicate using static method
            List<Interaction> deduplicatedData = InteractionAnalyzer.deduplicateDatasets(dataset1, dataset2);
            
            System.out.println("After deduplication: " + deduplicatedData.size() + " unique interactions");
            System.out.println("Duplicates removed: " + ((dataset1.size() + dataset2.size()) - deduplicatedData.size()));
            System.out.println("\nKnown duplicate pairs:");
            System.out.println("  I016 (Dataset_1) = I053 (Dataset_2)");
            System.out.println("  I025 (Dataset_1) = I055 (Dataset_2)");
            System.out.println("  I043 (Dataset_1) = I081 (Dataset_2)");
            System.out.println();
            
            // Create analyzer with deduplicated data for next operation
            InteractionAnalyzer multiAnalyzer = new InteractionAnalyzer(deduplicatedData);
            
            // Operation 2.3: Most interactions
            System.out.println("----------------------------------------");
            System.out.println("2.3: User with Most Interactions");
            System.out.println("----------------------------------------");
            
            // Find user with most interactions (with tie-breaking)
            String mostActiveUser = multiAnalyzer.getUserWithMostInteractionsAcrossDatasets();
            java.util.Map<String, Integer> userCounts = multiAnalyzer.getUserInteractionCounts();
            int winnerCount = userCounts.get(mostActiveUser);
            LocalDateTime winnerTimestamp = multiAnalyzer.getUserMostRecentTimestamp(mostActiveUser);
            
            System.out.println("Winner: " + mostActiveUser);
            System.out.println("Interactions: " + winnerCount);
            System.out.println("Most recent activity: " + winnerTimestamp.format(FORMATTER));
            
            // Show tie-breaking details
            System.out.println("\nTied users (all have " + winnerCount + " interactions):");
            java.util.List<String> tiedUsers = java.util.Arrays.asList("U001", "U002", "U003", "U004", "U005");
            for (String user : tiedUsers) {
                if (userCounts.containsKey(user) && userCounts.get(user) == winnerCount) {
                    LocalDateTime timestamp = multiAnalyzer.getUserMostRecentTimestamp(user);
                    System.out.printf("  %s: latest at %s%s\n", 
                        user, timestamp.format(FORMATTER),
                        user.equals(mostActiveUser) ? " ← WINNER (tie-breaker)" : "");
                }
            }
            System.out.println();
            
            // Show other users for comparison
            System.out.println("Other users:");
            java.util.List<String> otherUsers = new java.util.ArrayList<>(userCounts.keySet());
            java.util.Collections.sort(otherUsers);
            for (String user : otherUsers) {
                if (!tiedUsers.contains(user)) {
                    System.out.printf("  %s: %d interactions\n", user, userCounts.get(user));
                }
            }
            System.out.println();
            
            // Done!
            System.out.println("========================================");
            System.out.println("  DONE!");
            System.out.println("========================================\n");
            
            System.out.println("My work (Shauna):");
            System.out.println("  - CustomTreeMap");
            System.out.println("  - Time Range Query");
            System.out.println("  - Longest Viewing Span");
            System.out.println();
            System.out.println("Jamie's work:");
            System.out.println("  - CustomHashMap");
            System.out.println("  - CustomHashSet");
            System.out.println("  - Reverse Chronological");
            System.out.println("  - Most Diverse User");
            System.out.println("  - Remove Duplicates");
            System.out.println("  - Most Interactions");
            System.out.println();
            System.out.println("========================================\n");
            
        } catch (Exception e) {
            System.err.println("\n✗ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}