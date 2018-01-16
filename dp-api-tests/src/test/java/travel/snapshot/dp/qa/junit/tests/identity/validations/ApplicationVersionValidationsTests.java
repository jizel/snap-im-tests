package travel.snapshot.dp.qa.junit.tests.identity.validations;

import static java.util.Arrays.asList;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATION_VERSIONS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.FieldType.BOOL;
import static travel.snapshot.dp.qa.junit.helpers.FieldType.ENUM;
import static travel.snapshot.dp.qa.junit.helpers.FieldType.ID;
import static travel.snapshot.dp.qa.junit.helpers.FieldType.STRING;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import org.junit.Before;
import org.junit.Ignore;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionStatus;
import travel.snapshot.dp.api.model.EntityDto;
import travel.snapshot.dp.qa.junit.helpers.ObjectField;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Validations for IM Applications - boundary values testing
 */
@Ignore
@Getter
public class ApplicationVersionValidationsTests extends CommonValidationTests {

    private List<ObjectField> attributesBoundaries;
    private String path = APPLICATION_VERSIONS_PATH;
    private EntityDto testEntity;
    private Class<ApplicationVersionDto> dtoType = ApplicationVersionDto.class;
    private Class<ApplicationVersionDto[]> dtoArrayType = ApplicationVersionDto[].class;
    private UUID createdApplicationId;
    private static List<ApplicationVersionStatus> STATUSES = asList(ApplicationVersionStatus.values());

    @Override
    @Before
    public void setUp() {
        super.setUp();
        createdApplicationId = entityIsCreated(testApplication1);
        testAppVersion1.setApplicationId(createdApplicationId);
        testEntity = testAppVersion1;
        ApplicationVersionStatus randomApplicationVersionStatus = STATUSES.get(random.nextInt(STATUSES.size()));
        attributesBoundaries = asList(
                ObjectField.of(
                        "/name", STRING, true, "\\w{100}", null, "\\w{101}"),
                ObjectField.of(
                        "/api_key", STRING, true, "\\w{255}", null, "\\w{256}"),
                ObjectField.of(
                        "/status", ENUM, true, randomApplicationVersionStatus.toString().toLowerCase(), "\\.{10}", null),
                ObjectField.of(
                        "/description", STRING, false, "\\w{500}", null, "\\w{1001}"),
                ObjectField.of(
                        "/release_date", STRING, false, LocalDate.now().toString(), "\\.{10}", null),
                ObjectField.of(
                        "/is_active", BOOL, false, String.valueOf(random.nextBoolean()), "\\.{10}", null),
                ObjectField.of(
                        "/is_non_commercial", BOOL, false, String.valueOf(random.nextBoolean()), "\\.{10}", null),
                ObjectField.of(
                        "/application_id", ID, true, createdApplicationId.toString(), "\\w{10}", null)
        );
    }

    @Override
    protected ObjectNode getValidUpdate(ObjectNode objectNode) {
        objectNode.remove("application_id");

        return objectNode;
    }
}
