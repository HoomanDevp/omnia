package com.omnia.elastic.index.strategy;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public interface IndexStrategy {

    String getIndex(Long time, String prefix, String separator);

    default String format(int t) {
        return "" + (t < 10 ? "0" + t : t);
    }

    default ZonedDateTime epochToZonedDateTime(Long time) {
        return Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault());
    }
}