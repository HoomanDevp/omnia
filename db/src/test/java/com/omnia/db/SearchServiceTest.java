package com.omnia.db;

import com.omnia.db.dto.SearchDTO;
import com.omnia.db.search.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SearchServiceTest {

    private SearchService<String> searchService;

    @BeforeEach
    void setUp() {
        searchService = new SearchService<>();
    }

    @Test
    void testSearchWithConversion() {

        long totalRows = 11;
        List<String> fetchedData = List.of(
                "data1", "data2", "data3", "data4", "data5",
                "data6", "data7", "data8", "data9", "data10", "data11");
        List<String> convertedData = List.of(
                "converted1", "converted2", "converted3", "converted4", "converted5",
                "converted6", "converted7", "converted8", "converted9", "converted10", "converted11");

        SearchDTO.SearchRequest searchRequest = new SearchDTO.SearchRequest();
        searchRequest.setPageNumber(3);
        searchRequest.setPageSize(2);
        searchRequest.setSortBy("-field");

        List<String> data = fetchedData.subList(
                (searchRequest.getPageNumber() - 1) * searchRequest.getPageSize(),
                searchRequest.getPageNumber() * searchRequest.getPageSize());
        List<String> cData = convertedData.subList(
                (searchRequest.getPageNumber() - 1) * searchRequest.getPageSize(),
                Math.min((int) totalRows, searchRequest.getPageNumber() * searchRequest.getPageSize()));

        //noinspection unchecked
        Function<List<String>, List<String>> converter = mock(Function.class);
        when(converter.apply(data)).thenReturn(cData);

        //noinspection unchecked
        Function<SearchDTO.QueryContext, Map<Long, List<String>>> searchFunction = mock(Function.class);
        when(searchFunction.apply(any(SearchDTO.QueryContext.class))).thenReturn(new HashMap<>() {{
            put(totalRows, data);
        }});

        SearchDTO.SearchResponse<String> response = searchService.search(searchRequest, searchFunction, converter);

        assertEquals(2, response.getData().size());
        assertEquals(11, response.getTotalRows());
        assertEquals(3, response.getPageNumber());
        assertEquals(6, response.getTotalPages());
        assertEquals(cData, response.getData());
    }

    @Test
    void testSearchWithoutConversion() {

        long totalRows = 11;
        List<String> fetchedData = List.of(
                "data1", "data2", "data3", "data4", "data5",
                "data6", "data7", "data8", "data9", "data10", "data11");

        SearchDTO.SearchRequest searchRequest = new SearchDTO.SearchRequest();
        searchRequest.setPageNumber(1);
        searchRequest.setPageSize(20);

        List<String> data = fetchedData.subList(
                (searchRequest.getPageNumber() - 1) * searchRequest.getPageSize(),
                Math.min((int) totalRows, searchRequest.getPageNumber() * searchRequest.getPageSize()));

        //noinspection unchecked
        Function<SearchDTO.QueryContext, Map<Long, List<String>>> searchFunction = mock(Function.class);
        when(searchFunction.apply(any(SearchDTO.QueryContext.class))).thenReturn(new HashMap<>() {{
            put(totalRows, data);
        }});

        SearchDTO.SearchResponse<String> response = searchService.search(searchRequest, searchFunction);

        assertEquals(11, response.getData().size());
        assertEquals(11, response.getTotalRows());
        assertEquals(1, response.getPageNumber());
        assertEquals(1, response.getTotalPages());
        assertEquals(data, response.getData());
    }

    @Test
    void testSearchEmptyResults() {

        long totalRows = 0;
        SearchDTO.SearchRequest searchRequest = new SearchDTO.SearchRequest();
        searchRequest.setPageNumber(1);
        searchRequest.setPageSize(10);

        //noinspection unchecked
        Function<SearchDTO.QueryContext, Long> countFunction = mock(Function.class);
        when(countFunction.apply(any(SearchDTO.QueryContext.class))).thenReturn(totalRows);

        //noinspection unchecked
        Function<SearchDTO.QueryContext, Map<Long, List<String>>> searchFunction = mock(Function.class);
        when(searchFunction.apply(any(SearchDTO.QueryContext.class))).thenReturn(new HashMap<>() {{
            put(totalRows, new ArrayList<>());
        }});

        SearchDTO.SearchResponse<String> response = searchService.search(searchRequest, searchFunction);

        assertEquals(0, response.getData().size());
        assertEquals(0, response.getTotalRows());
        assertEquals(1, response.getPageNumber());
        assertEquals(0, response.getTotalPages());
    }
}