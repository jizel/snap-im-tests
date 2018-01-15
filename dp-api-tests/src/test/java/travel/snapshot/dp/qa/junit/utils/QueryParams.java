package travel.snapshot.dp.qa.junit.utils;

import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.buildQueryParamMapForPaging;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class QueryParams {

    private String limit;
    private String cursor;
    private String filter;
    private String sort;
    private String sortDesc;
    private Map<String, String> additionalParams;

    public static QueryParamsBuilder builder(){
        return new QueryParamsBuilder();
    }


    public static class QueryParamsBuilder {
        private String limit;
        private String cursor;
        private String filter;
        private String sort;
        private String sortDesc;
        private Map<String, String> additionalParams;


        public QueryParamsBuilder limit(String limit) {
            this.limit = limit;
            return this;
        }

        public QueryParamsBuilder cursor(String cursor) {
            this.cursor = cursor;
            return this;
        }

        public QueryParamsBuilder filter(String filter) {
            this.filter = filter;
            return this;
        }

        public QueryParamsBuilder sort(String sort) {
            this.sort = sort;
            return this;
        }

        public QueryParamsBuilder sortDesc(String sortDesc) {
            this.sortDesc = sortDesc;
            return this;
        }

        public QueryParamsBuilder additionalParams(Map<String, String> additionalParams) {
            this.additionalParams = additionalParams;
            return this;
        }

        public Map<String, String> build() {
            QueryParams queryParams = new QueryParams(limit, cursor, filter, sort, sortDesc, additionalParams);
            return buildQueryParamMapForPaging(
                    queryParams.getLimit(),
                    queryParams.getCursor(),
                    queryParams.getFilter(),
                    queryParams.getSort(),
                    queryParams.getSortDesc(),
                    queryParams.getAdditionalParams()
            );
        }
    }

}
