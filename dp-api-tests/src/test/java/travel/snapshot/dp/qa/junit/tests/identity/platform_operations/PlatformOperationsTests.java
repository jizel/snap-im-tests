package travel.snapshot.dp.qa.junit.tests.identity.platform_operations;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.UUID.randomUUID;
import static javax.servlet.http.HttpServletResponse.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PLATFORM_OPERATIONS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.numberOfEntitiesInResponse;

import com.jayway.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import qa.tools.ikeeper.annotation.Jira;
import travel.snapshot.dp.api.identity.model.PlatformOperationDto;
import travel.snapshot.dp.api.identity.model.PlatformOperationUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tests for /identity/platform_operations endpoint (DPIM-6)
 *
 * PlatformOperations table cannot be deleted (IM DB migration would have to be run again) so every test must clean what
 * it creates.
 */
public class PlatformOperationsTests extends CommonTest {

    //    Those constats should be replaced by enum created in DP Identity project when DPIM-6 is Done.
    private static final String GET_METHOD = "GET";
    private static final String POST_METHOD = "POST";
    private static final String PATCH_METHOD = "PATCH";
    private static final String DELETE_METHOD = "DELETE";
    private static final String TEST_URI_TEMPLATE = "/test/uri/template";

    private UUID platformOperationId;
    private PlatformOperationDto testPlatformOperationDto;
    private PlatformOperationDto createdPlatformOperationDto;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        platformOperationId = randomUUID();
        testPlatformOperationDto = constructPlatformOperation(platformOperationId, GET_METHOD, TEST_URI_TEMPLATE);
        createdPlatformOperationDto = commonHelpers.entityWithTypeIsCreated(PLATFORM_OPERATIONS_PATH, PlatformOperationDto.class,
                constructPlatformOperation(platformOperationId, GET_METHOD, TEST_URI_TEMPLATE));
    }

    @After
    public void tearDown() throws Exception {
        deletePlatformOperationIfExists(platformOperationId);
    }

    @Test
    public void getPlatformOperationsTest() throws Exception {
//        GetAll
        Response response = commonHelpers.getEntities(PLATFORM_OPERATIONS_PATH, null);
        responseCodeIs(SC_OK);
        stream(response.as(PlatformOperationDto[].class)).forEach(PlatformOperationsTests::platformOperationContainsAllFields);
//        Get single PO
        response = commonHelpers.getEntity(PLATFORM_OPERATIONS_PATH, testPlatformOperationDto.getId());
        responseCodeIs(SC_OK);
        assertThat(response.as(PlatformOperationDto.class), is(createdPlatformOperationDto));
    }

    @Test
    @Jira("DPIM-6")
    public void platformOperationsFilteringTest() throws Exception {
        Response response = commonHelpers.getEntities(PLATFORM_OPERATIONS_PATH, buildQueryParamMapForPaging(
                "5",
                null,
                "http_method==" + DELETE_METHOD,
                "uri_template",
                null,
                null));
        responseCodeIs(SC_OK);
        numberOfEntitiesInResponse(PlatformOperationDto.class, 5);
        stream(response.as(PlatformOperationDto[].class)).forEach(po -> assertThat(po.getHttpMethod(), is(DELETE_METHOD)));
    }

