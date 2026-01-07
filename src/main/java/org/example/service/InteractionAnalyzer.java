package org.example.service;

import org.example.model.Interaction;
import org.example.structure.CustomHashSet;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Analytics engine for analyzing user interactions from streaming platform data.
 * Uses multiple data structures for efficient querying:
 * - CustomHashSet: Removes duplicate interactions (custom implementation)
 * - HashMap: Fast lookups by user and action type (O(1))
 * TODO: Add TreeMap for efficient time-range queries
 */
public class InteractionAnalyzer {
    
    // Core storage - CustomHashSet automatically removes duplicates
    private final CustomHashSet<Interaction> uniqueInteractions;
    
    // Index for fast user lookups - maps userId to their interactions
    private final Map<String, List<Interaction>> userIndex;
    
    // Index for fast action type lookups - maps actionType to matching interactions
    private final Map<String, List<Interaction>> actionIndex;
    
    /**
     * Creates an analyzer and builds all indexes from the provided interactions.
     * This constructor does the heavy lifting of organizing data for fast queries.
     * 
     * @param interactions List of interactions to analyze (may contain duplicates)
     */
    public InteractionAnalyzer(List<Interaction> interactions) {
        // Step 1: Remove duplicates using CustomHashSet
        // Use Math.max to ensure capacity is at least 16 (avoids IllegalArgumentException for empty lists)
        int initialCapacity = Math.max(16, interactions.size());
        this.uniqueInteractions = new CustomHashSet<>(initialCapacity);
        for (Interaction interaction : interactions) {
            uniqueInteractions.add(interaction);
        }
        
        // Step 2: Initialize the index maps
        this.userIndex = new HashMap<>();
        this.actionIndex = new HashMap<>();
        
        // Step 3: Build all indexes by iterating through unique interactions
        for (Interaction interaction : uniqueInteractions) {
            // Add to user index
            userIndex.computeIfAbsent(interaction.getUserId(), k -> new ArrayList<>())
                    .add(interaction);
            
            // Add to action type index
            actionIndex.computeIfAbsent(interaction.getActionType(), k -> new ArrayList<>())
                    .add(interaction);
        }
    }
    
    // ==================== DEDUPLICATION METHOD ====================
    
    /**
     * Deduplicates interactions from two datasets using CustomHashSet.
     * 
     * Deduplication Criteria:
     * Two interactions are considered duplicates if ALL fields match EXCEPT InteractionID:
     * 
     * SessionID must match
     * UserID must match
     * Timestamp must match (exact LocalDateTime)
     * ActionType must match (PLAY, PAUSE, etc.)
     * ContentID must match
     * DurationSeconds must match
     *
     * 
     * Why InteractionID is excluded:
     * Different datasets assign different IDs to the same real-world event. For example:
     * 
     * I016 (Dataset_1) = I053 (Dataset_2) - same interaction, different labels
     * I025 (Dataset_1) = I055 (Dataset_2) - same interaction, different labels
     * I043 (Dataset_1) = I081 (Dataset_2) - same interaction, different labels
     * 
     * 
     * Implementation:
     * Uses {@link org.example.structure.CustomHashSet} which leverages 
     * {@link org.example.model.Interaction#equals(Object)} and 
     * {@link org.example.model.Interaction#hashCode()} for O(1) duplicate detection.
     * The Interaction.equals() method explicitly excludes InteractionID from comparison.
     * 
     * Expected Results:
     * Dataset_1 (50 interactions) + Dataset_2 (50 interactions) = 97 unique interactions
     * (3 duplicates removed: I053, I055, I081)
     * 
     * @param dataset1 First list of interactions (e.g., Dataset_1.txt)
     * @param dataset2 Second list of interactions (e.g., Dataset_2.txt)
     * @return List of unique interactions with duplicates removed (97 for provided datasets)
     * @see org.example.model.Interaction#equals(Object)
     * @see org.example.model.Interaction#hashCode()
     */
    public static List<Interaction> deduplicateDatasets(List<Interaction> dataset1, List<Interaction> dataset2) {
        // Create CustomHashSet with appropriate capacity
        int initialCapacity = Math.max(16, dataset1.size() + dataset2.size());
        CustomHashSet<Interaction> uniqueSet = new CustomHashSet<>(initialCapacity);
        
        // Add all interactions from both datasets
        // CustomHashSet automatically removes duplicates during add()
        for (Interaction interaction : dataset1) {
            uniqueSet.add(interaction);
        }
        for (Interaction interaction : dataset2) {
            uniqueSet.add(interaction);
        }
        
        // Convert CustomHashSet to List for return
        List<Interaction> result = new ArrayList<>();
        for (Interaction interaction : uniqueSet) {
            result.add(interaction);
        }
        
        return result;
    }
    
