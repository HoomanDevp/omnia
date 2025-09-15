package com.omnia.db.search.builder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnia.core.date.DateManager;
import com.omnia.db.dto.SearchDTO;
import com.omnia.db.search.constant.SearchOperationEnm;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchCriteriaBuilder {

    private final ObjectMapper objectMapper;
    private final DateManager dateManager;

    public Predicate build(SearchDTO.SearchCriteria searchCriteria, CriteriaBuilder criteriaBuilder, Root<?> root) {

        if (searchCriteria == null)
            return criteriaBuilder.conjunction();

        Predicate predicate = null;
        if (searchCriteria.getOperation() == null)
            throw new UnsupportedOperationException("The search operation is undefined.");
        switch (searchCriteria.getOperation()) {

            case SearchOperationEnm.and:

                List<SearchDTO.SearchCriteria> andCriteria = objectMapper.convertValue(
                        searchCriteria.getValue(),
                        new TypeReference<>() {
                        });
                List<Predicate> andPredicates = new ArrayList<>();
                for (SearchDTO.SearchCriteria criteria : andCriteria)
                    andPredicates.add(build(criteria, criteriaBuilder, root));

                predicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[0]));
                break;

            case SearchOperationEnm.or:

                List<SearchDTO.SearchCriteria> orCriteria = objectMapper.convertValue(
                        searchCriteria.getValue(),
                        new TypeReference<>() {
                        });
                List<Predicate> orPredicates = new ArrayList<>();
                for (SearchDTO.SearchCriteria criteria : orCriteria)
                    orPredicates.add(build(criteria, criteriaBuilder, root));

                predicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[0]));
                break;

            case SearchOperationEnm.equals:

                Path<Object> eqPath = root.get(searchCriteria.getKey());
                Object eqValue = convertValueIfNeeded(eqPath, searchCriteria.getValue());
                predicate = criteriaBuilder.equal(eqPath, eqValue);
                break;

            case SearchOperationEnm.notEqual:

                Path<Object> neqPath = root.get(searchCriteria.getKey());
                Object neqValue = convertValueIfNeeded(neqPath, searchCriteria.getValue());
                predicate = criteriaBuilder.notEqual(neqPath, neqValue);
                break;

            case SearchOperationEnm.greaterThan:

                Path<Object> gtPath = root.get(searchCriteria.getKey());
                Object gtValue = convertValueIfNeeded(gtPath, searchCriteria.getValue());
                //noinspection unchecked, rawtypes
                predicate = criteriaBuilder.greaterThan(root.get(searchCriteria.getKey()), (Comparable) gtValue);
                break;

            case SearchOperationEnm.greaterOrEqual:

                Path<Object> gtePath = root.get(searchCriteria.getKey());
                Object gteValue = convertValueIfNeeded(gtePath, searchCriteria.getValue());
                //noinspection unchecked, rawtypes
                predicate = criteriaBuilder.greaterThanOrEqualTo(root.get(searchCriteria.getKey()), (Comparable) gteValue);
                break;

            case SearchOperationEnm.lessThan:

                Path<Object> ltPath = root.get(searchCriteria.getKey());
                Object ltValue = convertValueIfNeeded(ltPath, searchCriteria.getValue());
                //noinspection unchecked, rawtypes
                predicate = criteriaBuilder.lessThan(root.get(searchCriteria.getKey()), (Comparable) ltValue);
                break;

            case SearchOperationEnm.lessOrEqual:
                Path<Object> ltePath = root.get(searchCriteria.getKey());
                Object lteValue = convertValueIfNeeded(ltePath, searchCriteria.getValue());
                //noinspection unchecked, rawtypes
                predicate = criteriaBuilder.lessThanOrEqualTo(root.get(searchCriteria.getKey()), (Comparable) lteValue);
                break;

            case SearchOperationEnm.contains:

                predicate = criteriaBuilder.like(root.get(searchCriteria.getKey()), "%" + searchCriteria.getValue() + "%");
                break;

            case SearchOperationEnm.notContains:

                predicate = criteriaBuilder.not(criteriaBuilder.like(root.get(searchCriteria.getKey()), "%" + searchCriteria.getValue() + "%"));
                break;

            case SearchOperationEnm.startsWith:

                predicate = criteriaBuilder.like(root.get(searchCriteria.getKey()), searchCriteria.getValue() + "%");
                break;

            case SearchOperationEnm.notStartsWith:

                predicate = criteriaBuilder.not(criteriaBuilder.like(root.get(searchCriteria.getKey()), searchCriteria.getValue() + "%"));
                break;

            case SearchOperationEnm.endsWith:

                predicate = criteriaBuilder.like(root.get(searchCriteria.getKey()), "%" + searchCriteria.getValue());
                break;

            case SearchOperationEnm.notEndsWith:

                predicate = criteriaBuilder.not(criteriaBuilder.like(root.get(searchCriteria.getKey()), "%" + searchCriteria.getValue()));
                break;

            case SearchOperationEnm.between:

                //noinspection unchecked
                List<Object> values = (List<Object>) searchCriteria.getValue();
                if (values.size() == 2) {
                    Path<Object> bPath = root.get(searchCriteria.getKey());
                    Object lValue = convertValueIfNeeded(bPath, ((List<?>) searchCriteria.getValue()).getFirst());
                    Object gValue = convertValueIfNeeded(bPath, ((List<?>) searchCriteria.getValue()).getLast());
                    //noinspection unchecked, rawtypes
                    predicate = criteriaBuilder.between(root.get(searchCriteria.getKey()), (Comparable) lValue, (Comparable) gValue);
                } else
                    throw new IllegalArgumentException("The between search operation need 2 values");
                break;
            case SearchOperationEnm.betweenInclusive:

                //noinspection unchecked
                List<Object> inclusiveValues = (List<Object>) searchCriteria.getValue();
                if (inclusiveValues.size() == 2) {
                    Path<Object> biPath = root.get(searchCriteria.getKey());
                    Object lValue = convertValueIfNeeded(biPath, ((List<?>) searchCriteria.getValue()).getFirst());
                    Object gValue = convertValueIfNeeded(biPath, ((List<?>) searchCriteria.getValue()).getLast());
                    //noinspection unchecked, rawtypes
                    predicate = criteriaBuilder.between(root.get(searchCriteria.getKey()), (Comparable) lValue, (Comparable) gValue);
                } else
                    throw new IllegalArgumentException("The betweenInclusive search operation need 2 values");

                break;

            case SearchOperationEnm.isNull:

                predicate = criteriaBuilder.isNull(root.get(searchCriteria.getKey()));
                break;

            case SearchOperationEnm.notNull:

                predicate = criteriaBuilder.isNotNull(root.get(searchCriteria.getKey()));
                break;

            case SearchOperationEnm.inSet:
                Path<Object> inPath = root.get(searchCriteria.getKey());
                //noinspection unchecked
                List<Object> inValues = (List<Object>) searchCriteria.getValue();
                inValues = inValues.stream().map(o -> convertValueIfNeeded(inPath, o)).toList();
                predicate = root.get(searchCriteria.getKey()).in(inValues);
                break;

            case SearchOperationEnm.notInSet:
                Path<Object> notInPath = root.get(searchCriteria.getKey());
                //noinspection unchecked
                List<Object> notInValues = (List<Object>) searchCriteria.getValue();
                notInValues = notInValues.stream().map(o -> convertValueIfNeeded(notInPath, o)).toList();
                predicate = criteriaBuilder.not(root.get(searchCriteria.getKey()).in(notInValues));
                break;

            case SearchOperationEnm.isBlank:

                predicate = criteriaBuilder.equal(root.get(searchCriteria.getKey()), "");
                break;

            case SearchOperationEnm.notBlank:

                predicate = criteriaBuilder.notEqual(root.get(searchCriteria.getKey()), "");
                break;

            default:
                throw new UnsupportedOperationException("Search operation not supported: " + searchCriteria.getOperation());
        }

        return predicate;
    }

    private Object convertValueIfNeeded(Path<Object> path, Object rawValue) {
        if (rawValue == null) {
            return null;
        }

        Class<?> type = path.getJavaType();

        try {
            if (LocalDate.class.isAssignableFrom(type)) {
                return dateManager.persianDateString2LocalDate(rawValue.toString(), "yyyy/MM/dd");
            }
            if (LocalDateTime.class.isAssignableFrom(type)) {
                return dateManager.persianDateString2LocalDateTime(rawValue.toString(), "yyyy/MM/dd HH:mm:ss");
            }
        } catch (ParseException e) {
            throw new IllegalStateException("Failed to parse date value: " + rawValue, e);
        }

        // default: return as is
        return rawValue;
    }
}