package travel.snapshot.dp.qa.junit.helpers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * Java object representing url parameters, response code and custom code.
 *
 * Helps with easier GET requests test data handling.
 */

@Getter
@Setter
@NoArgsConstructor
public class UrlParamsWithResponse {
    private String limit;
    private String cursor;
    private String filter;
    private String sort;
    private String sortDesc;
    private String responseCode;
    private String customCode;

    private UrlParamsWithResponse(Map<String, String> data) {
        limit = data.get("limit");
        cursor = data.get("cursor");
        filter = data.get("filter");
        sort = data.get("sort");
        sortDesc = data.get("sort_desc");
        responseCode = data.get("response_code");
        customCode = data.get("custom_code");
    }

    public static UrlParamsWithResponse createUrlParams(Map<String, String> data){
        return new UrlParamsWithResponse(data);
    }
}
