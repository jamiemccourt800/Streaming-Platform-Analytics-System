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
}
