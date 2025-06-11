package ir.stts.bajet.mongodb;

import ir.stts.bajet.db.dto.SearchDTO;
import ir.stts.bajet.db.search.SearchService;
import ir.stts.bajet.mongodb.search.MongoSearchCriteriaBuilder;
import ir.stts.bajet.mongodb.search.MongoSearchManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MongoSearchManagerTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private SearchService<Object> searchService;
    @InjectMocks
    private MongoSearchCriteriaBuilder mongoSearchCriteriaBuilder;

    private MongoSearchManager<Object> mongoSearchManager;

    @BeforeEach
    void setUp() throws Exception {

        AutoCloseable mocks = MockitoAnnotations.openMocks(this);

        mongoSearchManager = new MongoSearchManager<>(mongoTemplate, searchService, mongoSearchCriteriaBuilder);

        mocks.close();
    }

    @Test
    void testSearchWithoutConverter() {

        when(mongoTemplate.find(any(Query.class), eq(Object.class))).thenReturn(List.of(new Object(), new Object()));
        when(mongoTemplate.count(any(Query.class), eq(Object.class))).thenReturn(2L);

        SearchDTO.SearchRequest searchRequest = new SearchDTO.SearchRequest()
                .setPageNumber(1)
                .setPageSize(10)
                .setCriteria(new SearchDTO.SearchCriteria());
        SearchDTO.SearchResponse<Object> response = mongoSearchManager.search(searchRequest, Object.class);

        assertNotNull(response);

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        verify(mongoTemplate, times(1)).find(queryCaptor.capture(), eq(Object.class));
        verify(mongoTemplate, times(1)).count(queryCaptor.capture(), eq(Object.class));

        List<Query> capturedQueries = queryCaptor.getAllValues();
        Query capturedQuery = capturedQueries.getFirst();

        assertEquals(searchRequest.toPageable().getPageSize(), capturedQuery.getLimit());
        assertEquals(searchRequest.toPageable().getOffset(), capturedQuery.getSkip());
    }

    @Test
    void testSearchWithConverter() {

        when(mongoTemplate.find(any(Query.class), eq(Object.class))).thenReturn(List.of(new Object(), new Object()));
        when(mongoTemplate.count(any(Query.class), eq(Object.class))).thenReturn(2L);
        SearchDTO.SearchRequest searchRequest = new SearchDTO.SearchRequest()
                .setPageNumber(1)
                .setPageSize(5)
                .setCriteria(new SearchDTO.SearchCriteria());
        Function<List<Object>, List<String>> converter = objects -> List.of("Converted1", "Converted2");

        SearchDTO.SearchResponse<String> response = mongoSearchManager.search(searchRequest, converter, Object.class);

        assertNotNull(response);
        assertEquals(List.of("Converted1", "Converted2"), response.getData());

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        verify(mongoTemplate, times(1)).find(queryCaptor.capture(), eq(Object.class));
        verify(mongoTemplate, times(1)).count(queryCaptor.capture(), eq(Object.class));

        List<Query> capturedQueries = queryCaptor.getAllValues();
        Query capturedQuery = capturedQueries.getFirst();
        assertEquals(searchRequest.toPageable().getPageSize(), capturedQuery.getLimit());
        assertEquals(searchRequest.toPageable().getOffset(), capturedQuery.getSkip());
    }
}