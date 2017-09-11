package travel.snapshot.dp.qa.junit.tests.common;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PLATFORM_OPERATIONS_PATH;

import com.jayway.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import travel.snapshot.dp.api.identity.model.PlatformOperationDto;
import travel.snapshot.dp.api.type.HttpMethod;

import java.util.UUID;

/**
 * Commons for all Platform Operations tests
 */
public class CommonPlatformOperationTest extends CommonTest{

    protected static final String TEST_URI_TEMPLATE = "/test/uri/template";
    protected static final String TEST_IDENTITY_URI_TEMPLATE = "/identity/anything";
    protected static PlatformOperationDto platformOperationTestDto;
    protected static PlatformOperationDto createdPlatformOperation;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        platformOperationTestDto = platformOperationHelpers.constructPlatformOperation(HttpMethod.GET, TEST_URI_TEMPLATE);
        createdPlatformOperation = commonHelpers.entityWithTypeIsCreated(PLATFORM_OPERATIONS_PATH, PlatformOperationDto.class, platformOperationTestDto);
    }

    @After
    public void tearDown() throws Exception {
        deletePlatformOperationIfExists(createdPlatformOperation.getId());
    }

    protected static void deletePlatformOperationIfExists(UUID platformOperationId) {
        Response getResponse = commonHelpers.getEntity(PLATFORM_OPERATIONS_PATH, platformOperationId);
        if (getResponse.getStatusCode() == SC_OK) {
            commonHelpers.entityIsDeleted(PLATFORM_OPERATIONS_PATH, platformOperationId);
            commonHelpers.getEntity(PLATFORM_OPERATIONS_PATH, platformOperationId);
            responseCodeIs(SC_NOT_FOUND);
        }
    }
}
