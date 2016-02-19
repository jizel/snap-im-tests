package travel.snapshot.dp.qa.steps.review;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import groovy.io.FileType;
import net.thucydides.core.annotations.Step;
import org.apache.commons.lang3.reflect.TypeUtils;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.helpers.StringUtil;
import travel.snapshot.dp.qa.model.review.model.Traveller;
import travel.snapshot.dp.qa.model.review.model.TravellersStats;
import travel.snapshot.dp.qa.serenity.analytics.AnalyticsBaseSteps;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ReviewTravelersSteps extends AnalyticsBaseSteps {

    private static final String BASE_PATH_LOCATIONS = "/review/analytics/";

    public ReviewTravelersSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(REVIEW_BASE_URI));
        spec.basePath(BASE_PATH_LOCATIONS);
    }

    /**
     *
     * @param assertStatement lambda with assert statement checking the object from session response
     * @param type class with type of object receiving from response
     * @param <T>
     * @throws Exception
     */
    @Step
    public <T> void checkAnalyticsReturnedForType(Consumer<T> assertStatement, Class<T> type) throws Exception{
        T traveller = getSessionResponse().as(type);
        assertStatement.accept(traveller);
    }

    /**
     *
     * @param filePath path to file with expected data
     * @param assertStatement lambda with assert statement checking reponse against the file data
     * @param fileType type of object loaded from file
     * @param sessionType type of object loaded from session
     * @param <T>
     * @param <U>
     * @throws Exception
     */
    @Step
    public <T, U> void checkFileAgainstResponse(String filePath, BiConsumer<T, U> assertStatement, Class<T> fileType, Class<U> sessionType) throws Exception{
        String data = getRequestDataFromFile(this.getClass().getResourceAsStream(filePath));
        T expectedStatistics = from(data).getObject("", fileType);

        U actualStatistics = getSessionResponse().as(sessionType);

        assertStatement.accept(expectedStatistics, actualStatistics);
    }

    @Step
    public void getDataForSpecificTraveler(String url, String traveler, String granularity, String propertyId, String since, String until) {
        RequestSpecification requestSpecification = given().spec(spec);
        LocalDate sinceDate = StringUtil.parseDate(since);
        LocalDate untilDate = StringUtil.parseDate(until);

        if (sinceDate != null) {
            requestSpecification.parameter("since", sinceDate.format(DateTimeFormatter.ISO_DATE));
        }
        if (untilDate != null) {
            requestSpecification.parameter("until", untilDate.format(DateTimeFormatter.ISO_DATE));
        }
        if (granularity != null) {
            requestSpecification.parameter("granularity", granularity);
        }
        if (propertyId != null) {
            requestSpecification.parameter("property", propertyId);
        }
        if (traveler != null) {
            requestSpecification.parameter("traveller", traveler);
        }

        Response response = requestSpecification.when().get(url);
        setSessionResponse(response);
    }

    /**
     *
     * @param expected
     * @param actual
     */
    public static void assertTravellersData(TravellersStats expected, TravellersStats actual) {
        assertThat(expected.getData(), equalTo(actual.getData()));
    }
}
