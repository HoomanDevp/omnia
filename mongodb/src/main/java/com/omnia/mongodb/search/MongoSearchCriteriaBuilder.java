package com.omnia.mongodb.search;

import com.omnia.db.dto.SearchDTO;
import com.omnia.db.search.constant.SearchOperationEnm;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoSearchCriteriaBuilder {

    public Criteria build(SearchDTO.SearchCriteria searchCriteria) {

        if (searchCriteria == null)
            return new Criteria();

        Criteria criteria = null;
        if (searchCriteria.getOperation() == null)
            throw new UnsupportedOperationException("The search operation is undefined.");
        switch (searchCriteria.getOperation()) {

            case SearchOperationEnm.and:
                //noinspection unchecked
                criteria = new Criteria().andOperator(
                        ((List<SearchDTO.SearchCriteria>) searchCriteria.getValue()).stream()
                                .map(this::build)
                                .toArray(Criteria[]::new)
                );
                break;
            case SearchOperationEnm.or:
                //noinspection unchecked
                criteria = new Criteria().orOperator(
                        ((List<SearchDTO.SearchCriteria>) searchCriteria.getValue()).stream()
                                .map(this::build)
                                .toArray(Criteria[]::new)
                );
                break;
            case SearchOperationEnm.equals:
                criteria = Criteria.where(searchCriteria.getKey()).is(searchCriteria.getValue());
                break;
            case SearchOperationEnm.notEqual:
                criteria = Criteria.where(searchCriteria.getKey()).ne(searchCriteria.getValue());
                break;
            case SearchOperationEnm.greaterThan:
                criteria = Criteria.where(searchCriteria.getKey()).gt(searchCriteria.getValue());
                break;
            case SearchOperationEnm.greaterOrEqual:
                criteria = Criteria.where(searchCriteria.getKey()).gte(searchCriteria.getValue());
                break;
            case SearchOperationEnm.lessThan:
                criteria = Criteria.where(searchCriteria.getKey()).lt(searchCriteria.getValue());
                break;
            case SearchOperationEnm.lessOrEqual:
                criteria = Criteria.where(searchCriteria.getKey()).lte(searchCriteria.getValue());
                break;
            case SearchOperationEnm.contains:
                criteria = Criteria.where(searchCriteria.getKey()).regex(".*" + searchCriteria.getValue() + ".*");
                break;
            case SearchOperationEnm.notContains:
                criteria = Criteria.where(searchCriteria.getKey()).not().regex(".*" + searchCriteria.getValue() + ".*");
                break;
            case SearchOperationEnm.startsWith:
                criteria = Criteria.where(searchCriteria.getKey()).regex("^" + searchCriteria.getValue());
                break;
            case SearchOperationEnm.notStartsWith:
                criteria = Criteria.where(searchCriteria.getKey()).not().regex("^" + searchCriteria.getValue());
                break;
            case SearchOperationEnm.endsWith:
                criteria = Criteria.where(searchCriteria.getKey()).regex(searchCriteria.getValue() + "$");
                break;
            case SearchOperationEnm.notEndsWith:
                criteria = Criteria.where(searchCriteria.getKey()).not().regex(searchCriteria.getValue() + "$");
                break;
            case SearchOperationEnm.between:
                //noinspection unchecked
                List<Object> values = (List<Object>) searchCriteria.getValue();
                if (values.size() == 2) {
                    criteria = Criteria.where(searchCriteria.getKey()).gt(values.get(0)).lt(values.get(1));
                }
                break;
            case SearchOperationEnm.betweenInclusive:
                //noinspection unchecked
                List<Object> inclusiveValues = (List<Object>) searchCriteria.getValue();
                if (inclusiveValues.size() == 2) {
                    criteria = Criteria.where(searchCriteria.getKey()).gte(inclusiveValues.get(0)).lte(inclusiveValues.get(1));
                }
                break;
            case SearchOperationEnm.isNull:
                criteria = Criteria.where(searchCriteria.getKey()).is(null);
                break;
            case SearchOperationEnm.notNull:
                criteria = Criteria.where(searchCriteria.getKey()).ne(null);
                break;
            case SearchOperationEnm.inSet:
                //noinspection unchecked
                criteria = Criteria.where(searchCriteria.getKey()).in((List<Object>) searchCriteria.getValue());
                break;
            case SearchOperationEnm.notInSet:
                //noinspection unchecked
                criteria = Criteria.where(searchCriteria.getKey()).nin((List<Object>) searchCriteria.getValue());
                break;
            case SearchOperationEnm.isBlank:
                criteria = Criteria.where(searchCriteria.getKey()).is("");
                break;
            case SearchOperationEnm.notBlank:
                criteria = Criteria.where(searchCriteria.getKey()).ne("");
                break;
            default:
                throw new UnsupportedOperationException("Search operation not supported: " + searchCriteria.getOperation());
        }

        return criteria;
    }
}