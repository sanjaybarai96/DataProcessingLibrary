package com.here.dataprocessing;

public record EventStatistics(long count, long minTimestamp,long maxTimestamp, double mean) {
}
