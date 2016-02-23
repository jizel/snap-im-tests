package travel.snapshot.dp.qa.serenity.review;

import com.jayway.restassured.response.Response;
import net.thucydides.core.annotations.Step;
import travel.snapshot.dp.qa.helpers.ObjectMappers;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.analytics.AnalyticsBaseSteps;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;

public class ReviewSteps extends AnalyticsBaseSteps {

    public ReviewSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(REVIEW_BASE_URI));
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
     * @param type            type of object loaded from file
     * @param <T>
     * @throws Exception
     */
    @Step
    public <T> void checkFileAgainstResponse(String filePath, BiConsumer<T, T> assertStatement, Class<T> type) throws Exception {

        String data = getRequestDataFromFile(this.getClass().getResourceAsStream(filePath));
        T expectedStatistics = ObjectMappers.OBJECT_MAPPER.readValue(data, type);

        T actualStatistics = getSessionResponse().as(type);

        assertStatement.accept(expectedStatistics, actualStatistics);
    }

    public void checkNumberOfValuesReturnedForEachProperty(int count) {
        Response response = getSessionResponse();
        ArrayList propertyValues = response.jsonPath().get("properties.values");
        for (Object property : propertyValues) {
            assertEquals(((ArrayList) property).size(), count);
        }
    }

    public void getReviewAnalyticsData(String url, String granularity, String since, String until, String limit, String cursor) {
        Response aggregatedPropertySet = getEntitiesForUrlWihDates(url, limit, cursor, since, until, granularity, null);
        setSessionResponse(aggregatedPropertySet);
    }
}
