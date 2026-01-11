# Streaming Platform Analytics System

**DKIT - Algorithms & Data Structures Project**  
*Analyzing user interaction data using Java data structures*

## Overview

A Java-based analytics system that processes user interaction data from a streaming platform (like Netflix or Spotify). The system demonstrates efficient use of **custom-implemented** data structures (CustomHashMap, CustomHashSet, CustomTreeMap) to analyze user behavior, detect duplicates, and perform time-based queries across 100+ interaction records.

## Features

- **CSV Data Loading**: Parses interaction data from multiple dataset files
- **Duplicate Detection**: Identifies duplicate records across datasets using CustomHashSet
- **Reverse Chronological Display**: Sort interactions from newest to oldest using custom Comparator
- **User Analytics**: Fast filtering and statistics by UserID using HashMap indexing
- **Action Analysis**: Groups interactions by type (PLAY, PAUSE, LIKE, SKIP, STOP)
- **Time-Range Queries**: Efficient timestamp-based searches using CustomTreeMap (BST)
- **User Statistics**: Calculates interaction counts, timespans, and activity patterns
- **Comprehensive Testing**: 35+ tests across 8 test files validating all operations

## Project Structure

```
dsa-project-shauna-jamie/
├── data/
│   ├── Dataset_1.txt          # First dataset (50 interactions)
│   └── Dataset_2.txt          # Second dataset (50 interactions)
├── src/main/java/org/example/
│   ├── Main.java              # Application entry point - all operations
│   ├── model/
│   │   └── Interaction.java   # Domain model for user interactions
│   ├── datastructures/
│   │   └── CustomTreeMap.java # BST-based sorted map (by Shauna)
│   ├── structure/
│   │   ├── CustomHashMap.java # Hash-based map (by Jamie)
│   │   └── CustomHashSet.java # Hash-based set (by Jamie)
│   ├── util/
│   │   └── DataLoader.java    # CSV parser and file loader
│   └── service/
│       └── InteractionAnalyzer.java # Analytics engine
├── src/test/java/
│   ├── CustomTreeMapTest.java      # BST tests (by Shauna)
│   ├── TimestampRangeTest.java     # Range query tests (by Shauna)
│   ├── LongestSpanTest.java        # Viewing span tests (by Shauna)
│   ├── CustomHashMapTest.java      # Hash map tests (by Shauna)
│   ├── CustomHashSetTest.java      # Hash set tests (by Shauna)
│   ├── DeduplicationTest.java      # Deduplication tests (by Shauna)
│   └── DebugDuplicates.java        # Diagnostic verification tool
└── pom.xml                          # Maven configuration
```

## Technologies & Data Structures

- **Language**: Java 25
- **Build Tool**: Maven
- **Custom Data Structures Implemented**:
  - **CustomTreeMap<K,V>**: Binary Search Tree-based sorted map (by Shauna)
    - Operations: put(), get(), remove(), subMap(), firstKey(), lastKey()
    - Complexity: O(log n) for basic ops, O(log n + k) for range queries
    - Use case: Timestamp-based range queries (Operation 1.6)
  - **CustomHashMap<K,V>**: Hash table with chaining (by Jamie)
    - Operations: put(), get(), remove(), size()
    - Complexity: O(1) average case
    - Features: Automatic resizing at 75% load factor
  - **CustomHashSet<T>**: Hash-based set backed by CustomHashMap (by Jamie)
    - Operations: add(), remove(), contains(), size()
    - Complexity: O(1) average case
    - Use case: Deduplication (Operation 2.2)

## Getting Started

### Prerequisites

- Java 8 or higher
- Maven (optional - can compile with javac)

### Running the Application

```bash
# Compile
mvn clean compile

# Run main program (all operations)
mvn exec:java -Dexec.mainClass="org.example.Main"

# Or use Java directly
java -cp target/classes org.example.Main
```

### Running Tests

```bash
# Run individual test files
java -cp target/classes CustomTreeMapTest
java -cp target/classes TimestampRangeTest
java -cp target/classes LongestSpanTest
java -cp target/classes CustomHashMapTest
java -cp target/classes CustomHashSetTest
java -cp target/classes DeduplicationTest
```

## Dataset Information

- **Total Records**: 100 (50 per dataset)
- **Unique Records**: 97 (after duplicate removal)
- **Users**: U001 - U012
- **Action Types**: PLAY, PAUSE, STOP, LIKE, SKIP
- **Time Range**: 2025-11-23, 08:01 - 11:19
- **Known Duplicates**: 3 records
  - I016 (Dataset_1) = I053 (Dataset_2)
  - I025 (Dataset_1) = I055 (Dataset_2)
  - I043 (Dataset_1) = I081 (Dataset_2)

