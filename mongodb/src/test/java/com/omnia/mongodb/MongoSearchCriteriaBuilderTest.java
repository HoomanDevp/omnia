package com.omnia.mongodb;

import com.omnia.db.dto.SearchDTO;
import com.omnia.db.search.constant.SearchOperationEnm;
import com.omnia.mongodb.search.MongoSearchCriteriaBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MongoSearchCriteriaBuilderTest {

    private final MongoSearchCriteriaBuilder mongoSearchCriteriaBuilder = new MongoSearchCriteriaBuilder();

    @Test
    void testEqualsOperation() {

        SearchDTO.SearchCriteria criteria = new SearchDTO.SearchCriteria()
                .setKey("key")
                .setValue("value")
                .setOperation(SearchOperationEnm.equals);
        Criteria result = mongoSearchCriteriaBuilder.build(criteria);

        assertNotNull(result);
        assertEquals("key", result.getKey());
        assertEquals("value", result.getCriteriaObject().get("key"));
    }

    @Test
    void testNotEqualOperation() {

        SearchDTO.SearchCriteria criteria = new SearchDTO.SearchCriteria()
                .setKey("key")
                .setValue("value")
                .setOperation(SearchOperationEnm.notEqual);
        Criteria result = mongoSearchCriteriaBuilder.build(criteria);

        assertNotNull(result);
        assertEquals("key", result.getKey());
        assertEquals("Document{{$ne=value}}", result.getCriteriaObject().get("key").toString());
    }

    @Test
    void testAndOperation() {

        SearchDTO.SearchCriteria subCriteria1 = new SearchDTO.SearchCriteria()
                .setKey("key1")
                .setValue("value1")
                .setOperation(SearchOperationEnm.equals);
        SearchDTO.SearchCriteria subCriteria2 = new SearchDTO.SearchCriteria()
                .setKey("key2")
                .setValue("value2")
                .setOperation(SearchOperationEnm.equals);
        SearchDTO.SearchCriteria criteria = new SearchDTO.SearchCriteria()
                .setKey("key")
                .setValue(Arrays.asList(subCriteria1, subCriteria2))
                .setOperation(SearchOperationEnm.and);

        Criteria result = mongoSearchCriteriaBuilder.build(criteria);

        assertNotNull(result);
        assertEquals(2, ((List<?>) result.getCriteriaObject().get("$and")).size());
    }

    @Test
    void testOrOperation() {

        SearchDTO.SearchCriteria subCriteria1 = new SearchDTO.SearchCriteria()
                .setKey("key1")
                .setValue("value1")
                .setOperation(SearchOperationEnm.equals);
        SearchDTO.SearchCriteria subCriteria2 = new SearchDTO.SearchCriteria()
                .setKey("key2")
                .setValue("value2")
                .setOperation(SearchOperationEnm.equals);
        SearchDTO.SearchCriteria criteria = new SearchDTO.SearchCriteria()
                .setKey("key")
                .setValue(Arrays.asList(subCriteria1, subCriteria2))
                .setOperation(SearchOperationEnm.or);

        Criteria result = mongoSearchCriteriaBuilder.build(criteria);

        assertNotNull(result);
        assertEquals(2, ((List<?>) result.getCriteriaObject().get("$or")).size());
    }

    @Test
    void testBetweenOperation() {

        SearchDTO.SearchCriteria criteria = new SearchDTO.SearchCriteria()
                .setKey("key")
                .setValue(Arrays.asList(10, 20))
                .setOperation(SearchOperationEnm.between);
        Criteria result = mongoSearchCriteriaBuilder.build(criteria);

        assertNotNull(result);
        assertEquals("Document{{$gt=10, $lt=20}}", result.getCriteriaObject().get("key").toString());
    }

    @Test
    void testEmptyCriteria() {

        Criteria result = mongoSearchCriteriaBuilder.build(null);

        assertNotNull(result);
        assertTrue(result.getCriteriaObject().isEmpty());
    }
}