    // ==================== ACCESSOR METHODS ====================
    
    /**
     * Gets the total count of unique interactions (after removing duplicates).
     * 
     * @return Number of unique interactions
     */
    public int getUniqueInteractionCount() {
        return uniqueInteractions.size();
    }
    
    /**
     * Gets all unique interactions.
     * Returns the internal CustomHashSet for iteration and checking.
     * 
     * @return CustomHashSet of all unique interactions
     */
    public CustomHashSet<Interaction> getUniqueInteractions() {
        return uniqueInteractions;
    }
    
    /**
     * Gets all interactions sorted from newest to oldest (reverse chronological).
     * Converts the unordered CustomHashSet to a sorted List.
     * 
     * @return List of interactions sorted by timestamp in descending order
     */
    public List<Interaction> getInteractionsReverseChronological() {
        // Convert CustomHashSet to List
        List<Interaction> interactions = new ArrayList<>();
        for (Interaction interaction : uniqueInteractions) {
            interactions.add(interaction);
        }
        
        // Sort using the Interaction's built-in comparator (newest to oldest)
        Collections.sort(interactions, Interaction.reverseChronologicalOrder());
        
        return interactions;
    }
    
    /**
     * Gets the count of duplicates that were removed.
     * 
     * @param originalCount The original count before removing duplicates
     * @return Number of duplicate interactions removed
     */
    public int getDuplicateCount(int originalCount) {
        return originalCount - uniqueInteractions.size();
    }
    
    // ==================== FILTERING METHODS ====================
    
    /**
     * Gets all interactions for a specific user.
     * Uses the userIndex HashMap for O(1) lookup speed.
     * 
     * @param userId The user ID to search for (e.g., "U001", "U005")
     * @return List of interactions for this user, or empty list if user not found
     */
    public List<Interaction> getInteractionsByUser(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // HashMap lookup is O(1) - instant retrieval
        List<Interaction> interactions = userIndex.get(userId.trim());
        
        // Return empty list if user not found, otherwise return copy to prevent modification
        return interactions == null ? new ArrayList<>() : new ArrayList<>(interactions);
    }
    
    /**
     * Gets all interactions for a specific action type.
     * Uses the actionIndex HashMap for O(1) lookup speed.
     * 
     * @param actionType The action type to search for (e.g., "play", "pause", "skip")
     * @return List of interactions for this action type, or empty list if not found
     */
    public List<Interaction> getInteractionsByAction(String actionType) {
        if (actionType == null || actionType.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // HashMap lookup is O(1) - instant retrieval
        List<Interaction> interactions = actionIndex.get(actionType.trim());
        
        // Return empty list if action not found, otherwise return copy
        return interactions == null ? new ArrayList<>() : new ArrayList<>(interactions);
    }
    
    // ==================== DISCOVERY METHODS ====================
    
    /**
     * Gets all unique user IDs that appear in the dataset.
     * 
     * @return Set of all user IDs
     */
    public Set<String> getAllUsers() {
        return Collections.unmodifiableSet(userIndex.keySet());
    }
    
    /**
     * Gets all unique action types that appear in the dataset.
     * 
     * @return Set of all action types
     */
    public Set<String> getAllActionTypes() {
        return Collections.unmodifiableSet(actionIndex.keySet());
    }
    
    /**
     * Get all interactions in a time range.
     * This helps find what happened between two times.
     * 
     * @param start Start time
     * @param end End time
     * @return List of interactions in that time period
     */
    public List<Interaction> getInteractionsByTimeRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return new ArrayList<>();
        }
        
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        
        // Make a tree map to store interactions by time
        org.example.datastructures.CustomTreeMap<LocalDateTime, List<Interaction>> timeMap = 
            new org.example.datastructures.CustomTreeMap<>();
        
        // Put all interactions into the time map
        for (Interaction interaction : uniqueInteractions) {
            LocalDateTime time = interaction.getTimestamp();
            
            List<Interaction> list = timeMap.get(time);
            if (list == null) {
                list = new ArrayList<>();
                timeMap.put(time, list);
            }
            list.add(interaction);
        }
        
        // Get just the ones in our time range
        org.example.datastructures.CustomTreeMap<LocalDateTime, List<Interaction>> rangeMap = 
            timeMap.subMap(start, end);
        
