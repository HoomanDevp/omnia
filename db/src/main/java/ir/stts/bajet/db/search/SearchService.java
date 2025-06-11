package ir.stts.bajet.db.search;

import ir.stts.bajet.db.dto.SearchDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class SearchService<T> {

    public SearchDTO.SearchResponse<T> search(
            SearchDTO.SearchRequest searchRequest,
            Function<SearchDTO.QueryContext, Map<Long, List<T>>> searchFunction) {

        Pageable pageable = searchRequest.toPageable();
        SearchDTO.QueryContext queryContext = new SearchDTO.QueryContext()
                .setPageable(pageable)
                .setCriteria(searchRequest.getCriteria())
                .setDistinct(searchRequest.getDistinct());
        Map<Long, List<T>> result = searchFunction.apply(queryContext);
        //noinspection OptionalGetWithoutIsPresent
        List<T> data = result.values().stream().findFirst().get();
        long totalRows = result.keySet().stream().findFirst().get();

        SearchDTO.SearchResponse<T> response = new SearchDTO.SearchResponse<>();
        response.setData(data);
        response.setTotalRows(totalRows);
        response.setPageNumber(pageable.getPageNumber() + 1);
        response.setTotalPages((int) Math.ceil((double) totalRows / pageable.getPageSize()));

        return response;
    }

    public <U> SearchDTO.SearchResponse<U> search(
            SearchDTO.SearchRequest searchRequest,
            Function<SearchDTO.QueryContext, Map<Long, List<T>>> searchFunction,
            Function<List<T>, List<U>> converter) {

        Pageable pageable = searchRequest.toPageable();
        SearchDTO.QueryContext queryContext = new SearchDTO.QueryContext()
                .setPageable(pageable)
                .setCriteria(searchRequest.getCriteria())
                .setDistinct(searchRequest.getDistinct());
        Map<Long, List<T>> result = searchFunction.apply(queryContext);
        //noinspection OptionalGetWithoutIsPresent
        List<T> data = result.values().stream().findFirst().get();
        long totalRows = result.keySet().stream().findFirst().get();

        SearchDTO.SearchResponse<U> response = new SearchDTO.SearchResponse<>();
        response.setData(converter.apply(data));
        response.setTotalRows(totalRows);
        response.setPageNumber(pageable.getPageNumber() + 1);
        response.setTotalPages((int) Math.ceil((double) totalRows / pageable.getPageSize()));

        return response;
    }
}