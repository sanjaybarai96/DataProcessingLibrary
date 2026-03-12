# Design Decisions

1. Streaming Processing
   The library processes events directly from the input Stream without materializing them into collections.

2. Parallel Execution
   Aggregation uses groupingByConcurrent and a custom Collector to support safe parallel execution.

3. Deduplication
   Duplicate events are detected using a ConcurrentHashMap-backed Set storing (id,timestamp).

4. Immutable Output
   The final result uses Java records (EventStatistics) to ensure immutability and thread safety.

5. Complexity
   Time Complexity: O(N)
   Memory Complexity: O(unique ids + unique events)


# Entry point of api
The main entry point of the library is the following method:

Map<String, EventStatistics> EventAccumulator.aggregator(Stream<Event> events)