        // Put them all in one list
        List<Interaction> results = new ArrayList<>();
        for (LocalDateTime time : rangeMap.keySet()) {
            List<Interaction> list = rangeMap.get(time);
            if (list != null) {
                results.addAll(list);
            }
        }
        
        return results;
    }
    
    /**
     * Find the earliest time in the data.
     * 
     * @return The first timestamp
     */
    public LocalDateTime getEarliestTimestamp() {
        if (uniqueInteractions.isEmpty()) {
            return null;
        }
        
        LocalDateTime earliest = null;
        for (Interaction interaction : uniqueInteractions) {
            LocalDateTime time = interaction.getTimestamp();
            if (earliest == null || time.isBefore(earliest)) {
                earliest = time;
            }
        }
        return earliest;
    }
    
    /**
     * Find the latest time in the data.
     * 
     * @return The last timestamp
     */
    public LocalDateTime getLatestTimestamp() {
        if (uniqueInteractions.isEmpty()) {
            return null;
        }
        
        LocalDateTime latest = null;
        for (Interaction interaction : uniqueInteractions) {
            LocalDateTime time = interaction.getTimestamp();
            if (latest == null || time.isAfter(latest)) {
                latest = time;
            }
        }
        return latest;
    }
    
    // ==================== ANALYTICS & STATISTICS METHODS ====================
    
    /**
     * Calculates comprehensive statistics for a specific user.
     * Returns a map containing:
     * - "interactionCount": Total number of interactions
     * - "timespan": Time difference between earliest and latest interaction (in hours)
     * - "totalDuration": Sum of all interaction durations (in seconds)
     * - "earliestInteraction": Timestamp of first interaction
     * - "latestInteraction": Timestamp of last interaction
     * 
     * @param userId The user ID to analyze
     * @return Map of statistics, or null if user not found
     */
    public Map<String, Object> getUserStatistics(String userId) {
        List<Interaction> userInteractions = getInteractionsByUser(userId);
        
        if (userInteractions.isEmpty()) {
            return null;
        }
        
        Map<String, Object> stats = new HashMap<>();
        
        // Count interactions
        stats.put("interactionCount", userInteractions.size());
        
        // Find earliest and latest timestamps
        LocalDateTime earliest = userInteractions.get(0).getTimestamp();
        LocalDateTime latest = userInteractions.get(0).getTimestamp();
        long totalDuration = 0;
        
        for (Interaction interaction : userInteractions) {
            LocalDateTime timestamp = interaction.getTimestamp();
            if (timestamp.isBefore(earliest)) {
                earliest = timestamp;
            }
            if (timestamp.isAfter(latest)) {
                latest = timestamp;
            }
            totalDuration += interaction.getDurationSeconds();
        }
        
        stats.put("earliestInteraction", earliest);
        stats.put("latestInteraction", latest);
        stats.put("totalDuration", totalDuration);
        
        // Calculate timespan in hours
        long timespanHours = java.time.Duration.between(earliest, latest).toHours();
        stats.put("timespan", timespanHours);
        
        return stats;
    }
    
    /**
     * Aggregates interaction counts for all users in the dataset.
     * Returns a map where each key is a userId and the value is the count
     * of unique interactions for that user (after deduplication).
     * 
     * This method is used for identifying the most active users and
     * handling tie-breaking scenarios in multi-dataset analysis.
     * 
     * Example output for deduplicated datasets:
     * {U001=12, U002=12, U003=12, U004=12, U005=12, U006=10, U007=10, ...}
     * 
     * @return Map of userId to interaction count, or empty map if no data
     */
    public Map<String, Integer> getUserInteractionCounts() {
        Map<String, Integer> counts = new HashMap<>();
        
        // Iterate through userIndex and extract sizes
        for (Map.Entry<String, List<Interaction>> entry : userIndex.entrySet()) {
            String userId = entry.getKey();
            int count = entry.getValue().size();
            counts.put(userId, count);
        }
        
        return counts;
    }
    
    /**
     * Gets the timestamp of the most recent interaction for a specific user.
     * Used for tie-breaking when multiple users have the same interaction count.
     * 
     * The user with the most recent (latest) timestamp is considered more active
     * in tie-breaking scenarios.
     * 
     * Example use case:
     * - U001 has 12 interactions, most recent at 11:01
     * - U005 has 12 interactions, most recent at 11:09
     * - U005 wins the tie-breaker (11:09 > 11:01)
     * 
     * @param userId The user ID to check
     * @return The most recent timestamp for this user, or null if user not found
     */
    public LocalDateTime getUserMostRecentTimestamp(String userId) {
        List<Interaction> userInteractions = getInteractionsByUser(userId);
        
        if (userInteractions.isEmpty()) {
            return null;
        }
        
        LocalDateTime mostRecent = userInteractions.get(0).getTimestamp();
        
        for (Interaction interaction : userInteractions) {
            LocalDateTime timestamp = interaction.getTimestamp();
            if (timestamp.isAfter(mostRecent)) {
                mostRecent = timestamp;
            }
        }
        
        return mostRecent;
    }
    
    /**
     * Finds the user with the most interactions across deduplicated datasets.
     * Handles ties by selecting the user with the most recent interaction timestamp.
     * 
     * Algorithm:
     * 1. Aggregate interaction counts for all users (via getUserInteractionCounts())
     * 2. Find the maximum interaction count
     * 3. Collect all users with that maximum count
     * 4. If only one user has max count, return that user immediately
     * 5. If multiple users tied, compare their most recent timestamps
     * 6. Return the user with the latest (most recent) timestamp
     * 
     * Tie-Breaking Example:
     * After deduplication of Dataset_1 + Dataset_2:
     * - U001: 12 interactions, most recent at 2025-11-23 11:01
     * - U002: 12 interactions, most recent at 2025-11-23 11:03
     * - U003: 12 interactions, most recent at 2025-11-23 11:05
     * - U004: 12 interactions, most recent at 2025-11-23 11:07
     * - U005: 12 interactions, most recent at 2025-11-23 11:09 ← WINNER
     * 
     * Result: U005 (12 interactions, wins tie-breaker with timestamp 11:09)
     * 
     * Complexity:
     * - Time: O(n + m) where n = number of users, m = interactions for tied users
     * - Space: O(n) for storing user counts and tied users list
     * 
     * @return User ID with most interactions (tie-broken by most recent timestamp), 
     *         or null if no data
     */
    public String getUserWithMostInteractionsAcrossDatasets() {
        if (userIndex.isEmpty()) {
            return null;
        }
        
        // Step 1: Get all user interaction counts
        Map<String, Integer> counts = getUserInteractionCounts();
        
        // Step 2: Find the maximum interaction count
        int maxCount = 0;
        for (int count : counts.values()) {
            if (count > maxCount) {
                maxCount = count;
            }
        }
        
        // Step 3: Collect all users with the maximum count (potential ties)
        List<String> tiedUsers = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (entry.getValue() == maxCount) {
                tiedUsers.add(entry.getKey());
            }
        }
        
        // Step 4: If only one user has max count, return immediately
        if (tiedUsers.size() == 1) {
            return tiedUsers.get(0);
        }
        
        // Step 5: Multiple users tied - apply timestamp tie-breaker
        String winnerUser = tiedUsers.get(0);
        LocalDateTime winnerTimestamp = getUserMostRecentTimestamp(winnerUser);
        
        for (int i = 1; i < tiedUsers.size(); i++) {
            String currentUser = tiedUsers.get(i);
            LocalDateTime currentTimestamp = getUserMostRecentTimestamp(currentUser);
            
            // User with more recent timestamp wins
            if (currentTimestamp != null && 
                (winnerTimestamp == null || currentTimestamp.isAfter(winnerTimestamp))) {
                winnerUser = currentUser;
                winnerTimestamp = currentTimestamp;
            }
        }
        
        return winnerUser;
    }
    
    /**
     * Finds the user with the most interactions in the dataset.
     * 
     * @return User ID of the most active user, or null if no data
     */
    public String getMostActiveUser() {
        if (userIndex.isEmpty()) {
            return null;
        }
        
        String mostActiveUser = null;
        int maxInteractions = 0;
        
        // Iterate through all users and find the one with most interactions
        for (Map.Entry<String, List<Interaction>> entry : userIndex.entrySet()) {
            int count = entry.getValue().size();
            if (count > maxInteractions) {
                maxInteractions = count;
                mostActiveUser = entry.getKey();
            }
        }
        
        return mostActiveUser;
    }
    
    /**
     * Counts how many unique action types a specific user has performed.
     * For example, if a user has done "play", "pause", and "skip" actions,
     * this returns 3 (even if they did "play" multiple times).
     * 
     * @param userId The user ID to analyze
     * @return Number of unique action types, or 0 if user not found
     */
    public int countUniqueActions(String userId) {
        List<Interaction> userInteractions = getInteractionsByUser(userId);
        
        if (userInteractions.isEmpty()) {
            return 0;
        }
        
        // Use a HashSet to track unique action types
        Set<String> uniqueActions = new HashSet<>();
        for (Interaction interaction : userInteractions) {
            uniqueActions.add(interaction.getActionType());
        }
        
        return uniqueActions.size();
    }
    
    /**
     * Gets the distribution of action types across all interactions.
     * Returns a map where keys are action types and values are counts.
     * Example: {"play": 45, "pause": 30, "skip": 15, "like": 7}
     * 
     * @return Map of action types to their counts
     */
    public Map<String, Integer> getActionDistribution() {
        Map<String, Integer> distribution = new HashMap<>();
        
        for (Map.Entry<String, List<Interaction>> entry : actionIndex.entrySet()) {
            distribution.put(entry.getKey(), entry.getValue().size());
        }
        
        return distribution;
    }
    
    /**
     * Calculates the total duration (in seconds) of all interactions in the dataset.
     * 
     * @return Total duration in seconds
     */
    public long getTotalDuration() {
        long total = 0;
        
        for (Interaction interaction : uniqueInteractions) {
            total += interaction.getDurationSeconds();
        }
        
        return total;
    }
    
    /**
     * Gets the average duration (in seconds) across all interactions.
     * 
     * @return Average duration in seconds, or 0 if no data
     */
    public double getAverageDuration() {
        if (uniqueInteractions.isEmpty()) {
            return 0.0;
        }
        
        return (double) getTotalDuration() / uniqueInteractions.size();
    }
    
    /**
     * Finds all users who have performed a specific action type.
     * 
     * @param actionType The action type to search for
     * @return Set of user IDs who performed this action
     */
    public Set<String> getUsersByAction(String actionType) {
        List<Interaction> interactions = getInteractionsByAction(actionType);
        
        Set<String> users = new HashSet<>();
        for (Interaction interaction : interactions) {
            users.add(interaction.getUserId());
        }
        
        return users;
    }
    
    /**
     * Find which user watched for the longest time.
     * Longest time = first interaction to last interaction for each user.
     * 
     * @return The user ID with longest span
     */
    public String getUserWithLongestViewingSpan() {
        if (userIndex.isEmpty()) {
            return null;
        }
        
        String winnerUser = null;
        long longestMinutes = -1;
        String mostRecentId = null;
        
        // Check each user
        for (Map.Entry<String, List<Interaction>> entry : userIndex.entrySet()) {
            String userId = entry.getKey();
            List<Interaction> userInteractions = entry.getValue();
            
            if (userInteractions.isEmpty()) {
                continue;
            }
            
            // Find first and last time for this user
            LocalDateTime firstTime = userInteractions.get(0).getTimestamp();
            LocalDateTime lastTime = userInteractions.get(0).getTimestamp();
            String userRecentId = userInteractions.get(0).getInteractionId();
            
            for (Interaction interaction : userInteractions) {
                LocalDateTime time = interaction.getTimestamp();
                String id = interaction.getInteractionId();
                
                if (time.isBefore(firstTime)) {
                    firstTime = time;
                }
                if (time.isAfter(lastTime)) {
                    lastTime = time;
                    userRecentId = id;
                } else if (time.equals(lastTime)) {
                    // Same time? Pick the bigger ID
                    if (id.compareTo(userRecentId) > 0) {
                        userRecentId = id;
                    }
                }
            }
            
            // Calculate minutes between first and last
            long minutes = java.time.Duration.between(firstTime, lastTime).toMinutes();
            
            // Is this user better than our current winner?
            if (minutes > longestMinutes) {
                longestMinutes = minutes;
                winnerUser = userId;
                mostRecentId = userRecentId;
            } else if (minutes == longestMinutes) {
                // Tie! Pick user with more recent ID
                if (userRecentId.compareTo(mostRecentId) > 0) {
                    winnerUser = userId;
                    mostRecentId = userRecentId;
                }
            }
        }
        
        return winnerUser;
    }
    
    /**
     * Get the viewing span for one user in minutes.
     * 
     * @param userId The user to check
     * @return Minutes from first to last interaction
     */
    public long getUserViewingSpan(String userId) {
        List<Interaction> userInteractions = getInteractionsByUser(userId);
        
        if (userInteractions.size() < 2) {
            return 0;
        }
        
        LocalDateTime firstTime = userInteractions.get(0).getTimestamp();
        LocalDateTime lastTime = userInteractions.get(0).getTimestamp();
        
        for (Interaction interaction : userInteractions) {
            LocalDateTime time = interaction.getTimestamp();
            if (time.isBefore(firstTime)) {
                firstTime = time;
            }
            if (time.isAfter(lastTime)) {
                lastTime = time;
            }
        }
        
        return java.time.Duration.between(firstTime, lastTime).toMinutes();
    }
}