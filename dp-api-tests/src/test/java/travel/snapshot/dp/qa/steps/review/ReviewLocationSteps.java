package travel.snapshot.dp.qa.steps.review;

import com.jayway.restassured.response.Response;

import java.util.List;

import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.model.review.model.Location;
import travel.snapshot.dp.qa.model.review.model.Property;
import travel.snapshot.dp.qa.serenity.analytics.AnalyticsBaseSteps;

import static org.junit.Assert.assertEquals;

public class ReviewLocationSteps extends AnalyticsBaseSteps {

    private static final String BASE_PATH_LOCATIONS = "/review/locations";

    public ReviewLocationSteps() {

        super();
        spec.baseUri(PropertiesHelper.getProperty(REVIEW_BASE_URI));
        spec.basePath(BASE_PATH_LOCATIONS);
        spec.parameter("access_token", "aaa");
    }

    public void listOfLocationsIsGot(String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntities(limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);

    }

    public void locationNamesAreInResponseInOrder(List<String> names) {
        Response response = getSessionResponse();
        Location[] locations = response.as(Location[].class);
        int i = 0;
        for (Location l : locations) {
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
        Property[] properties = response.as(Property[].class);
        int i = 0;
        for (Property p : properties) {
            assertEquals("location on index=" + i + " is not expected", ids.get(i), p.getPropertyId());
            i++;
        }
    }
}
