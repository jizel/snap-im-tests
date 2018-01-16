package travel.snapshot.dp.qa.serenity.analytics;

import static org.junit.Assert.*;
import static travel.snapshot.dp.json.ObjectMappers.OBJECT_MAPPER;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.jayway.restassured.response.Response;
import net.thucydides.core.annotations.Step;
import travel.snapshot.dp.api.analytics.model.GlobalStatsDto;
import travel.snapshot.dp.api.analytics.model.MetricDto;
import travel.snapshot.dp.api.analytics.model.MetricName;
import travel.snapshot.dp.api.analytics.model.RecordDto;
import travel.snapshot.dp.qa.helpers.MetricNameDeserializer;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sedlacek on 10/5/2015.
 */
public class InstagramSteps extends AnalyticsBaseSteps {


    public InstagramSteps() {
        spec.baseUri(propertiesHelper.getProperty(INSTAGRAM_BASE_URI));
    }


    /**
     * This method is set to check only one value for every metric in object
     *
     * @param expected list of values that each is unique
     */
    @Step
    public void checkListofValuesFromResponse(List<Integer> expected) {
        Response response = getSessionResponse();
        GlobalStatsDto actualStats = null;
        try {
            SimpleModule module = new SimpleModule();
            module.addDeserializer(MetricName.class, new MetricNameDeserializer());
            OBJECT_MAPPER.registerModule(module);
            actualStats = OBJECT_MAPPER.readValue(response.asString(), TypeFactory.defaultInstance().constructType(GlobalStatsDto.class));
        } catch (IOException e){
            fail("Exception when trying to convert response to GlobalStats object: " + e.getMessage());
        }

        ArrayList<Integer> actualList = new ArrayList<Integer>();
        for (MetricDto metric : actualStats.getData()) {
            List<RecordDto<? extends Number>> actualValues = metric.getValues();
            actualList.add(Integer.valueOf(actualValues.get(0).getValue().toString()));
        }

        expected.sort(Integer::compareTo);
        actualList.sort(Integer::compareTo);

        assertEquals(expected, actualList);
    }
}
