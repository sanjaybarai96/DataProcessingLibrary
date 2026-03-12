package com.here.dataprocessing;

public class StatsAccumulator {

    private long count;
    private double sum;
    private long minTimestamp = Long.MAX_VALUE;
    private long maxTimestamp = Long.MIN_VALUE;


    public void add(Event event){
        count ++;
        minTimestamp = Math.min(this.minTimestamp, event.timestamp());
        maxTimestamp = Math.max(this.maxTimestamp, event.timestamp());
        sum += event.value();
    }

    public StatsAccumulator merge(StatsAccumulator other){
        this.count += other.count;
        minTimestamp = Math.min(this.minTimestamp, other.minTimestamp);
        maxTimestamp = Math.max(this.maxTimestamp, other.maxTimestamp);
        this.sum += other.sum;
        return this;
    }

    public EventStatistics toStats() {
        double mean = count == 0 ? 0 : sum / count;
        return new EventStatistics(count, minTimestamp, maxTimestamp, mean);
    }

}
