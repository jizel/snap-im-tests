package travel.snapshot.dp.qa.junit.tests.identity.validations;

import static java.util.Arrays.asList;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATIONS_PATH;
import static travel.snapshot.dp.qa.cucumber.helpers.FieldType.BOOL;
import static travel.snapshot.dp.qa.cucumber.helpers.FieldType.ID;
import static travel.snapshot.dp.qa.cucumber.helpers.FieldType.STRING;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;

import lombok.Getter;
import org.junit.Before;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.model.EntityDto;
import travel.snapshot.dp.qa.cucumber.helpers.ObjectField;
import travel.snapshot.dp.qa.junit.tests.common.CommonValidationTests;

import java.util.List;
import java.util.UUID;

/**
 * Validations for IM Applications - boundary values testing
 */
@Getter
public class ApplicationValidations extends CommonValidationTests {

    private List<ObjectField> attributesBoundaries;
    private String path = APPLICATIONS_PATH;
    private EntityDto testEntity;
    private Class<ApplicationDto> dtoType = ApplicationDto.class;
    private Class<ApplicationDto[]> dtoArrayType = ApplicationDto[].class;
    private UUID createdPartnerId;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        testEntity = testApplication1;
        createdPartnerId = entityIsCreated(testPartner1);
        attributesBoundaries = asList(
                ObjectField.of(
                        "/name", STRING, true, "\\w{100}", null, "\\w{101}"),
                ObjectField.of(
                        "/website", STRING, true, "http:\\/\\/[a-z0-9]{63}\\.com", "\\.{10}", "\\w{1001}"),
                ObjectField.of(
                        "/description", STRING, false, "\\w{500}", null, "\\w{1001}"),
                ObjectField.of(
                        "/is_active", BOOL, false, String.valueOf(random.nextBoolean()), "\\.{10}", null),
                ObjectField.of(
                        "/is_internal", BOOL, true, String.valueOf(random.nextBoolean()), "\\.{10}", null),
                ObjectField.of(
                        "/partner_id", ID, true, createdPartnerId.toString(), "\\w{10}", null)
        );
    }
}
