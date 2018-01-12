package travel.snapshot.dp.qa.junit.tests.identity.application_versions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static travel.snapshot.dp.api.identity.model.ApplicationVersionStatus.CERTIFIED;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATION_VERSIONS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_CODE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityAsType;

public class ApplicationVersionsTests extends CommonTest {

    UUID appId;

    @BeforeEach
    public void setUp() {
        super.setUp();
        appId = entityIsCreated(testApplication1);
    }

    @Test
    void appVersionsCRUD() {
        testAppVersion1.setApplicationId(appId);
        UUID appVersionId = entityIsCreated(testAppVersion1);
        ApplicationVersionDto app = getEntityAsType(APPLICATION_VERSIONS_PATH, ApplicationVersionDto.class, appVersionId);
        assertAll(
                () -> assertThat(app.getApplicationId().equals(appId)),
                () -> assertThat(app.getName().equals(testAppVersion1.getName())),
                () -> assertThat(app.getDescription().equals(testAppVersion1.getDescription())),
                () -> assertThat(app.getIsActive().equals(testAppVersion1.getIsActive()))
        );
        ApplicationVersionUpdateDto updateDto = new ApplicationVersionUpdateDto();
        updateDto.setName("New name");
        entityIsUpdated(APPLICATION_VERSIONS_PATH, appVersionId, updateDto);
        entityIsDeleted(APPLICATION_VERSIONS_PATH, appVersionId);
    }

    @Test
    void createAppVersionErrors() {
        // non-unique appId
        createEntity(testAppVersion1).then().statusCode(SC_CONFLICT).body(RESPONSE_CODE, is(CC_CONFLICT_CODE));
        // missing appId
        testAppVersion1.setApplicationId(null);
        createEntity(testAppVersion1).then().statusCode(SC_UNPROCESSABLE_ENTITY).body(RESPONSE_CODE, is(CC_SEMANTIC_ERRORS));
        // Missing name
        testAppVersion1.setApplicationId(appId);
        testAppVersion1.setName(null);
        createEntity(testAppVersion1).then().statusCode(SC_UNPROCESSABLE_ENTITY).body(RESPONSE_CODE, is(CC_SEMANTIC_ERRORS));
        // Missing status
        testAppVersion1.setName("Some name");
        testAppVersion1.setStatus(null);
        createEntity(testAppVersion1).then().statusCode(SC_UNPROCESSABLE_ENTITY).body(RESPONSE_CODE, is(CC_SEMANTIC_ERRORS));
        // Non-unique ID
        testAppVersion1.setStatus(CERTIFIED);
        testAppVersion1.setId(DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID);
        createEntity(testAppVersion1).then().statusCode(SC_CONFLICT).body(RESPONSE_CODE, is(CC_CONFLICT_ID));
    }
}
