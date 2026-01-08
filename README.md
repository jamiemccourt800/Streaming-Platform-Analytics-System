# Streaming Platform Analytics System

**DKIT - Algorithms & Data Structures Project**  
*Analyzing user interaction data using Java data structures*

## Overview

A Java-based analytics system that processes user interaction data from a streaming platform (like Netflix or Spotify). The system demonstrates efficient use of core data structures (HashMap, HashSet, TreeMap, ArrayList) to analyze user behavior, detect duplicates, and perform time-based queries across 100+ interaction records.

## Features

- **CSV Data Loading**: Parses interaction data from multiple dataset files
- **Duplicate Detection**: Identifies duplicate records across datasets using HashSet
- **Reverse Chronological Display**: Sort interactions from newest to oldest using custom Comparator
- **User Analytics**: Fast filtering and statistics by UserID using HashMap
- **Action Analysis**: Groups interactions by type (PLAY, PAUSE, LIKE, SKIP, STOP)
- **Time-Range Queries**: Efficient timestamp-based searches using TreeMap
- **User Statistics**: Calculates interaction counts, timespans, and activity patterns

## Project Structure

```
dsa-project-shauna-jamie/
├── data/
│   ├── Dataset_1.txt          # First dataset (50 interactions)
│   └── Dataset_2.txt          # Second dataset (50 interactions)
├── src/main/java/org/example/
│   ├── model/
│   │   └── Interaction.java   # Domain model for user interactions
│   ├── util/
│   │   └── DataLoader.java    # CSV parser and file loader
│   └── service/
│       └── (analytics engine - coming soon)
└── pom.xml                     # Maven configuration
```

## Technologies & Data Structures

- **Language**: Java 24
- **Build Tool**: Maven
- **Data Structures Used**:

## Getting Started

### Prerequisites

- Java 8 or higher
- Maven (optional - can compile with javac)

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

- **User with most interactions**: U005 (12 interactions, latest at 11:09)
- **Longest activity timespan**: U002 (180 minutes: 08:03 to 11:03)
- **Most diverse user** (action types): U002, U003, U005, U009 (5 unique actions)

## Authors

- **Jamie** - D00265301
- **Shauna** - D00243983

## Project Status

**In Development** - Core data structures complete, analytics engine in progress