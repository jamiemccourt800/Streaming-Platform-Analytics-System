package org.example.service;

import org.example.model.Interaction;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Analytics engine for analyzing user interactions from streaming platform data.
 * Uses multiple data structures for efficient querying:
 * - HashSet: Removes duplicate interactions
 * - HashMap: Fast lookups by user and action type (O(1))
 * - TreeMap: Ordered access by timestamp for range queries (O(log n))
 */
public class InteractionAnalyzer {
    
    // Core storage - HashSet automatically removes duplicates
    private final Set<Interaction> uniqueInteractions;
    
    // Index for fast user lookups - maps userId to their interactions
    private final Map<String, List<Interaction>> userIndex;
    
    // Index for fast action type lookups - maps actionType to matching interactions
    private final Map<String, List<Interaction>> actionIndex;
    
    // Ordered by timestamp for efficient time-range queries
    private final TreeMap<LocalDateTime, List<Interaction>> timeIndex;
    
    /**
     * Creates an analyzer and builds all indexes from the provided interactions.
     * This constructor does the heavy lifting of organizing data for fast queries.
     * 
     * @param interactions List of interactions to analyze (may contain duplicates)
     */
    public InteractionAnalyzer(List<Interaction> interactions) {
        // Step 1: Remove duplicates using HashSet
        this.uniqueInteractions = new HashSet<>(interactions);
        
        // Step 2: Initialize the index maps
        this.userIndex = new HashMap<>();
        this.actionIndex = new HashMap<>();
        this.timeIndex = new TreeMap<>(); // TreeMap keeps timestamps sorted
        
        // Step 3: Build all indexes by iterating through unique interactions
        for (Interaction interaction : uniqueInteractions) {
            // Add to user index
            userIndex.computeIfAbsent(interaction.getUserId(), k -> new ArrayList<>())
                    .add(interaction);
            
            // Add to action type index
            actionIndex.computeIfAbsent(interaction.getActionType(), k -> new ArrayList<>())
                    .add(interaction);
            
            // Add to timestamp index
            timeIndex.computeIfAbsent(interaction.getTimestamp(), k -> new ArrayList<>())
                    .add(interaction);
        }
    }
    
    /**
     * Gets the total count of unique interactions (after removing duplicates).
     * 
     * @return Number of unique interactions
     */
    public int getUniqueInteractionCount() {
        return uniqueInteractions.size();
    }
    
    /**
     * Gets all unique interactions as an unmodifiable set.
     * 
     * @return Set of all unique interactions
     */
    public Set<Interaction> getUniqueInteractions() {
        return Collections.unmodifiableSet(uniqueInteractions);
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
    
    /**
     * Gets all interactions within a specific time range (inclusive).
     * Uses the TreeMap's subMap for efficient range queries - O(log n + k) where k is result size.
     * 
     * @param start Start of time range (inclusive)
     * @param end End of time range (inclusive)
     * @return List of interactions within the time range, sorted by timestamp
     */
    public List<Interaction> getInteractionsByTimeRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return new ArrayList<>();
        }
        
        if (start.isAfter(end)) {
            // Swap if start is after end
            LocalDateTime temp = start;
            start = end;
            end = temp;
        }
        
        List<Interaction> results = new ArrayList<>();
        
        // subMap gets a portion of the TreeMap efficiently - O(log n) to find boundaries
        Map<LocalDateTime, List<Interaction>> rangeMap = timeIndex.subMap(start, true, end, true);
        
        // Collect all interactions from the range
        for (List<Interaction> interactions : rangeMap.values()) {
            results.addAll(interactions);
        }
        
        return results;
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
     * Gets the earliest timestamp in the dataset.
     * 
     * @return Earliest timestamp, or null if no data
     */
    public LocalDateTime getEarliestTimestamp() {
        return timeIndex.isEmpty() ? null : timeIndex.firstKey();
    }
    
    /**
     * Gets the latest timestamp in the dataset.
     * 
     * @return Latest timestamp, or null if no data
     */
    public LocalDateTime getLatestTimestamp() {
        return timeIndex.isEmpty() ? null : timeIndex.lastKey();
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
}
