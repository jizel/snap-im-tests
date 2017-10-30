package travel.snapshot.dp.qa.junit.tests.identity.validations;

import static java.util.Arrays.asList;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PARTNERS_PATH;
import static travel.snapshot.dp.qa.cucumber.helpers.FieldType.BOOL;
import static travel.snapshot.dp.qa.cucumber.helpers.FieldType.STRING;

import lombok.Getter;
import org.junit.Before;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.api.model.EntityDto;
import travel.snapshot.dp.qa.cucumber.helpers.ObjectField;
import travel.snapshot.dp.qa.junit.tests.common.CommonValidationTests;

import java.util.List;

/**
 * Validations for IM Partners - boundary values testing
 */
@Getter
public class PartnerValidations extends CommonValidationTests {

    private List<ObjectField> attributesBoundaries;
    private String path = PARTNERS_PATH;
    private EntityDto testEntity;
    private Class<PartnerDto> dtoType = PartnerDto.class;
    private Class<PartnerDto[]> dtoArrayType = PartnerDto[].class;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        testEntity = testPartner1;
        attributesBoundaries = asList(
                ObjectField.of(
                        "/name", STRING, true, "\\w{100}", null, "\\w{101}"),
                ObjectField.of(
                        "/email", STRING, true, "(([a-z]|\\d){9}\\.){4}([a-z]|\\d){10}\\@(([a-z]|\\d){9}\\.){4}com", "\\.{10}", "\\w{101}"),
                ObjectField.of(
                        "/website", STRING, false, "http:\\/\\/[a-z0-9]{63}\\.com", "\\.{10}", "\\w{1001}"),
                ObjectField.of(
                        "/notes", STRING, false, "\\w{1000}", null, "\\w{1001}"),
                ObjectField.of(
                        "/is_active", BOOL, false, String.valueOf(random.nextBoolean()), "\\.{10}", null),
                ObjectField.of(
                        "/vat_id", STRING, false, "DE[0-9]{9}", null, "\\w{101}")
        );
    }
}
