package travel.snapshot.dp.qa.junit.tests.identity.propertysets;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.qa.junit.tests.Categories;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.io.IOException;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.Assert.fail;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.headerContains;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.headerIs;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.numberOfEntitiesInResponse;

@RunWith(JUnitParamsRunner.class)
public class ParametersPropertySetTests extends CommonTest {

    private static final String EXAMPLES = "src/test/resources/csv/property_sets/";


    @FileParameters(EXAMPLES + "getPropertySets.csv")
    @Test
    @Category(Categories.SlowTests.class)
    public void getPropertySets(
        String limit,
        String cursor,
        String returned,
        String total,
        String linkHeader
    ) throws Exception {

        // Create property sets
        IntStream.range(0, 52).forEachOrdered(n -> {
            testPropertySet1.setName(String.format("ps_name_%d", n));
            try {
                commonHelpers.entityIsCreated(PROPERTY_SETS_PATH, testPropertySet1);
            } catch (IOException e) {
                fail(e.getMessage());
            }
        });

        // Get list of property sets
        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, null, null, null, null);
        commonHelpers.getEntities(PROPERTY_SETS_PATH, params);
        numberOfEntitiesInResponse(PropertySetDto.class, Integer.parseInt(returned));
        headerIs("X-Total-Count", total);
        headerContains("Link", linkHeader);
    }
}
