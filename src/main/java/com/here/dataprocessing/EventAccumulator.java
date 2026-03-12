package com.here.dataprocessing;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventAccumulator {


    private record DeduplicationId(String id, long timestamp){}

    public static Map<String, EventStatistics> aggregator(Stream<Event> events){
       Set<DeduplicationId> registeredKey = ConcurrentHashMap.newKeySet();
       return events
               .filter(EventAccumulator::isValid)
               .filter(e -> registeredKey.add(new DeduplicationId(e.id(),e.timestamp())))
               .collect(Collectors.groupingByConcurrent(Event::id,
                       Collector.of(
                             StatsAccumulator::new,
                             StatsAccumulator::add,
                             StatsAccumulator::merge,
                             StatsAccumulator::toStats
                       )));

    }

    public static boolean isValid(Event event){
        return event.value()>0;
    }

}
