package travel.snapshot.dp.qa.junit.tests.identity.platform_operations;

import static java.util.Arrays.stream;
import static java.util.UUID.randomUUID;
import static javax.servlet.http.HttpServletResponse.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PLATFORM_OPERATIONS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.numberOfEntitiesInResponse;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntities;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityAsType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntity;
import static travel.snapshot.dp.qa.junit.helpers.PlatformOperationHelpers.platformOperationContainsAllFields;

import com.jayway.restassured.response.Response;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.PlatformOperationCreateDto;
import travel.snapshot.dp.api.identity.model.PlatformOperationDto;
import travel.snapshot.dp.api.identity.model.PlatformOperationUpdateDto;
import travel.snapshot.dp.api.type.HttpMethod;
import travel.snapshot.dp.qa.junit.helpers.PlatformOperationHelpers;
import travel.snapshot.dp.qa.junit.tests.common.CommonPlatformOperationTest;

import java.util.HashMap;
import java.util.Map;

/**
 * Tests for /identity/platform_operations endpoint (DPIM-6)
 *
 * PlatformOperations table cannot be deleted (IM DB migration would have to be run again) so every test must clean what
 * it creates.
 */
public class PlatformOperationsTests extends CommonPlatformOperationTest {

    @Test
    public void getPlatformOperationsTest() throws Exception {
//        GetAll
        Response response = getEntities(PLATFORM_OPERATIONS_PATH, null);
        responseCodeIs(SC_OK);
        stream(response.as(PlatformOperationDto[].class)).forEach(PlatformOperationHelpers::platformOperationContainsAllFields);
//        Get single PO
        response = getEntity(PLATFORM_OPERATIONS_PATH, createdPlatformOperation.getId());
        responseCodeIs(SC_OK);
        assertThat(response.as(PlatformOperationDto.class), is(createdPlatformOperation));
    }

    @Test
    public void platformOperationsFilteringTest() throws Exception {
        Response response = getEntities(PLATFORM_OPERATIONS_PATH, buildQueryParamMapForPaging(
                "5",
                null,
                "http_method==" + HttpMethod.DELETE,
                "uri_template",
                null,
                null));
        responseCodeIs(SC_OK);
        numberOfEntitiesInResponse(PlatformOperationDto.class, 5);
        stream(response.as(PlatformOperationDto[].class)).forEach(po -> assertThat(po.getHttpMethod(), is(HttpMethod.DELETE)));
    }

//    TODO: make invalid filter params test as well when DPIM is Done

    @Test
    public void createAndGetPlatformOperationTest() throws Exception {
        for (HttpMethod method : HttpMethod.values()) {
            PlatformOperationCreateDto platformOperationDto = platformOperationHelpers.constructPlatformOperation(method, TEST_URI_TEMPLATE + "/{create}");

            Response createResponse = createEntity(PLATFORM_OPERATIONS_PATH, platformOperationDto);
            responseCodeIs(SC_CREATED);

            PlatformOperationDto createdPlatformOperation = createResponse.as(PlatformOperationDto.class);
            platformOperationContainsAllFields(createdPlatformOperation);
            assertThat(createdPlatformOperation.getHttpMethod(), is(method));
            assertThat(createdPlatformOperation.getUriTemplate(), is(TEST_URI_TEMPLATE + "/{create}"));

            PlatformOperationDto requestedPlatformOperation = getEntityAsType(PLATFORM_OPERATIONS_PATH, PlatformOperationDto.class, createdPlatformOperation.getId());
            assertThat(requestedPlatformOperation, is(createdPlatformOperation));

            deletePlatformOperationIfExists(createdPlatformOperation.getId());
        }
    }

    @Test
    public void updatePlatformOperationTest() throws Exception {
        String updatedUriTemplate = "/updated/template/{update}";
        PlatformOperationUpdateDto update = new PlatformOperationUpdateDto();
        update.setHttpMethod(HttpMethod.POST);
        update.setUriTemplate(updatedUriTemplate);
        Response udpateResponse = updateEntity(PLATFORM_OPERATIONS_PATH, createdPlatformOperation.getId(), update);
        responseCodeIs(SC_OK);

        PlatformOperationDto updatedPlatformOperation = udpateResponse.as(PlatformOperationDto.class);
        platformOperationContainsAllFields(updatedPlatformOperation);
        assertThat(updatedPlatformOperation.getId(), is(createdPlatformOperation.getId()));
        assertThat(updatedPlatformOperation.getHttpMethod(), is(HttpMethod.POST));
        assertThat(updatedPlatformOperation.getUriTemplate(), is(updatedUriTemplate));
    }

//    Negative scenarios

    @Test
    public void checkPlatformOperationsCreateErrorCodes() throws Exception {
//        Duplicate values
        createEntity(PLATFORM_OPERATIONS_PATH, platformOperationTestDto);
        verifyResponseAndCustomCode(SC_CONFLICT, CC_CONFLICT_VALUES);
//        Missing httpMethod
        PlatformOperationCreateDto errorTestPO = platformOperationHelpers.constructPlatformOperation(HttpMethod.GET, TEST_URI_TEMPLATE);
        errorTestPO.setHttpMethod(null);
        createEntity(PLATFORM_OPERATIONS_PATH, errorTestPO);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_SEMANTIC_ERRORS);
//        Missing uriTemplate
        errorTestPO.setHttpMethod(HttpMethod.GET);
        errorTestPO.setUriTemplate(null);
        createEntity(PLATFORM_OPERATIONS_PATH, errorTestPO);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_SEMANTIC_ERRORS);
    }

    @Test
    public void checkPlatformOperationsUpdateErrorCodes() throws Exception {
        Map<String, String> invalidUpdate = new HashMap<>();
        updateEntity(PLATFORM_OPERATIONS_PATH, randomUUID(), invalidUpdate);
        responseCodeIs(SC_NOT_FOUND);

        invalidUpdate.put("http_method", "definitely not RFC HTTP method");
        updateEntity(PLATFORM_OPERATIONS_PATH, createdPlatformOperation.getId(), invalidUpdate);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_SEMANTIC_ERRORS);
    }
}
