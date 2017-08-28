package travel.snapshot.dp.qa.junit.tests.identity.properties;

import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.PROPERTY_CODE;

import com.google.gson.Gson;
import org.apache.commons.collections.map.SingletonMap;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.PropertyUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

public class PropertyCodeTests extends CommonTest{

    @Test
    public void whenPropertyCodeIsNotUniqueTheSmallestPossibleIntegerIsConcatenatedToIt() {
        testProperty1.setId(null);
        testProperty1.setCode(null);
        commonHelpers.entityIsCreated(PROPERTIES_PATH, testProperty1);
        String propertyCode1 = commonHelpers.getAttributeValue(PROPERTY_CODE);
        commonHelpers.entityIsCreated(PROPERTIES_PATH, testProperty1);
        String propertyCode2 = commonHelpers.getAttributeValue(PROPERTY_CODE);
        assertThat(propertyCode1, is("CZBRQPRO"));
        assertThat(propertyCode2, is("CZBRQPRO1"));
    }

    @Test
    public void propertyCodeHasToBeUnique() {
        testProperty1.setId(null);
        testProperty1.setCode("ليونيكود");
        commonHelpers.entityIsCreated(PROPERTIES_PATH, testProperty1);
        commonHelpers.createEntity(PROPERTIES_PATH, testProperty1);
        responseCodeIs(SC_CONFLICT);
        customCodeIs(CC_CONFLICT_CODE);
    }

    @Test
    public void maximumPropertyCodeLengthIs50Characters() {
        testProperty1.setId(null);
        testProperty1.setCode(StringUtils.repeat("A", 50));
        commonHelpers.entityIsCreated(PROPERTIES_PATH, testProperty1);
        testProperty1.setCode(StringUtils.repeat("A", 51));
        commonHelpers.createEntity(PROPERTIES_PATH, testProperty1);
        responseIsUnprocessableEntity();
    }

    @Test
    public void propertyCodeCanNotBeUpdated() {
        UUID createdPropertyId = commonHelpers.entityIsCreated(PROPERTIES_PATH, testProperty1);
        PropertyUpdateDto update = new PropertyUpdateDto();
        SingletonMap map = new SingletonMap("property_code", "BRQ1234567");
        Gson gsone = new Gson();
        commonHelpers.updateEntity(PROPERTIES_PATH, createdPropertyId, gsone.toJson(map));
        responseIsUnprocessableEntity();
    }
}
