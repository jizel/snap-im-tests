package travel.snapshot.dp.qa.serenity.analytics;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.*;

import com.jayway.restassured.response.Response;
import net.thucydides.core.annotations.Step;
import org.apache.commons.lang3.StringUtils;
import travel.snapshot.dp.qa.helpers.ObjectMappers;
import travel.snapshot.dp.qa.helpers.StringUtil;
import travel.snapshot.dp.qa.serenity.BasicSteps;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * Created by sedlacek on 10/5/2015.
 */
public class AnalyticsBaseSteps extends BasicSteps {


    public AnalyticsBaseSteps() {
    }

    //GET Requests

    @Step
    public void getDataWithoutProperty(String url) {
        Response response = given().spec(spec).parameter("access_token", "aaa").get(url);
        setSessionResponse(response);
    }

    @Step
    //TODO try out test that have been affected (all social media)
    /**
     * returns entities for certain property for certain date granularity
     */
    public void getPropertiesWithDate(String url, String granularity, String propertyId, String since, String until) {
        Map<String, String> prepareParams = new HashMap<>();
        if (StringUtils.isNotBlank(propertyId)) {
            prepareParams.put("property", propertyId);
        }

        Response response = getEntitiesForUrlWihDates(url, null, null, since, until, granularity, prepareParams);
        setSessionResponse(response);
    }

    @Step
    //TODO try out test that have been affected (all social media)
    /**
     * returns entities with paging for certain property
     */
    public void getPropertiesWithPaging(String url, String propertyId, String limit, String cursor) {
        Map<String, String> queryParams = new HashMap<>();
        if (StringUtils.isNotBlank(propertyId)) {
            queryParams.put("property", propertyId);
        }
        Response response = getEntities(url, limit, cursor, null, null, null, queryParams);
        setSessionResponse(response);
    }

    public void getPropertiesWithPagingAndDate(String url, String propertyId, String limit, String cursor, String granularity, String since, String until) {
        Map<String, String> queryParams = new HashMap<>();
        if (StringUtils.isNotBlank(propertyId)) {
            queryParams.put("property", propertyId);
        }

        Response response = getEntitiesForUrlWihDates(url, limit, cursor, since, until, granularity, queryParams);
        setSessionResponse(response);
    }

    //Response Validation
    @Step
    public void responseContainsValues(int count) {
        Response response = getSessionResponse();
        response.then().body("values.size()", is(count));
    }

    @Step
    public void responseContainsValuesForAllMetrics(int count) {
        Response response = getSessionResponse();
        Map responseMap = response.as(Map.class);

        List<Map<String, Object>> metrics = (List<Map<String, Object>>) responseMap.get("data");
        metrics.forEach(m -> {
            List values = (List) m.get("values");
            assertEquals("Bad number of values for metric " + m.get("name"), count, values.size());
        });
    }

    @Step
    public void maximumNumberOfItemsInResponse(int count) {
        Response response = getSessionResponse();
        response.then().body("size()", lessThanOrEqualTo(count));
    }

    @Step
    public void responseContainsObjectsAllWithPropertyAndValues(String property, List<String> values) {
        List responseList = getSessionResponse().body().jsonPath().getList(property);

        for (Object record : responseList) {
            assertTrue(((List) record).containsAll(values));
        }
    }

    @Step
    public void listOfObjectsAreSortedAccordingToProperty(String property, boolean ascending, Class propertyClassType) {
        if (property == null || property.isEmpty()) {
            throw new IllegalArgumentException("Property ID must not be a null object.");
        }
        List values = getSessionResponse().body().jsonPath().getList(property, propertyClassType);

        listOfObjectsAreSortedAccordingToProperty(values, ascending, propertyClassType);
    }

    @Step
    public void listOfObjectsAreSortedAccordingToProperty(List values, boolean ascending, Class propertyClassType) {

        if (propertyClassType == null) {
            throw new IllegalArgumentException("Property class type can not be a null object.");
        }

        BiPredicate equalityPredicate = null;

        BiPredicate<? extends Number, ? extends Number> numberEquality = ascending ? (a, b) -> a.longValue() <= b.longValue() : (a, b) -> a.longValue() >= b.longValue();
        BiPredicate<String, String> stringEquality = ascending ? (a, b) -> a.compareTo(b) <= 0 : (a, b) -> a.compareTo(b) >= 0;

        if (propertyClassType == Double.class || propertyClassType == Long.class || propertyClassType == Integer.class) {
            equalityPredicate = numberEquality;
        }

        if (propertyClassType == String.class) {
            equalityPredicate = stringEquality;
        }

        if (equalityPredicate == null) {
            throw new IllegalArgumentException(String.format("Unable to resolve equality predicate from property of class type %s.", propertyClassType.getName()));
        }

        for (int i = 0; i < values.size() - 1; i++) {
            assertTrue("\nValue at index " + i + ": " + values.get(i) + "\nValue at index " + (i + 1) + ": " + values.get(i + 1),
                    equalityPredicate.test(values.get(i), values.get(i + 1)));
        }
    }

    @Step
    public void valueIsLessThanOrEqualTo(String pathToValue1, String pathToValue2) {
        Response response = getSessionResponse();
        response.then().body(pathToValue1, lessThanOrEqualTo(pathToValue2));
    }

    @Step
    public void dateFieldIs(String fieldName, String value) {
        LocalDate expectedDate = StringUtil.parseDate(value);
        Response response = getSessionResponse();

        LocalDate actualDate = LocalDate.parse(response.getBody().path(fieldName));
        assertEquals(expectedDate, actualDate);
    }

    @Step
    public void responseContainsCorrectDateRangeFor(String granularity, String since, String until) {
        LocalDate sinceDate = StringUtil.parseDate(since);
        LocalDate untilDate = StringUtil.parseDate(until);

        int count = 0;
        LocalDate d = sinceDate;
        while (!d.isAfter(untilDate)) {
            switch (granularity) {
                case "":
                case "day": {
                    count++;
                    break;
                }
                case "week": {
                    if (DayOfWeek.SUNDAY.equals(d.getDayOfWeek())) {
                        count++;
                    }
                    break;
                }
                case "month": {
                    if (d.getDayOfMonth() == d.lengthOfMonth()) {
                        count++;
                    }
                    break;
                }
            }

            d = d.plusDays(1);
        }
        responseContainsValues(count);
    }

    /**
     * @param assertStatement lambda with assert statement checking the object from session
     *                        response
     * @param type            class with type of object receiving from response
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
     */
    @Step
    public <T> void checkFileAgainstResponse(String filePath, BiConsumer<T, T> assertStatement, Class<T> type) throws Exception {
        String data;
        try (InputStream stream = this.getClass().getResourceAsStream(filePath)) {
            data = getRequestDataFromFile(stream);
        }

        T expectedStatistics = ObjectMappers.OBJECT_MAPPER.readValue(data, type);
        T actualStatistics = getSessionResponse().as(type);

        assertStatement.accept(expectedStatistics, actualStatistics);
    }

    @Step
    public void responseContainsDataValues(int count) {
        Response response = getSessionResponse();
        response.then().body("data.size()", is(count));
    }

    @Step
    public void bodyContainsEntityWithSize(String attributeName, int size) {
        Response response = getSessionResponse();
        response.then().body(attributeName, hasSize(size));
    }
}
