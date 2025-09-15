package com.omnia.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnia.core.date.DateManager;
import com.omnia.db.dto.SearchDTO;
import com.omnia.db.search.builder.SearchCriteriaBuilder;
import com.omnia.db.search.constant.SearchOperationEnm;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SearchCriteriaBuilderTest {

    private SearchCriteriaBuilder searchCriteriaBuilder;

    @Mock
    private Root<?> root;
    @Mock
    private Predicate mockPredicate;
    @Mock
    private CriteriaBuilder criteriaBuilder;

    @BeforeEach
    void setUp() throws Exception {

        AutoCloseable mocks = MockitoAnnotations.openMocks(this);

        searchCriteriaBuilder = new SearchCriteriaBuilder(new ObjectMapper(), new DateManager());

        mocks.close();
    }

    @Test
    void testBuildEqualsOperation() {

        SearchDTO.SearchCriteria criteria = new SearchDTO.SearchCriteria();
        criteria.setKey("name");
        criteria.setOperation(SearchOperationEnm.equals);
        criteria.setValue("John");
        when(criteriaBuilder.equal(root.get("name"), "John")).thenReturn(mockPredicate);

        Predicate result = searchCriteriaBuilder.build(criteria, criteriaBuilder, root);

        assertNotNull(result);
        verify(criteriaBuilder).equal(root.get("name"), "John");
    }

    @Test
    void testBuildBetweenInclusiveOperation() {

        SearchDTO.SearchCriteria criteria = new SearchDTO.SearchCriteria();
        criteria.setKey("age");
        criteria.setOperation(SearchOperationEnm.betweenInclusive);
        criteria.setValue(Arrays.asList(20, 30));
        when(criteriaBuilder.between(root.get("age"), 20, 30)).thenReturn(mockPredicate);

        Predicate result = searchCriteriaBuilder.build(criteria, criteriaBuilder, root);

        assertNotNull(result);
        verify(criteriaBuilder).between(root.get("age"), 20, 30);
    }

    @Test
    void testBuildAndOperation() {

        SearchDTO.SearchCriteria subCriteria1 = new SearchDTO.SearchCriteria()
                .setKey("age")
                .setOperation(SearchOperationEnm.greaterThan)
                .setValue(18);
        SearchDTO.SearchCriteria subCriteria2 = new SearchDTO.SearchCriteria()
                .setKey("age")
                .setOperation(SearchOperationEnm.lessThan)
                .setValue(60);
        SearchDTO.SearchCriteria criteria = new SearchDTO.SearchCriteria()
                .setKey("and")
                .setOperation(SearchOperationEnm.and)
                .setValue(List.of(subCriteria1, subCriteria2));
        Predicate greaterThanPredicate = Mockito.mock(Predicate.class);
        Predicate lessThanPredicate = Mockito.mock(Predicate.class);
        //noinspection unchecked
        when(root.get("age")).thenReturn(Mockito.mock(Path.class));
        when(criteriaBuilder.greaterThan(root.get("age"), 18)).thenReturn(greaterThanPredicate);
        when(criteriaBuilder.lessThan(root.get("age"), 60)).thenReturn(lessThanPredicate);
        when(criteriaBuilder.and(new Predicate[]{greaterThanPredicate, lessThanPredicate})).thenReturn(mockPredicate);

        Predicate result = searchCriteriaBuilder.build(criteria, criteriaBuilder, root);

        assertNotNull(result);
        assertEquals(mockPredicate, result);
        verify(criteriaBuilder).and(new Predicate[]{greaterThanPredicate, lessThanPredicate});
    }

    @Test
    void testBuildOrOperation() {

        SearchDTO.SearchCriteria subCriteria1 = new SearchDTO.SearchCriteria()
                .setKey("status")
                .setOperation(SearchOperationEnm.equals)
                .setValue("active");
        SearchDTO.SearchCriteria subCriteria2 = new SearchDTO.SearchCriteria()
                .setKey("status")
                .setOperation(SearchOperationEnm.equals)
                .setValue("inactive");
        SearchDTO.SearchCriteria criteria = new SearchDTO.SearchCriteria()
                .setKey("or")
                .setOperation(SearchOperationEnm.or)
                .setValue(List.of(subCriteria1, subCriteria2));
        Predicate activePredicate = Mockito.mock(Predicate.class);
        Predicate inactivePredicate = Mockito.mock(Predicate.class);
        when(criteriaBuilder.equal(root.get("status"), "active")).thenReturn(activePredicate);
        when(criteriaBuilder.equal(root.get("status"), "inactive")).thenReturn(inactivePredicate);
        when(criteriaBuilder.or(new Predicate[]{activePredicate, inactivePredicate})).thenReturn(mockPredicate);

        Predicate result = searchCriteriaBuilder.build(criteria, criteriaBuilder, root);

        assertNotNull(result);
        assertEquals(mockPredicate, result);
        verify(criteriaBuilder).or(new Predicate[]{activePredicate, inactivePredicate});
    }
}