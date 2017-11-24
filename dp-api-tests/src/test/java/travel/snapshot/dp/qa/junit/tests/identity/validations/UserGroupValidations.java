package travel.snapshot.dp.qa.junit.tests.identity.validations;

import static java.util.Arrays.asList;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUPS_PATH;
import static travel.snapshot.dp.qa.cucumber.helpers.FieldType.BOOL;
import static travel.snapshot.dp.qa.cucumber.helpers.FieldType.ID;
import static travel.snapshot.dp.qa.cucumber.helpers.FieldType.STRING;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;

import lombok.Getter;
import org.junit.Before;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
import travel.snapshot.dp.api.model.EntityDto;
import travel.snapshot.dp.qa.cucumber.helpers.ObjectField;

import java.util.List;
import java.util.UUID;

/**
 * Validations for IM Property Sets - boundary values testing
 */
@Getter
public class UserGroupValidations extends CommonValidationTests {

    private List<ObjectField> attributesBoundaries;
    private String path = USER_GROUPS_PATH;
    private EntityDto testEntity;
    private Class<UserGroupDto> dtoType = UserGroupDto.class;
    private Class<UserGroupDto[]> dtoArrayType = UserGroupDto[].class;
    private UUID createdCustomerId;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        testEntity = testUserGroup1;
        createdCustomerId = entityIsCreated(testCustomer1);
        attributesBoundaries = asList(
                ObjectField.of(
                        "/name", STRING, true, "\\w{100}", null, "\\w{101}"),
                ObjectField.of(
                        "/is_active", BOOL, false, String.valueOf(random.nextBoolean()), "\\.{10}", null),
                ObjectField.of(
                        "/description", STRING, false, "\\w{500}", null, "\\w{501}"),
                ObjectField.of(
                        "/picture", STRING, false, "http:\\/\\/[a-z0-9]{63}\\.com", "\\.{10}", "\\w{1001}"),
                ObjectField.of(
                        "/customer_id", ID, true, createdCustomerId.toString(), "\\w{10}", null)
        );
    }
}