//    TODO: make invalid filter params test as well when DPIM is Done

    @Test
    public void createAndGetPlatformOperationTest() throws Exception {
        asList(GET_METHOD, POST_METHOD, PATCH_METHOD, DELETE_METHOD).forEach(method -> {
            UUID id = randomUUID();
            String uriTemplate = String.format("/test/uri/template{%s}", id);
            PlatformOperationDto platformOperationDto = constructPlatformOperation(id, method, uriTemplate);

            Response createResponse = commonHelpers.createEntity(PLATFORM_OPERATIONS_PATH, platformOperationDto);
            responseCodeIs(SC_CREATED);

            PlatformOperationDto createdPlatformOperation = createResponse.as(PlatformOperationDto.class);
            platformOperationContainsAllFields(createdPlatformOperation);
            assertThat(createdPlatformOperation.getId(), is(id));
            assertThat(createdPlatformOperation.getHttpMethod(), is(method));
            assertThat(createdPlatformOperation.getUriTemplate(), is(uriTemplate));

            PlatformOperationDto requestedPlatformOperation = commonHelpers.getEntityAsType(PLATFORM_OPERATIONS_PATH, PlatformOperationDto.class, id);
            assertThat(requestedPlatformOperation, is(createdPlatformOperation));

            deletePlatformOperationIfExists(id);
        });
    }

    @Test
    public void updatePlatformOperationTest() throws Exception {
        String updatedUriTemplate = "updated/template{update}";
        PlatformOperationUpdateDto update = new PlatformOperationUpdateDto();
        update.setHttpMethod(POST_METHOD);
        update.setUriTemplate(updatedUriTemplate);
        Response udpateResponse = commonHelpers.updateEntity(PLATFORM_OPERATIONS_PATH, platformOperationId, update);
        responseCodeIs(SC_OK);

        PlatformOperationDto updatedPlatformOperation = udpateResponse.as(PlatformOperationDto.class);
        platformOperationContainsAllFields(updatedPlatformOperation);
        assertThat(updatedPlatformOperation.getId(), is(platformOperationId));
        assertThat(updatedPlatformOperation.getHttpMethod(), is(POST_METHOD));
        assertThat(updatedPlatformOperation.getUriTemplate(), is(updatedUriTemplate));
    }

//    Negative scenarios

    @Test
    @Jira("DPIM-6")
    public void checkPlatformOperationsCreateErrorCodes() throws Exception {
        UUID errorTestId = randomUUID();
        PlatformOperationDto errorTestPO = testPlatformOperationDto;
        errorTestPO.setId(errorTestId);
//        Duplicate values
        commonHelpers.createEntity(PLATFORM_OPERATIONS_PATH, errorTestPO);
        verifyResponseAndCustomCode(SC_CONFLICT, CC_CONFLICT_VALUES);
//        Missing httpMethod
        errorTestPO.setHttpMethod(null);
        commonHelpers.createEntity(PLATFORM_OPERATIONS_PATH, errorTestPO);
        responseCodeIs(SC_BAD_REQUEST);
//        Missing uriTemplate
        errorTestPO.setHttpMethod(GET_METHOD);
        errorTestPO.setUriTemplate(null);
        commonHelpers.createEntity(PLATFORM_OPERATIONS_PATH, errorTestPO);
        responseCodeIs(SC_BAD_REQUEST);
    }

    @Test
    @Jira("DPIM-6")
    public void checkPlatformOperationsUpdateErrorCodes() throws Exception {
        Map<String, String> invalidUpdate = new HashMap<>();
        commonHelpers.updateEntity(PLATFORM_OPERATIONS_PATH, randomUUID(), invalidUpdate);
        responseCodeIs(SC_NOT_FOUND);

        invalidUpdate.put("http_method", "definitely not RFC HTTP method");
        commonHelpers.updateEntity(PLATFORM_OPERATIONS_PATH, platformOperationId, invalidUpdate);
        responseCodeIs(SC_BAD_REQUEST);
    }

    //    Private help methods
    private static void platformOperationContainsAllFields(PlatformOperationDto platformOperation) {
        assertNotNull(platformOperation.getId());
        assertNotNull(platformOperation.getVersion());
        assertNotNull(platformOperation.getHttpMethod());
        assertNotNull(platformOperation.getUriTemplate());
    }

    private PlatformOperationDto constructPlatformOperation(UUID id, String httpMethod, String uriTemplate) {
        PlatformOperationDto platformOperation = new PlatformOperationDto();
        platformOperation.setId(id);
        platformOperation.setHttpMethod(httpMethod);
        platformOperation.setUriTemplate(uriTemplate);
        return platformOperation;
    }

    private void deletePlatformOperationIfExists(UUID platformOperationId) {
        Response getResponse = commonHelpers.getEntity(PLATFORM_OPERATIONS_PATH, platformOperationId);
        if (getResponse.getStatusCode() == SC_OK) {
            commonHelpers.entityIsDeleted(PLATFORM_OPERATIONS_PATH, platformOperationId);
            responseCodeIs(SC_NO_CONTENT);
            commonHelpers.getEntity(PLATFORM_OPERATIONS_PATH, platformOperationId);
            responseCodeIs(SC_NOT_FOUND);
        }
    }
}
