package ir.stts.bajet.elastic.index;

import ir.stts.bajet.elastic.index.strategy.IndexStrategy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public abstract class AbstractIndexService implements IndexService {

    private final String separator;
    private final IndexStrategy indexStrategy;

    public abstract String getPrefix();

    @Override
    public List<String> getIndexOf() {
        return getIndexOf(null, null);
    }

    @Override
    public String getIndex(Long time) {
        return indexStrategy.getIndex(time, getPrefix(), separator);
    }

    @Override
    public List<String> getIndexOf(Long from, Long to) {

        if (from == null || to == null) return Collections.singletonList(getPrefix() + WILDCARD);

        if (from.equals(to))
            return Collections.singletonList(getPrefix() + separator + Instant.ofEpochMilli(from).atZone(ZoneId.systemDefault()).toLocalDate() + WILDCARD);

        LocalDateTime startLocalDateTime = Instant.ofEpochMilli(from).atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime endLocalDateTime = Instant.ofEpochMilli(to).atZone(ZoneId.systemDefault()).toLocalDateTime();

        Period period = Period.between(startLocalDateTime.toLocalDate(), endLocalDateTime.toLocalDate());
        if (period.getYears() > 0)
            return overThanYear(startLocalDateTime, endLocalDateTime);

        if (period.getMonths() > 0)
            return overThanMonth(startLocalDateTime, endLocalDateTime);

        return lowerThanMonth(startLocalDateTime, endLocalDateTime);

    }

    private List<String> lowerThanMonth(LocalDateTime start, LocalDateTime end) {

        Set<String> indices = new TreeSet<>();

        if (start.getYear() == end.getYear()) {
            if (start.getMonthValue() == end.getMonthValue())
                for (int day = end.getDayOfMonth(); day >= start.getDayOfMonth(); day--)
                    indices.add(dayIndexWithWildCard(start.getYear(), start.getMonthValue(), day));
            else
                return overThanMonth(start, end);
        } else
            return overThanYear(start, end);

        return Stream.of(indices).flatMap(Collection::stream).collect(Collectors.toList());
    }

    private List<String> overThanMonth(LocalDateTime start, LocalDateTime end) {

        Set<String> indices = new TreeSet<>();
        Set<String> monthIndices = new TreeSet<>();
        Set<String> yearIndices = new TreeSet<>();
        if (start.getYear() == end.getYear()) {

            indices.addAll(findStartDayBound(start));

            for (int month = end.getMonthValue() - 1; month > start.getMonthValue(); month--)
                monthIndices.add(monthIndexWithWildCard(start.getYear(), month));

            indices.addAll(findEndBoundDays(end));
        } else
            return overThanYear(start, end);

        return Stream.of(yearIndices, monthIndices, indices).flatMap(Collection::stream).collect(Collectors.toList());
    }


    private List<String> overThanYear(LocalDateTime start, LocalDateTime end) {

        Set<String> indices = new TreeSet<>();
        Set<String> monthIndices = new TreeSet<>();
        Set<String> yearIndices = new TreeSet<>();

        indices.addAll(findStartDayBound(start));
        for (int month = 12; month > start.getMonthValue(); month--)
            monthIndices.add(monthIndexWithWildCard(start.getYear(), month));

        for (int year = start.getYear() + 1; year < end.getYear(); year++)
            yearIndices.add(yearIndexWithWildCard(year));

        for (int month = 1; month < end.getMonthValue(); month++)
            monthIndices.add(monthIndexWithWildCard(end.getYear(), month));

        indices.addAll(findEndBoundDays(end));
        return Stream.of(yearIndices, monthIndices, indices).flatMap(Collection::stream).collect(Collectors.toList());
    }


    private Collection<String> findStartDayBound(LocalDateTime start) {

        Set<String> dayIndices = new TreeSet<>();
        Set<String> monthIndices = new TreeSet<>();
        for (int day = start.toLocalDate().lengthOfMonth(); day >= start.getDayOfMonth(); day--)
            dayIndices.add(dayIndexWithWildCard(start.getYear(), start.getMonthValue(), day));

        if (dayIndices.size() == start.toLocalDate().lengthOfMonth()) {
            monthIndices.add(monthIndexWithWildCard(start.getYear(), start.getMonthValue()));
            return monthIndices;
        } else
            return dayIndices;
    }

    private Collection<String> findEndBoundDays(LocalDateTime end) {

        Set<String> dayIndices = new TreeSet<>();
        Set<String> monthIndices = new TreeSet<>();
        for (int day = 1; day <= end.getDayOfMonth(); day++)
            dayIndices.add(dayIndexWithWildCard(end.getYear(), end.getMonthValue(), day));

        if (dayIndices.size() == end.toLocalDate().lengthOfMonth()) {
            monthIndices.add(monthIndexWithWildCard(end.getYear(), end.getMonthValue()));
            return monthIndices;
        } else
            return dayIndices;
    }

    private String yearIndexWithWildCard(Integer year) {
        return getPrefix() + separator + year + WILDCARD;
    }

    private String monthIndexWithWildCard(Integer year, Integer month) {
        return getPrefix() + separator + year + separator + (month < 10 ? "0" + month : month) + WILDCARD;
    }

    private String dayIndexWithWildCard(Integer year, Integer month, Integer day) {
        return getPrefix() + separator + year + separator + (month < 10 ? "0" + month : month) + separator + (day < 10 ? "0" + day : day) + WILDCARD;
    }

    private String hourIndexWithWildCard(Integer year, Integer month, Integer day, Integer hour) {
        return getPrefix() + separator + year + separator + (month < 10 ? "0" + month : month) + separator + (day < 10 ? "0" + day : day) + separator + (hour < 10 ? "0" + hour : hour) + WILDCARD;
    }
}