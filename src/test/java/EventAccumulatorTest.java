import com.here.dataprocessing.Event;
import com.here.dataprocessing.EventAccumulator;
import com.here.dataprocessing.EventStatistics;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class EventAccumulatorTest {

    @Test
    void shouldReturnEmptyMapForEmptyStream() {

        Map<String, EventStatistics> result =
                EventAccumulator.aggregator(Stream.empty());

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldAggregateSingleEvent() {

        Stream<Event> stream =
                Stream.of(new Event("A", 100, 10));

        Map<String, EventStatistics> result =
                EventAccumulator.aggregator(stream);

        EventStatistics stats = result.get("A");

        assertEquals(1, stats.count());
        assertEquals(100, stats.minTimestamp());
        assertEquals(100, stats.maxTimestamp());
        assertEquals(10, stats.mean());
    }

    @Test
    void shouldIgnoreDuplicateEvents() {

        Stream<Event> stream = Stream.of(
                new Event("A", 100, 10),
                new Event("A", 100, 10), // duplicate
                new Event("A", 120, 20)
        );

        Map<String, EventStatistics> result =
                EventAccumulator.aggregator(stream);

        EventStatistics stats = result.get("A");

        assertEquals(2, stats.count());
        assertEquals(100, stats.minTimestamp());
        assertEquals(120, stats.maxTimestamp());
        assertEquals(15, stats.mean());
    }

    @Test
    void shouldDiscardInvalidEvents() {

        Stream<Event> stream = Stream.of(
                new Event("A", 100, -5),         // invalid
                new Event("A", 120, Double.NaN), // invalid
                new Event("A", 150, 10)          // valid
        );

        Map<String, EventStatistics> result =
                EventAccumulator.aggregator(stream);

        EventStatistics stats = result.get("A");

        assertEquals(1, stats.count());
        assertEquals(150, stats.minTimestamp());
        assertEquals(150, stats.maxTimestamp());
        assertEquals(10, stats.mean());
    }

    @Test
    void shouldAggregateMultipleIds() {

        Stream<Event> stream = Stream.of(
                new Event("A", 100, 10),
                new Event("B", 200, 5),
                new Event("A", 120, 20),
                new Event("B", 250, 15)
        );

        Map<String, EventStatistics> result =
                EventAccumulator.aggregator(stream);

        EventStatistics statsA = result.get("A");
        EventStatistics statsB = result.get("B");

        assertEquals(2, statsA.count());
        assertEquals(100, statsA.minTimestamp());
        assertEquals(120, statsA.maxTimestamp());
        assertEquals(15, statsA.mean());

        assertEquals(2, statsB.count());
        assertEquals(200, statsB.minTimestamp());
        assertEquals(250, statsB.maxTimestamp());
        assertEquals(10, statsB.mean());
    }

    @Test
    void shouldWorkWithParallelStream() {

        Stream<Event> stream = Stream.of(
                new Event("A", 100, 10),
                new Event("A", 120, 20),
                new Event("B", 200, 5),
                new Event("B", 250, 15)
        ).parallel();

        Map<String, EventStatistics> result =
                EventAccumulator.aggregator(stream);

        assertEquals(2, result.get("A").count());
        assertEquals(2, result.get("B").count());
    }
}
