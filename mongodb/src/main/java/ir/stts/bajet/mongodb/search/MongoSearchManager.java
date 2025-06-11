package ir.stts.bajet.mongodb.search;

import ir.stts.bajet.db.dto.SearchDTO;
import ir.stts.bajet.db.search.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@ConditionalOnBean(MongoTemplate.class)
public class MongoSearchManager<T> {

    private final MongoTemplate mongoTemplate;
    private final SearchService<T> searchService;
    private final MongoSearchCriteriaBuilder mongoSearchCriteriaBuilder;

    public SearchDTO.SearchResponse<T> search(SearchDTO.SearchRequest searchRequest, Class<T> clazz) {

        return searchService.search(searchRequest, q -> search(q, clazz));
    }

    public <U> SearchDTO.SearchResponse<U> search(SearchDTO.SearchRequest searchRequest, Function<List<T>, List<U>> converter, Class<T> clazz) {

        return searchService.search(searchRequest, q -> search(q, clazz), converter);
    }

    private Map<Long, List<T>> search(SearchDTO.QueryContext context, Class<T> clazz) {

        Criteria criteria = mongoSearchCriteriaBuilder.build((SearchDTO.SearchCriteria) context.getCriteria());
        Query query = new Query(criteria);
        query.with(context.getPageable());
        List<T> data = mongoTemplate.find(query, clazz);
        long totalRows = mongoTemplate.count(query, clazz);

        return new HashMap<>() {{
            put(totalRows, data);
        }};
    }
}