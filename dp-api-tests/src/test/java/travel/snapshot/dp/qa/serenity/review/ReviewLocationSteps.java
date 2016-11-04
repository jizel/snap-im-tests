package travel.snapshot.dp.qa.serenity.review;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

import com.jayway.restassured.response.Response;
import net.thucydides.core.annotations.Step;
import travel.snapshot.dp.api.review.model.LocationDto;
import travel.snapshot.dp.api.review.model.LocationPropertyDto;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.analytics.AnalyticsBaseSteps;

import java.util.List;

public class ReviewLocationSteps extends AnalyticsBaseSteps {

    private static final String BASE_PATH_LOCATIONS = "/review/locations";

    public ReviewLocationSteps() {
        spec.baseUri(PropertiesHelper.getProperty(REVIEW_BASE_URI));
        spec.basePath(BASE_PATH_LOCATIONS);
    }

    @Step
    public void listOfLocationsIsGot(String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntities(limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    @Step
    public void locationNamesAreInResponseInOrder(List<String> names) {
        Response response = getSessionResponse();
        LocationDto[] locations = response.as(LocationDto[].class);
        int i = 0;
        for (LocationDto location : locations) {
//            assertEquals("location on index=" + i + " is not expected", names.get(i), location.getLocationName());
            assertThat("location on index=" + i + " is not expected", location.getLocationName(), equalTo(names.get(i)));
            i++;
        }
    }

    @Step
    public void listOfLocationPropertiesIsGot(String limit, String cursor, String filter, String sort, String sortDesc, String id) {
        Response response = getSecondLevelEntities(id, "properties", limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    @Step
    public void locationPropertiesAreInResponseInOrder(List<String> ids) {
        Response response = getSessionResponse();
        LocationPropertyDto[] properties = response.as(LocationPropertyDto[].class);
        int i = 0;
        for (LocationPropertyDto locationProperty : properties) {
            assertEquals("location on index=" + i + " is not expected", ids.get(i), locationProperty.getPropertyId());
            i++;
        }
    }
}
