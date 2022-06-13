package io.camunda.operate.search;

import java.util.ArrayList;
import java.util.List;

import io.camunda.operate.exception.OperateException;

public class SearchQuery {
    private Filter filter;
    private Integer size;
    private List<Sort> sort;

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<Sort> getSort() {
        return sort;
    }

    public void setSort(List<Sort> sort) {
        this.sort = sort;
    }

    public static class Builder {

        private Filter filter;
        private Integer size;
        private List<Sort> sorts = new ArrayList<>();

        public Builder() {

        }

        public Builder withFilter(Filter filter) {
            this.filter = filter;
            return this;
        }

        public Builder withSize(Integer size) {
            this.size = size;
            return this;
        }

        public Builder withSort(Sort sort) {
            this.sorts.add(sort);
            return this;
        }

        public SearchQuery build() throws OperateException {
            SearchQuery query = new SearchQuery();
            query.filter = filter;
            query.size = size;
            if (!sorts.isEmpty()) {
                query.setSort(sorts);
            }
            return query;
        }
    }
}
