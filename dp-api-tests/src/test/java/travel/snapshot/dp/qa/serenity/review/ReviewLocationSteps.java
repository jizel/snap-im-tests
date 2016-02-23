package travel.snapshot.dp.qa.serenity.review;

import com.jayway.restassured.response.Response;
import travel.snapshot.dp.api.review.model.LocationDto;
import travel.snapshot.dp.api.review.model.LocationPropertyDto;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.analytics.AnalyticsBaseSteps;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ReviewLocationSteps extends AnalyticsBaseSteps {

    private static final String BASE_PATH_LOCATIONS = "/review/locations";

    public ReviewLocationSteps() {

        super();
        spec.baseUri(PropertiesHelper.getProperty(REVIEW_BASE_URI));
        spec.basePath(BASE_PATH_LOCATIONS);
    }

    public void listOfLocationsIsGot(String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntities(limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    public void locationNamesAreInResponseInOrder(List<String> names) {
        Response response = getSessionResponse();
        LocationDto[] locations = response.as(LocationDto[].class);
        int i = 0;
        for (LocationDto l : locations) {
            assertEquals("location on index=" + i + " is not expected", names.get(i), l.getLocationName());
            i++;
        }
    }

    public void listOfLocationPropertiesIsGot(String limit, String cursor, String filter, String sort, String sortDesc, String id) {
        Response response = getSecondLevelEntities(id, "properties", limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    public void locationPropertiesAreInResponseInOrder(List<String> ids) {
        Response response = getSessionResponse();
        LocationPropertyDto[] properties = response.as(LocationPropertyDto[].class);
        int i = 0;
        for (LocationPropertyDto p : properties) {
            assertEquals("location on index=" + i + " is not expected", ids.get(i), p.getPropertyId());
            i++;
        }
    }
}
