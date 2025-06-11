package ir.stts.bajet.db;

import ir.stts.bajet.db.dto.SearchDTO;
import ir.stts.bajet.db.search.SearchManager;
import ir.stts.bajet.db.search.SearchService;
import ir.stts.bajet.db.search.builder.SearchCriteriaBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.Predicate;
import org.hibernate.Session;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaRoot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class SearchManagerTest {

    private EntityManager entityManager;
    private SearchService<Object> searchService;
    private SearchCriteriaBuilder searchCriteriaBuilder;
    private SearchManager<Object> searchManager;

    @BeforeEach
    void setUp() throws Exception {

        AutoCloseable mocks = MockitoAnnotations.openMocks(this);

        entityManager = Mockito.mock(EntityManager.class);
        //noinspection unchecked
        searchService = mock(SearchService.class);
        searchCriteriaBuilder = mock(SearchCriteriaBuilder.class);
        searchManager = new SearchManager<>(entityManager, searchService, searchCriteriaBuilder);

        mocks.close();
    }

    @Test
    void testSearchWithSearchRequest() {
        // Setup for SearchRequest and other mocks
        SearchDTO.SearchRequest searchRequest = new SearchDTO.SearchRequest();
        SearchDTO.QueryContext queryContext = mock(SearchDTO.QueryContext.class);
        Pageable pageable = mock(Pageable.class);

        when(pageable.getOffset()).thenReturn(0L);
        when(pageable.getPageSize()).thenReturn(10);

        when(queryContext.getCriteria()).thenReturn(mock(SearchDTO.SearchCriteria.class));
        when(queryContext.getPageable()).thenReturn(pageable);
        when(queryContext.getDistinct()).thenReturn(false);

        //noinspection unchecked
        when(searchService.search(eq(searchRequest), any(Function.class))).then(invocation -> {
            Function<SearchDTO.QueryContext, Map<Long, List<Object>>> searchFunction = invocation.getArgument(1);
            Map<Long, List<Object>> result = searchFunction.apply(queryContext);
            return new SearchDTO.SearchResponse<>()
                    .setTotalRows(result.keySet().iterator().next())
                    .setData(result.values().iterator().next())
                    .setPageNumber(1)
                    .setTotalPages(2);
        });

        // Mock CriteriaBuilder, CriteriaQuery, and Root
        HibernateCriteriaBuilder criteriaBuilder = mock(HibernateCriteriaBuilder.class);
        JpaCriteriaQuery<Object> criteriaQuery = mock(JpaCriteriaQuery.class);
        JpaRoot<Object> root = mock(JpaRoot.class);
        Predicate predicate = mock(Predicate.class);

        // Ensure the correct sequence of calls: unwrap -> getCriteriaBuilder -> createQuery
        Session mockSession = mock(Session.class);
        when(entityManager.unwrap(Session.class)).thenReturn(mockSession);
        when(mockSession.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Object.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Object.class)).thenReturn(root);
        when(searchCriteriaBuilder.build(any(), eq(criteriaBuilder), eq(root))).thenReturn(predicate);
        when(criteriaQuery.where(predicate)).thenReturn(criteriaQuery);

        // Mock TypedQuery for normal query
        TypedQuery<Object> typedQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>() {{
            add(new Object());
            add(new Object());
            add(new Object());
            add(new Object());
            add(new Object());
            add(new Object());
            add(new Object());
            add(new Object());
            add(new Object());
            add(new Object());
        }});

        // Mock the count query
        JpaCriteriaQuery<Long> countQuery = mock(JpaCriteriaQuery.class);
        JpaRoot<Object> countRoot = mock(JpaRoot.class);
        TypedQuery<Long> countTypedQuery = mock(TypedQuery.class);

        when(criteriaQuery.createCountQuery()).thenReturn(countQuery);
        when(criteriaBuilder.createQuery(Long.class)).thenReturn(countQuery);
        when(countQuery.from(Object.class)).thenReturn(countRoot);
        when(countQuery.select(any())).thenReturn(countQuery);
        when(countQuery.where(predicate)).thenReturn(countQuery);
        when(entityManager.createQuery(countQuery)).thenReturn(countTypedQuery);
        when(countTypedQuery.getSingleResult()).thenReturn(12L);

        // Perform the search operation
        SearchDTO.SearchResponse<Object> response = searchManager.search(searchRequest, Object.class);

        // Assertions
        assertNotNull(response);
        assertEquals(12L, response.getTotalRows());
        assertEquals(1, response.getPageNumber());
        assertEquals(2, response.getTotalPages());
        assertEquals(10, response.getData().size());

        // Verify the correct order of interactions
        verify(entityManager).unwrap(Session.class);  // Ensure unwrap is called first
        verify(mockSession).getCriteriaBuilder();     // Verify getCriteriaBuilder on the Session mock
        verify(searchCriteriaBuilder).build(any(), eq(criteriaBuilder), eq(root));
        verify(typedQuery).setFirstResult(0);
        verify(typedQuery).setMaxResults(10);
    }
}