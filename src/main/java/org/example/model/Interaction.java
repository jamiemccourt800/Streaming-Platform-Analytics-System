package org.example.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Objects;

/**
 * Represents one user interaction.
 * This is like one row from the CSV file.
 * Each interaction has info about what the user did (PLAY, PAUSE, LIKE, etc.).
 * 
 * @author Shauna Mc Guinness
 */
public class Interaction {
    
    // Format for parsing timestamps from CSV (e.g., "2025-11-23 08:01")
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    // Fields matching CSV columns
    private final String interactionId;
    private final String sessionId;
    private final String userId;
    private final LocalDateTime timestamp;
    private final String actionType;
    private final String contentId;
    private final int durationSeconds;
    
    /**
     * Constructor to create an Interaction object.
     * Think of this like filling out a form with all the details.
     */
    public Interaction(String interactionId, String sessionId, String userId, 
                      LocalDateTime timestamp, String actionType, 
                      String contentId, int durationSeconds) {
        this.interactionId = interactionId;
        this.sessionId = sessionId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.actionType = actionType;
        this.contentId = contentId;
        this.durationSeconds = durationSeconds;
    }
    
    /**
     * Creates an Interaction from a CSV line.
     * Like reading one line: "I001,S001,U001,2025-11-23 08:01,PLAY,C001,120"
     * and turning it into an object.
     */
    public static Interaction fromCsvLine(String csvLine) {
        String[] parts = csvLine.split(",");
        
        if (parts.length != 7) {
            throw new IllegalArgumentException(
                "Invalid CSV line - expected 7 fields, got " + parts.length + ": " + csvLine);
        }
        
        try {
            String interactionId = parts[0].trim();
            String sessionId = parts[1].trim();
            String userId = parts[2].trim();
            LocalDateTime timestamp = LocalDateTime.parse(parts[3].trim(), TIMESTAMP_FORMATTER);
            String actionType = parts[4].trim();
            String contentId = parts[5].trim();
            int duration = Integer.parseInt(parts[6].trim());
            
            return new Interaction(interactionId, sessionId, userId, timestamp, 
                                 actionType, contentId, duration);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error parsing CSV line: " + csvLine, e);
        }
    }
    
    // Getters (like asking "What's the userId?")
    
    public String getInteractionId() {
        return interactionId;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getActionType() {
        return actionType;
    }
    
    public String getContentId() {
        return contentId;
    }
    
    public int getDurationSeconds() {
        return durationSeconds;
    }
    
    /**
     * Checks if two interactions are "duplicates".
     * We ignore InteractionID because different datasets might have different IDs
     * but the same actual interaction data.
     * 
     * Like saying: "These two records describe the same event, even if they 
     * have different labels."
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Interaction other = (Interaction) obj;
        
        // Two interactions are duplicates if ALL fields except InteractionID match
        return Objects.equals(sessionId, other.sessionId) &&
               Objects.equals(userId, other.userId) &&
               Objects.equals(timestamp, other.timestamp) &&
               Objects.equals(actionType, other.actionType) &&
               Objects.equals(contentId, other.contentId) &&
               durationSeconds == other.durationSeconds;
    }
    
    /**
     * Hash code for efficient lookup in HashSet/HashMap.
     * Think of this as a fingerprint for the interaction.
     */
    @Override
    public int hashCode() {
        return Objects.hash(sessionId, userId, timestamp, actionType, contentId, durationSeconds);
    }
    
    /**
     * Pretty printing for display.
     * Makes it easy to see what's in an interaction.
     */
    @Override
    public String toString() {
        return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%d",
            interactionId, sessionId, userId, 
            timestamp.format(TIMESTAMP_FORMATTER),
            actionType, contentId, durationSeconds);
    }
    
    /**
     * Get a unique key combining SessionID and InteractionID for internal use.
     */
    public String getUniqueKey() {
        return sessionId + "_" + userId + "_" + timestamp.format(TIMESTAMP_FORMATTER);
    }
    
    // ==================== COMPARATORS ====================
    
    /**
     * Returns a Comparator for sorting interactions from newest to oldest.
     * 
     * @return Comparator that sorts by timestamp in descending order
     */
    public static Comparator<Interaction> reverseChronologicalOrder() {
        return Comparator.comparing(Interaction::getTimestamp).reversed();
    }
}
