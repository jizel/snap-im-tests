package travel.snapshot.dp.qa.junit.tests.identity.platform_operations;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PLATFORM_OPERATIONS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import travel.snapshot.dp.api.identity.model.PlatformOperationCreateDto;
import travel.snapshot.dp.api.identity.model.PlatformOperationDto;
import travel.snapshot.dp.api.identity.model.PlatformOperationUpdateDto;
import travel.snapshot.dp.api.type.HttpMethod;
import travel.snapshot.dp.qa.junit.tests.Categories;
import travel.snapshot.dp.qa.junit.tests.common.CommonPlatformOperationTest;

import java.util.List;

/**
 * Identity Platform Operations cannot be created, updated or deleted according to DPIM-76.
 */
public class IdentityPlatformOperationsTests extends CommonPlatformOperationTest {

    @Test
    public void identityPermissionCannotBeCreated() {
        for (HttpMethod method : HttpMethod.values()) {
            PlatformOperationCreateDto platformOperationDto = platformOperationHelpers.constructPlatformOperation(method, TEST_IDENTITY_URI_TEMPLATE);
            commonHelpers.createEntity(PLATFORM_OPERATIONS_PATH, platformOperationDto);
            responseCodeIs(SC_FORBIDDEN);
        }
    }

    @Test
    public void anyPermissionCannotBeUpdatedToIdentity() {
        String updatedUriTemplate = "/identity/updated/uri";
        PlatformOperationUpdateDto update = new PlatformOperationUpdateDto();
        update.setUriTemplate(updatedUriTemplate);
        commonHelpers.updateEntity(PLATFORM_OPERATIONS_PATH, createdPlatformOperation.getId(), update);
        responseCodeIs(SC_FORBIDDEN);
    }

    @Test
    @Category(Categories.SlowTests.class)
    public void identityPermissionCannotBeUpdatedOrDeleted() {
        List<PlatformOperationDto> allIdentityPlatformOperations = commonHelpers.getEntitiesAsType(PLATFORM_OPERATIONS_PATH, PlatformOperationDto.class,
                buildQueryParamMapForPaging(null, null, "uri_template==/identity/*", "uri_template", null, null));

        allIdentityPlatformOperations.forEach(po -> {
            commonHelpers.updateEntity(PLATFORM_OPERATIONS_PATH, po.getId(), new PlatformOperationUpdateDto());
            responseCodeIs(SC_FORBIDDEN);
            commonHelpers.deleteEntity(PLATFORM_OPERATIONS_PATH, po.getId());
            responseCodeIs(SC_FORBIDDEN);
        });
    }

}
