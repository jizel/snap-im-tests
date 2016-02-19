package travel.snapshot.dp.qa.steps.review;

import com.jayway.restassured.response.Response;
import net.thucydides.core.annotations.Step;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.model.review.model.TravellersStats;
import travel.snapshot.dp.qa.serenity.analytics.AnalyticsBaseSteps;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;


public class ReviewTravelersSteps extends AnalyticsBaseSteps {

    private static final String BASE_PATH_LOCATIONS = "/review/analytics/";

    public ReviewTravelersSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(REVIEW_BASE_URI));
        spec.basePath(BASE_PATH_LOCATIONS);
    }

    /**
     * @param assertStatement lambda with assert statement checking the object from session response
     * @param type            class with type of object receiving from response
     * @param <T>
     * @throws Exception
     */
    @Step
    public <T> void checkAnalyticsReturnedForType(Consumer<T> assertStatement, Class<T> type) throws Exception {
        T traveller = getSessionResponse().as(type);
        assertStatement.accept(traveller);
    }

    /**
     * @param filePath        path to file with expected data
     * @param assertStatement lambda with assert statement checking reponse against the file data
     * @param fileType        type of object loaded from file
     * @param sessionType     type of object loaded from session
     * @param <T>
     * @param <U>
     * @throws Exception
     */
    @Step
    public <T, U> void checkFileAgainstResponse(String filePath, BiConsumer<T, U> assertStatement, Class<T> fileType, Class<U> sessionType) throws Exception {
        String data = getRequestDataFromFile(this.getClass().getResourceAsStream(filePath));
        T expectedStatistics = from(data).getObject("", fileType);

        U actualStatistics = getSessionResponse().as(sessionType);

        assertStatement.accept(expectedStatistics, actualStatistics);
    }

    @Step
    public void getDataForSpecificTraveler(String url, String traveler, String granularity, String propertyId, String since, String until) {
        Map<String, String> queryParams = new HashMap<>();
        if (propertyId != null) {
            queryParams.put("property", propertyId);
        }
        if (traveler != null) {
            queryParams.put("traveller", traveler);
        }

        Response response = getEntitiesForURLWihDates(url, null, null, since, until, granularity, queryParams);
        setSessionResponse(response);
    }

    /**
     * @param expected
     * @param actual
     */
    public static void assertTravellersData(TravellersStats expected, TravellersStats actual) {
        assertThat(expected.getData(), equalTo(actual.getData()));
    }
}