## Key Implementation Details

### Interaction Model
Each interaction represents a user action with streaming content:
- InteractionID, SessionID, UserID
- Timestamp (LocalDateTime)
- ActionType (PLAY, PAUSE, etc.)
- ContentID, Duration

### Duplicate Detection Logic
Two interactions are considered duplicates if ALL fields match EXCEPT InteractionID (since different datasets may use different IDs for the same event).

### Reverse Chronological Sorting
The system provides timestamp-based sorting to display interactions from newest to oldest:
- **Static Comparator Pattern**: `Interaction.reverseChronologicalOrder()` returns a reusable Comparator
- **Implementation**: Uses `Comparator.comparing(Interaction::getTimestamp).reversed()`
- **Method**: `InteractionAnalyzer.getInteractionsReverseChronological()` converts unordered CustomHashSet to sorted List
- **Performance**: O(n log n) sorting with Collections.sort(), efficient for expected dataset sizes

### User with Most Interactions (Multi-Dataset)
The system identifies the most active user across deduplicated datasets with intelligent tie-breaking:
- **Main Method**: `getUserWithMostInteractionsAcrossDatasets()` - finds user with highest interaction count
- **Aggregation**: `getUserInteractionCounts()` - returns Map of all userId → count pairs
- **Tie-Breaker**: `getUserMostRecentTimestamp(userId)` - finds user's latest interaction timestamp
- **Tie-Breaking Strategy**: When multiple users have equal counts, user with most recent timestamp wins
- **Expected Result**: U005 (12 interactions, timestamp 11:09) defeats U001-U004 (also 12, but earlier timestamps)
- **Performance**: O(n + m) where n = users, m = interactions for tied users

### User with Most Diverse Actions (Single Dataset)
The system identifies the user with the most unique action types in Dataset_1 only, with tie-breaking by earliest interaction:
- **Main Method**: `getUserWithMostDiverseActions()` - finds user with most unique action types
- **Aggregation**: `getUserActionTypeCounts()` - returns Map of all userId → diversity count pairs
- **Tie-Breaker**: `getUserEarliestTimestamp(userId)` - finds user's earliest interaction timestamp
- **Diversity Criteria**: Counts unique action types (PLAY, PAUSE, STOP, LIKE, SKIP), not total actions
- **Tie-Breaking Strategy**: When multiple users have equal diversity counts, user with earliest timestamp wins (rewards pioneers)
- **Expected Result**: U002 (5 unique actions, earliest at 08:03) defeats U003/U005/U009 (also 5, but later timestamps)
- **Performance**: O(n × m) where n = users, m = average interactions per user

```

## Expected Analytics Results

### Part 1 Operations (Dataset_1 only - 50 interactions)
- **Operation 1.2**: Display all 50 interactions in reverse chronological order (newest first)
- **Operation 1.3**: User with most diverse actions - U002 (5 unique action types, earliest at 08:03)
- **Operation 1.4**: Filter by user (e.g., U001 has 7 interactions in Dataset_1)
- **Operation 1.5**: Filter by action type (e.g., PLAY, PAUSE, STOP, LIKE, SKIP)
- **Operation 1.6**: Timestamp range query (08:30-09:00) returns 15 interactions
- **Operation 1.7**: Longest viewing span - U002 (90 minutes: 08:05 to 09:35)

### Part 2 Operations (Combined deduplicated datasets - 97 interactions)
- **Operation 2.2**: Deduplication - 100 total → 97 unique (3 duplicates removed)
  - Duplicates: I016=I053, I025=I055, I043=I081
- **Operation 2.3**: Most active user - U002 (12 interactions, tie-breaker: most recent timestamp)

## Authors

- **Shauna McGuinness** - D00243983
  - CustomTreeMap (BST implementation)
  - Operations 1.6 (timestamp range query) and 1.7 (longest viewing span)
  - Main.java integration
  - Comprehensive test suite (all 8 test files)
  
- **Jamie McCourt** - D00265301
  - CustomHashMap and CustomHashSet implementations
  - Deduplication logic (deduplicateDatasets method)
  - Operations 1.3 and 2.3 core logic

## Project Status

✅ **COMPLETE** - All 9 operations implemented, tested, and validated. Ready for submission.