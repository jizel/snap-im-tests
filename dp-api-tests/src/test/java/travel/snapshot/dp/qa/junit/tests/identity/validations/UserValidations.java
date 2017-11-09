package travel.snapshot.dp.qa.junit.tests.identity.validations;

import static java.util.Arrays.asList;
import static java.util.Locale.getAvailableLocales;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.qa.cucumber.helpers.FieldType.BOOL;
import static travel.snapshot.dp.qa.cucumber.helpers.FieldType.ENUM;
import static travel.snapshot.dp.qa.cucumber.helpers.FieldType.JSON;
import static travel.snapshot.dp.qa.cucumber.helpers.FieldType.STRING;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import org.junit.Before;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserUpdateDto;
import travel.snapshot.dp.api.model.EntityDto;
import travel.snapshot.dp.qa.cucumber.helpers.ObjectField;
import travel.snapshot.dp.qa.junit.tests.common.CommonValidationTests;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Validations for IM Users - boundary values testing
 */
@Getter
public class UserValidations extends CommonValidationTests {
    private List<ObjectField> attributesBoundaries;
    private String path = USERS_PATH;
    private EntityDto testEntity;
    private Class<UserDto> dtoType = UserDto.class;
    private Class<UserDto[]> dtoArrayType = UserDto[].class;
    private UUID createdCustomerId;

    private static final List<UserUpdateDto.UserType> USER_TYPES = asList(UserUpdateDto.UserType.values());

    @Override
    @Before
    public void setUp() {
        super.setUp();
        testEntity = testUser1;
        createdCustomerId = entityIsCreated(testCustomer1);
        List<Locale> availableLocales = asList(getAvailableLocales());
        String randomCulture = availableLocales.get(random.nextInt(availableLocales.size())).toLanguageTag();
        UserUpdateDto.UserType randomUserType = USER_TYPES.get(random.nextInt(USER_TYPES.size()));

        attributesBoundaries = new ArrayList<>(asList(
                ObjectField.of(
                        "/user_name", STRING, true, "\\w{100}", null, "\\w{101}"),
                ObjectField.of(
                        "/first_name", STRING, true, "\\w{100}", null, "\\w{101}"),
                ObjectField.of(
                        "/last_name", STRING, true, "\\w{100}", null, "\\w{101}"),
                ObjectField.of(
                        "/salesforce_id", STRING, false, "[0-9a-zA-Z]{15}", null, "\\w{101}"),
                ObjectField.of(
                        "/email", STRING, true, "(([a-z]|\\d){9}\\.){4}([a-z]|\\d){10}\\@(([a-z]|\\d){9}\\.){4}com", "\\.{10}", "\\w{101}"),
                ObjectField.of(
                        "/timezone", STRING, true, "(Europe/Prague)", "UTC+01:00", null),
                ObjectField.of(
                        "/culture", STRING, true, randomCulture, null, "\\w{51}"),
                ObjectField.of(
                        "/picture", STRING, false, "http:\\/\\/[a-z0-9]{63}\\.com", "\\.{10}", "\\w{1001}"),
                ObjectField.of(
                        "/comment", STRING, false, "\\w{500}", null, "\\w{501}"),
                ObjectField.of(
                        "/is_active", BOOL, false, String.valueOf(random.nextBoolean()), "\\.{10}", null),
                ObjectField.of(
                        "/user_type", ENUM, true, randomUserType.toString().toLowerCase(), "\\w{10}", null),
                ObjectField.of(
                        "/phone", STRING, false, "\\+[0-9]{8,15}", "\\w{10}", null),
                ObjectField.of(
                        "/user_customer_relationship", JSON, true, String.format("{\"is_primary\":false,\"customer_id\":\"%s\"}", createdCustomerId), "{\"invalid\":\"relationship\"}", null)
        ));
        // Other than customer type users do not support user_customer_relationship
        if (randomUserType == UserUpdateDto.UserType.CUSTOMER) {
            attributesBoundaries.add(ObjectField.of(
                    "/user_customer_relationship", JSON, true, String.format("{\"is_primary\":false,\"customer_id\":\"%s\"}", createdCustomerId), "{\"invalid\":\"relationship\"}", null)
            );
        }
    }

    @Override
    protected ObjectNode getValidUpdate(ObjectNode objectNode) {
        objectNode.remove("user_customer_relationship");

        return objectNode;
    }

}