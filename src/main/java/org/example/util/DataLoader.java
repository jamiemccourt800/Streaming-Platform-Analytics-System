package org.example.util;

import org.example.model.Interaction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads interaction data from CSV files.
 * Reads the file and converts each line into an Interaction object.
 * 
 * @author Shauna Mc Guinness
 */
public class DataLoader {
    
    /**
     * Loads interactions from a CSV file.
     * Skips the header line and reads each line into an Interaction object.
     * 
     * @param filePath Path to the CSV file (e.g., "data/Dataset_1.txt")
     * @return List of Interaction objects
     * @throws IOException if file can't be read
     */
    public static List<Interaction> loadFromFile(String filePath) throws IOException {
        List<Interaction> interactions = new ArrayList<>();
        
        // Check if file exists
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IOException("File not found: " + filePath);
        }
        
        System.out.println("Loading data from: " + filePath);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;
            int successCount = 0;
            int errorCount = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                // Skip header line and empty lines
                if (lineNumber == 1 || line.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    Interaction interaction = Interaction.fromCsvLine(line);
                    interactions.add(interaction);
                    successCount++;
                } catch (IllegalArgumentException e) {
                    // Log the error but continue processing
                    System.err.println("Warning: Skipping line " + lineNumber + " - " + e.getMessage());
                    errorCount++;
                }
            }
            
            System.out.println("✓ Successfully loaded " + successCount + " interactions");
            if (errorCount > 0) {
                System.out.println("✗ Skipped " + errorCount + " invalid lines");
            }
        }
        
        return interactions;
    }
    
    /**
     * Loads interactions from multiple CSV files.
     * Like reading multiple pages of a book and combining them.
     * 
     * @param filePaths Array of file paths to load
     * @return Combined list of all interactions from all files
     * @throws IOException if any file can't be read
     */
    public static List<Interaction> loadFromMultipleFiles(String... filePaths) throws IOException {
        List<Interaction> allInteractions = new ArrayList<>();
        
        for (String filePath : filePaths) {
            List<Interaction> fileInteractions = loadFromFile(filePath);
            allInteractions.addAll(fileInteractions);
        }
        
        System.out.println("\nTotal interactions loaded from all files: " + allInteractions.size());
        return allInteractions;
    }
    
    /**
     * Validates that a file exists and is readable.
     * Like checking if a book is on the shelf before trying to read it.
     */
    public static boolean validateFile(String filePath) {
        Path path = Paths.get(filePath);
        
        if (!Files.exists(path)) {
            System.err.println("Error: File does not exist: " + filePath);
            return false;
        }
        
        if (!Files.isReadable(path)) {
            System.err.println("Error: File is not readable: " + filePath);
            return false;
        }
        
        if (Files.isDirectory(path)) {
            System.err.println("Error: Path is a directory, not a file: " + filePath);
            return false;
        }
        
        return true;
    }
    
    /**
     * Quick test method to check if data loading works.
     * Run this to make sure your CSV files are in the right place!
     */
    public static void main(String[] args) {
        System.out.println("=== DataLoader Test ===\n");
        
        // Try to load both dataset files
        String[] files = {"Dataset_1.txt", "Dataset_2.txt"};
        
        for (String file : files) {
            System.out.println("Checking " + file + "...");
            if (validateFile(file)) {
                try {
                    List<Interaction> interactions = loadFromFile(file);
                    System.out.println("Success! Loaded " + interactions.size() + " interactions\n");
                    
                    // Show first 3 interactions as sample
                    if (!interactions.isEmpty()) {
                        System.out.println("Sample data (first 3 rows):");
                        for (int i = 0; i < Math.min(3, interactions.size()); i++) {
                            System.out.println(interactions.get(i));
                        }
                        System.out.println();
                    }
                } catch (IOException e) {
                    System.err.println("Error loading file: " + e.getMessage() + "\n");
                }
            } else {
                System.err.println("File validation failed!\n");
            }
        }
        
        // Try loading both files together
        System.out.println("\nTrying to load both files together...");
        try {
            List<Interaction> allData = loadFromMultipleFiles("Dataset_1.txt", "Dataset_2.txt");
            System.out.println("✓ Combined dataset contains " + allData.size() + " total interactions");
        } catch (IOException e) {
            System.err.println("✗ Error loading files: " + e.getMessage());
        }
    }
}
