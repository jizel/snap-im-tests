package travel.snapshot.dp.qa.junit.tests.identity.relationships;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipDto;
import travel.snapshot.dp.qa.cucumber.steps.DbStepDefs;
import travel.snapshot.dp.qa.junit.tests.Categories;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.Map;
import java.util.UUID;

import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.CHAIN;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsType;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructCustomerPropertyRelationshipDto;

@RunWith(JUnitParamsRunner.class)
@Category(Categories.SlowTests.class)
public class CustomerPropertyFilteringTests extends CommonTest {

    private static final String EXAMPLES = "src/test/resources/csv/relationships/";
    private static DbStepDefs dbStepDefs;

    @BeforeClass
    public static void createTestResources() throws Exception {
        // Create 30 test properties and customer-property relations but only once for all tests!
        dbStepDefs = new DbStepDefs();
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
        loadDefaultTestEntities();
        UUID createdCustomerId = entityIsCreated(testCustomer1);
        range(0, 30).forEachOrdered(n -> {
            testProperty1.setName(String.format("property_%d", n));
            testProperty1.setId(null);
            testProperty1.setCode(null);
            UUID propertyId = entityIsCreated(testProperty1);
            entityIsCreated(constructCustomerPropertyRelationshipDto(createdCustomerId, propertyId, true, CHAIN, validFrom, validTo));
        });
    }

    @Override
    @Before
    public void setUp() {
    }

    @Test
    @FileParameters(EXAMPLES + "gettingListOfCustomerProperties.csv")
    public void gettingListOfCustomerProperties(String limit, String cursor, String returned) {
        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, null, null, null, null);
        assertThat(getEntitiesAsType(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, CustomerPropertyRelationshipDto.class, params)).hasSize(Integer.valueOf(returned));
    }
}
