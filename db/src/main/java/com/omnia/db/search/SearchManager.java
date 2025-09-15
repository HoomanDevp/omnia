package com.omnia.db.search;

import com.omnia.db.dto.SearchDTO;
import com.omnia.db.search.builder.SearchCriteriaBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class
SearchManager<T> {

    private final EntityManager entityManager;
    private final SearchService<T> searchService;
    private final SearchCriteriaBuilder searchCriteriaBuilder;

    public SearchDTO.SearchResponse<T> search(SearchDTO.SearchRequest searchRequest, Class<T> clazz) {

        return searchService.search(searchRequest, q -> search(q, clazz));
    }

    public <U> SearchDTO.SearchResponse<U> search(SearchDTO.SearchRequest searchRequest, Function<List<T>, List<U>> converter, Class<T> clazz) {

        return searchService.search(searchRequest, q -> search(q, clazz), converter);
    }

    private Map<Long, List<T>> search(SearchDTO.QueryContext context, Class<T> clazz) {

        HibernateCriteriaBuilder criteriaBuilder = entityManager.unwrap(Session.class).getCriteriaBuilder();
        JpaCriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
        Root<T> root = criteriaQuery.from(clazz);

        Predicate predicate = searchCriteriaBuilder.build((SearchDTO.SearchCriteria) context.getCriteria(), criteriaBuilder, root);

        criteriaQuery.where(predicate);

        if (context.getDistinct())
            criteriaQuery.distinct(true);
        Sort sort = context.getPageable().getSort();
        sort.stream().forEach(order -> {
            if (order.isAscending())
                criteriaQuery.orderBy(criteriaBuilder.asc(root.get(order.getProperty())));
            else
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get(order.getProperty())));
        });
        TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult((int) context.getPageable().getOffset());
        typedQuery.setMaxResults(context.getPageable().getPageSize());

        List<T> data = typedQuery.getResultList();

        // Total count of results
        Long totalRows = entityManager.createQuery(criteriaQuery.createCountQuery()).getSingleResult();

        return new HashMap<>() {{
            put(totalRows, data);
        }};
    }
}