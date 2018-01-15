package travel.snapshot.dp.qa.junit.tests.identity.validations;

import static java.util.Arrays.asList;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.FieldType.BOOL;
import static travel.snapshot.dp.qa.junit.helpers.FieldType.ENUM;
import static travel.snapshot.dp.qa.junit.helpers.FieldType.ID;
import static travel.snapshot.dp.qa.junit.helpers.FieldType.STRING;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;

import lombok.Getter;
import org.junit.Before;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.PropertySetType;
import travel.snapshot.dp.api.model.EntityDto;
import travel.snapshot.dp.qa.junit.helpers.ObjectField;

import java.util.List;
import java.util.UUID;

/**
 * Validations for IM Property Sets - boundary values testing
 */
@Getter
public class PropertySetValidationsTests extends CommonValidationTests {

    private List<ObjectField> attributesBoundaries;
    private String path = PROPERTY_SETS_PATH;
    private EntityDto testEntity;
    private Class<PropertySetDto> dtoType = PropertySetDto.class;
    private Class<PropertySetDto[]> dtoArrayType = PropertySetDto[].class;
    private UUID createdCustomerId;
    private UUID createdPropertySetId;

    private static final List<PropertySetType> PROPERTY_SET_TYPES = asList(PropertySetType.values());

    @Override
    @Before
    public void setUp() {
        super.setUp();
        testEntity = testPropertySet1;
        createdCustomerId = entityIsCreated(testCustomer1);
        createdPropertySetId = entityIsCreated(testPropertySet2);
        PropertySetType randomPropertySetType = PROPERTY_SET_TYPES.get(random.nextInt(PROPERTY_SET_TYPES.size()));
        attributesBoundaries = asList(
                ObjectField.of(
                        "/name", STRING, true, "\\w{100}", null, "\\w{101}"),
                ObjectField.of(
                        "/is_active", BOOL, false, String.valueOf(random.nextBoolean()), "\\.{10}", null),
                ObjectField.of(
                        "/description", STRING, false, "\\w{500}", null, "\\w{501}"),
                ObjectField.of(
                        "/property_set_type", ENUM, true, randomPropertySetType.toString().toLowerCase(), "\\w{10}", null),
                ObjectField.of(
                        "/customer_id", ID, true, createdCustomerId.toString(), "\\w{10}", null),
                ObjectField.of(
                        "/parent_property_set_id", ID, false, createdPropertySetId.toString(), "\\w{10}", null)
        );
    }
}
