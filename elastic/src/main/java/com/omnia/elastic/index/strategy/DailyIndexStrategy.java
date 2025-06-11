package com.omnia.elastic.index.strategy;

import java.time.ZonedDateTime;

public class DailyIndexStrategy implements IndexStrategy {

    @Override
    public String getIndex(Long time, String prefix, String separator) {

        ZonedDateTime from = epochToZonedDateTime(time);
        return prefix + separator + format(from.getYear()) + separator + format(from.getMonthValue()) + separator + format(from.getDayOfMonth());
    }
}