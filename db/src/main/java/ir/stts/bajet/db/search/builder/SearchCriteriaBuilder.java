package ir.stts.bajet.db.search.builder;

import ir.stts.bajet.db.dto.SearchDTO;
import ir.stts.bajet.db.search.constant.SearchOperationEnm;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SearchCriteriaBuilder {

    public Predicate build(SearchDTO.SearchCriteria searchCriteria, CriteriaBuilder criteriaBuilder, Root<?> root) {

        if (searchCriteria == null)
            return criteriaBuilder.conjunction();

        Predicate predicate = null;
        if (searchCriteria.getOperation() == null)
            throw new UnsupportedOperationException("The search operation is undefined.");
        switch (searchCriteria.getOperation()) {

            case SearchOperationEnm.and:

                @SuppressWarnings("unchecked")
                List<SearchDTO.SearchCriteria> andCriteria = (List<SearchDTO.SearchCriteria>) searchCriteria.getValue();
                List<Predicate> andPredicates = new ArrayList<>();
                for (SearchDTO.SearchCriteria criteria : andCriteria)
                    andPredicates.add(build(criteria, criteriaBuilder, root));

                predicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[0]));
                break;

            case SearchOperationEnm.or:

                @SuppressWarnings("unchecked")
                List<SearchDTO.SearchCriteria> orCriteria = (List<SearchDTO.SearchCriteria>) searchCriteria.getValue();
                List<Predicate> orPredicates = new ArrayList<>();
                for (SearchDTO.SearchCriteria criteria : orCriteria)
                    orPredicates.add(build(criteria, criteriaBuilder, root));

                predicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[0]));
                break;

            case SearchOperationEnm.equals:

                predicate = criteriaBuilder.equal(root.get(searchCriteria.getKey()), searchCriteria.getValue());
                break;

            case SearchOperationEnm.notEqual:

                predicate = criteriaBuilder.notEqual(root.get(searchCriteria.getKey()), searchCriteria.getValue());
                break;

            case SearchOperationEnm.greaterThan:

                //noinspection unchecked, rawtypes
                predicate = criteriaBuilder.greaterThan(root.get(searchCriteria.getKey()), (Comparable) searchCriteria.getValue());
                break;

            case SearchOperationEnm.greaterOrEqual:

                //noinspection unchecked, rawtypes
                predicate = criteriaBuilder.greaterThanOrEqualTo(root.get(searchCriteria.getKey()), (Comparable) searchCriteria.getValue());
                break;

            case SearchOperationEnm.lessThan:

                //noinspection unchecked, rawtypes
                predicate = criteriaBuilder.lessThan(root.get(searchCriteria.getKey()), (Comparable) searchCriteria.getValue());
                break;

            case SearchOperationEnm.lessOrEqual:

                //noinspection unchecked, rawtypes
                predicate = criteriaBuilder.lessThanOrEqualTo(root.get(searchCriteria.getKey()), (Comparable) searchCriteria.getValue());
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
                if (values.size() == 2)
                    //noinspection unchecked, rawtypes
                    predicate = criteriaBuilder.between(root.get(searchCriteria.getKey()), (Comparable) values.get(0), (Comparable) values.get(1));

                break;
            case SearchOperationEnm.betweenInclusive:

                //noinspection unchecked
                List<Object> inclusiveValues = (List<Object>) searchCriteria.getValue();
                if (inclusiveValues.size() == 2)
                    //noinspection unchecked, rawtypes
                    predicate = criteriaBuilder.between(root.get(searchCriteria.getKey()), (Comparable) inclusiveValues.get(0), (Comparable) inclusiveValues.get(1));

                break;

            case SearchOperationEnm.isNull:

                predicate = criteriaBuilder.isNull(root.get(searchCriteria.getKey()));
                break;

            case SearchOperationEnm.notNull:

                predicate = criteriaBuilder.isNotNull(root.get(searchCriteria.getKey()));
                break;

            case SearchOperationEnm.inSet:

                //noinspection unchecked
                List<Object> inValues = (List<Object>) searchCriteria.getValue();

                predicate = root.get(searchCriteria.getKey()).in(inValues);
                break;

            case SearchOperationEnm.notInSet:

                //noinspection unchecked
                List<Object> notInValues = (List<Object>) searchCriteria.getValue();

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
}