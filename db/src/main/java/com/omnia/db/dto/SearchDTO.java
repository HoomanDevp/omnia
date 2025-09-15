package com.omnia.db.dto;

import com.omnia.core.dto.BaseDto;
import com.omnia.db.search.constant.SearchOperationEnm;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SearchDTO {

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class SearchRequest extends BaseDto {

        private static final Integer DEFAULT_PAGE_SIZE = 25;
        private static final Integer DEFAULT_PAGE_NUMBER = 1;

        private Object sortBy;
        private Integer pageSize;
        private Integer pageNumber;
        private SearchCriteria criteria;
        private Boolean distinct = false;

        public Pageable toPageable() {

            List<SortByRequest> sortByRequests = getSortBy();
            if (pageSize == null && pageNumber == null && (sortByRequests == null || sortByRequests.isEmpty()))
                return Pageable.unpaged();
            else if ((pageSize != null || pageNumber != null) && (sortByRequests == null || sortByRequests.isEmpty())) {

                int size = pageSize != null && pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE;
                int page = pageNumber != null && pageNumber > 0 ? pageNumber : DEFAULT_PAGE_NUMBER;

                return PageRequest.of(page - 1, size);
            } else if (pageSize == null && pageNumber == null) {

                Sort sort = Sort.unsorted();
                for (SortByRequest sortByRequest : sortByRequests) {

                    Sort newSort = Sort.by(sortByRequest.isDescending() ? Sort.Order.desc(sortByRequest.getKey()) : Sort.Order.asc(sortByRequest.getKey()));
                    sort = sort.and(newSort);
                }

                return Pageable.unpaged(sort);
            }

            Sort sort = Sort.unsorted();
            int size = pageSize != null && pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE;
            int page = pageNumber != null && pageNumber > 0 ? pageNumber : DEFAULT_PAGE_NUMBER;
            for (SortByRequest sortByRequest : sortByRequests) {

                Sort newSort = Sort.by(sortByRequest.isDescending() ? Sort.Order.desc(sortByRequest.getKey()) : Sort.Order.asc(sortByRequest.getKey()));
                sort = sort.and(newSort);
            }

            return PageRequest.of(page - 1, size, sort);
        }

        private List<SortByRequest> getSortBy() {

            if (sortBy != null) {

                final List<SortByRequest> sortByRequestList = new ArrayList<>();
                if (sortBy instanceof Collection<?> fieldNames)
                    fieldNames.forEach(fieldName -> sortByRequestList.add(getSortByRequest(fieldName.toString())));
                else {

                    final String fieldName = sortBy.toString();
                    sortByRequestList.add(getSortByRequest(fieldName));
                }

                return sortByRequestList;
            }

            return null;
        }

        private SortByRequest getSortByRequest(String fieldName) {

            final SortByRequest sortByRequest = new SortByRequest();
            if (fieldName.startsWith("-"))
                sortByRequest.setKey(fieldName.substring(1)).setDescending(true);
            else
                sortByRequest.setKey(fieldName);

            return sortByRequest;
        }
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class SearchCriteria extends BaseDto {

        @NotEmpty
        private String key;
        private Object value;
        private SearchOperationEnm operation = SearchOperationEnm.equals;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class SearchResponse<T> extends BaseDto {

        private Long totalRows;
        private Integer pageNumber;
        private Integer totalPages;
        private List<T> data = Collections.emptyList();
    }


    @Getter
    @Setter
    @Accessors(chain = true)
    public static class QueryContext {

        private Object criteria;
        private Boolean distinct;
        private Pageable pageable;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    private static class SortByRequest {

        private String key;
        private boolean descending = false;
    }
}
