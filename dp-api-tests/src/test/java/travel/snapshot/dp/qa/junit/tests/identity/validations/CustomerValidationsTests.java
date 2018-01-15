package travel.snapshot.dp.qa.junit.tests.identity.validations;

import static java.util.Arrays.asList;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.FieldType.BOOL;
import static travel.snapshot.dp.qa.junit.helpers.FieldType.ENUM;
import static travel.snapshot.dp.qa.junit.helpers.FieldType.STRING;

import groovy.util.logging.Slf4j;
import lombok.Getter;
import org.junit.Before;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerType;
import travel.snapshot.dp.api.model.EntityDto;
import travel.snapshot.dp.qa.junit.helpers.ObjectField;

import java.util.List;

/**
 * Validations for customer attributes - boundary values testing
 */
@Slf4j
@Getter
public class CustomerValidationsTests extends CommonValidationTests {

    private List<ObjectField> attributesBoundaries;
    private String path = CUSTOMERS_PATH;
    private EntityDto testEntity;
    private Class<CustomerDto> dtoType = CustomerDto.class;
    private Class<CustomerDto[]> dtoArrayType = CustomerDto[].class;


    private static final List<CustomerType> CUSTOMER_TYPES = asList(CustomerType.values());

    @Override
    @Before
    public void setUp() {
        super.setUp();
        testEntity = testCustomer1;

        attributesBoundaries = asList(
                ObjectField.of(
                        "/name", STRING, true, "\\w{100}", null, "\\w{101}"),
                ObjectField.of(
                        "/salesforce_id", STRING, false, "[0-9a-zA-Z]{15}", null, "\\w{101}"),
                ObjectField.of(
                        "/vat_id", STRING, false, "DE[0-9]{9}", null, "\\w{101}"),
                ObjectField.of(
                        "/website", STRING, false, "http:\\/\\/[a-z0-9]{63}\\.com", "\\.{10}", "\\w{1001}"),
                ObjectField.of(
                        "/email", STRING, true, "(([a-z]|\\d){9}\\.){4}([a-z]|\\d){10}\\@(([a-z]|\\d){9}\\.){4}com", "\\.{10}", "\\w{101}"),
                ObjectField.of(
                        "/headquarters_timezone", STRING, true, "(Europe/Prague)", "UTC+01:00", null),
                ObjectField.of(
                        "/is_demo_customer", BOOL, true, String.valueOf(random.nextBoolean()), "\\.{10}", null),
                ObjectField.of(
                        "/is_active", BOOL, false, String.valueOf(random.nextBoolean()), "\\.{10}", null),
                ObjectField.of(
                        "/type", ENUM, true, CUSTOMER_TYPES.get(random.nextInt(CUSTOMER_TYPES.size())).toString().toLowerCase(), "\\w{10}", null),
                ObjectField.of(
                        "/phone", STRING, false, "\\+[0-9]{8,15}", "\\w{10}", null),
                ObjectField.of(
                        "/notes", STRING, false, "\\w{999}", null, "\\w{1001}"),
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
