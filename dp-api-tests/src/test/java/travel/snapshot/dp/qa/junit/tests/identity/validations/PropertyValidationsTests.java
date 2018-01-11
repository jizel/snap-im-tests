package travel.snapshot.dp.qa.junit.tests.identity.validations;

import static java.util.Arrays.asList;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.qa.cucumber.helpers.FieldType.BOOL;
import static travel.snapshot.dp.qa.cucumber.helpers.FieldType.ID;
import static travel.snapshot.dp.qa.cucumber.helpers.FieldType.INTEGER;
import static travel.snapshot.dp.qa.cucumber.helpers.FieldType.STRING;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;

import groovy.util.logging.Slf4j;
import lombok.Getter;
import org.junit.Before;
import travel.snapshot.dp.api.identity.model.CustomerType;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.model.EntityDto;
import travel.snapshot.dp.qa.cucumber.helpers.ObjectField;

import java.util.List;
import java.util.UUID;

/**
 * Validations for IM Properties - boundary values testing
 */
@Slf4j
@Getter
public class PropertyValidationsTests extends CommonValidationTests{
    private List<ObjectField> attributesBoundaries;
    private String path = PROPERTIES_PATH;
    private EntityDto testEntity;
    private Class<PropertyDto> dtoType = PropertyDto.class;
    private Class<PropertyDto[]> dtoArrayType = PropertyDto[].class;
    private UUID createdCustomerId;


    private static final List<CustomerType> CUSTOMER_TYPES = asList(CustomerType.values());

    @Override
    @Before
    public void setUp() {
        super.setUp();
        testEntity = testProperty1;
        createdCustomerId = entityIsCreated(testCustomer1);

        attributesBoundaries = asList(
                ObjectField.of(
                        "/name", STRING, true, "\\w{100}", null, "\\w{101}"),
                ObjectField.of(
                        "/salesforce_id", STRING, false, "[0-9a-zA-Z]{15}", null, "\\w{101}"),
                ObjectField.of(
                        "/tti_id", INTEGER, false, String.valueOf(random.nextInt()), "\\w{10}", String.valueOf((long) Integer.MAX_VALUE + 1)),
                ObjectField.of(
                        "/website", STRING, false, "http:\\/\\/[a-z0-9]{63}\\.com", "\\.{10}", "\\w{1001}"),
                ObjectField.of(
                        "/email", STRING, true, "(([a-z]|\\d){9}\\.){4}([a-z]|\\d){10}\\@(([a-z]|\\d){9}\\.){4}com", "\\.{10}", "\\w{101}"),
                ObjectField.of(
                        "/is_demo_property", BOOL, true, String.valueOf(random.nextBoolean()), "\\.{10}", null),
                ObjectField.of(
                        "/is_active", BOOL, false, String.valueOf(random.nextBoolean()), "\\.{10}", null),
                ObjectField.of(
                        "/timezone", STRING, true, "(Europe/Prague)", "UTC+01:00", null),
                ObjectField.of(
                        "/description", STRING, false, "\\w{500}", null, "\\w{501}"),
                ObjectField.of(
                        "/anchor_customer_id", ID, true, createdCustomerId.toString(), "\\w{10}", null),
                //        Address
                ObjectField.of(
                        "/address/address_line1", STRING, true, "\\w{150}", null, "\\w{151}"),
                ObjectField.of(
                        "/address/zip_code", STRING, true, "[a-zA-Z0-9]{20}", null, "\\w{21}"),
                ObjectField.of(
                        "/address/city", STRING, true, "\\w{100}", null, "\\w{101}"),
                ObjectField.of(
                        "/address/country", STRING, true, "DE", "XX", "USA")
        );
    }
}
