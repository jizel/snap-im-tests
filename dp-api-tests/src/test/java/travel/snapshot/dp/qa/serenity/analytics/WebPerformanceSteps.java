package travel.snapshot.dp.qa.serenity.analytics;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.dp.api.analytics.model.RecordDto;
import travel.snapshot.dp.api.analytics.model.SingleStatsDto;
import travel.snapshot.dp.api.webperformance.model.PeriodAverageStatsDto;
import travel.snapshot.dp.api.webperformance.model.ReferralStatsDto;
import travel.snapshot.dp.qa.helpers.ObjectMappers;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.helpers.StringUtil;
import travel.snapshot.dp.qa.model.LegacyReferralStatsDto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sedlacek on 10/5/2015.
 */
public class WebPerformanceSteps extends AnalyticsBaseSteps {

    private static final Logger logger = LoggerFactory.getLogger(WebPerformanceSteps.class);

    public WebPerformanceSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(WEB_PERFORMANCE_BASE_URI));
    }

    // GET Requests
    // Special case for web performance

    @Step
    public void getData(String url, String granularity, String propertyId, String since, String until, String metric,
                        String direction) {
        LocalDate sinceDate = StringUtil.parseDate(since);
        LocalDate untilDate = StringUtil.parseDate(until);

        RequestSpecification requestSpecification = given().spec(spec);

        if (StringUtils.isNotBlank(propertyId)) {
            requestSpecification.header("x-property", propertyId);
        }
        if (StringUtils.isNotBlank(granularity)) {
            requestSpecification.parameter("granularity", granularity);
        }
        if (StringUtils.isNotBlank(metric)) {
            switch (direction) {
                case "ascending":
                    requestSpecification.parameter("sort", metric);
                    break;

                case "descending":
                    requestSpecification.parameter("sort_desc", metric);
                    break;
            }
        }
        if (sinceDate != null) {
            requestSpecification.parameter("since", sinceDate.format(DateTimeFormatter.ISO_DATE));
        }
        if (untilDate != null) {
            requestSpecification.parameter("until", untilDate.format(DateTimeFormatter.ISO_DATE));
        }

        Response response = requestSpecification.when().get(url);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    @Step
    public void isoFormatCheckerinList(String path) {
        Response response = getSessionResponse();
        List<String> countries = response.body().jsonPath().getList(path);
        for (String c : countries) {
            assertTrue(c.matches("^[A-Z][A-Z]$"));
        }
    }

    @Step
    public void isoFormatChecker(String path) {
        Response response = getSessionResponse();
        String country = response.body().jsonPath().get(path);
        assertTrue(country.matches("^[A-Z][A-Z]$"));
    }

    @Step
    public void valueRecordIsOfValue(int valueNumber, Class valueType, String value, boolean incomplete) throws Exception {

        Object object = ObjectMappers.OBJECT_MAPPER.readValue(getSessionResponse().body().prettyPrint(), valueType);

        RecordDto recordDto = null;

        if (object instanceof PeriodAverageStatsDto) {
            recordDto = (RecordDto) ((PeriodAverageStatsDto) object).getValues().get(valueNumber - 1);
        } else if (object instanceof SingleStatsDto) {
            recordDto = (RecordDto) ((SingleStatsDto) object).getValues().get(valueNumber - 1);
        } else {
            throw new IllegalStateException("Unsupported type.");
        }

        if (incomplete) {
            assertTrue(recordDto.getDataIncomplete() == null || recordDto.getDataIncomplete());
        }

        if (value == null) {
            assertNull(recordDto.getValue());
        } else {
            Double d = Double.valueOf(recordDto.getValue().toString());
            String s = d.longValue() == d ? "" + d.longValue() : "" + d;

            if (s.contains(".")) {
                s = s.split("\\.")[0];
            }

            assertEquals(Integer.parseInt(value), Integer.parseInt(s));
        }
    }

    @Step
    public void referralsAreSorted(String metric, boolean ascending) throws Exception {

        ReferralStatsDto referralStatsDto = ObjectMappers.OBJECT_MAPPER.readValue(getSessionResponse().body().prettyPrint(), LegacyReferralStatsDto.class);

        List values = referralStatsDto.getValues().stream().map(recordDto -> {
            switch (metric) {
                case "revenue":
                    return recordDto.getValue().getRevenue();
                case "visits":
                    return recordDto.getValue().getVisits();
                case "visits_unique":
                    return recordDto.getValue().getUniqueVisits();
                case "site":
                    return recordDto.getValue().getSite();
                default:
                    throw new IllegalArgumentException("");
            }
        }).collect(Collectors.toList());

        listOfObjectsAreSortedAccordingToProperty(values, ascending, values.get(0).getClass());
    }
}
