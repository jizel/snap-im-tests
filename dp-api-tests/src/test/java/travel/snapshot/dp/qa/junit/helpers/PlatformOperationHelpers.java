package travel.snapshot.dp.qa.junit.helpers;

import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PLATFORM_OPERATIONS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsType;

import travel.snapshot.dp.api.identity.model.PlatformOperationCreateDto;
import travel.snapshot.dp.api.identity.model.PlatformOperationDto;
import travel.snapshot.dp.api.type.HttpMethod;

import java.util.Map;
import java.util.UUID;

/**
 * Helpers for Platform Operation tests
 */
public class PlatformOperationHelpers {

    private final CommonHelpers commonHelpers = new CommonHelpers();
    private final AuthorizationHelpers authorizationHelpers = new AuthorizationHelpers();

    public PlatformOperationCreateDto constructPlatformOperation(HttpMethod httpMethod, String uriTemplate) {
        PlatformOperationCreateDto platformOperation = new PlatformOperationCreateDto();
        platformOperation.setHttpMethod(httpMethod);
        platformOperation.setUriTemplate(uriTemplate);
        return platformOperation;
    }

    public static void platformOperationContainsAllFields(PlatformOperationDto platformOperation) {
        assertNotNull(platformOperation.getId());
        assertNotNull(platformOperation.getVersion());
        assertNotNull(platformOperation.getHttpMethod());
        assertNotNull(platformOperation.getUriTemplate());
    }

    public UUID getPlatformOperationId(HttpMethod method, String uriTemplate, Boolean withAuth) {
        Map<String, String> params = buildQueryParamMapForPaging(
                null,
                null,
                String.format("http_method==%s and uri_template==%s", method.toString(), uriTemplate),
                null,
                null,
                null);
        if (withAuth.equals(true)) {
            return authorizationHelpers.getEntitiesAsType(PLATFORM_OPERATIONS_PATH, PlatformOperationDto.class, params)
                    .get(0)
                    .getId();
        }
        else {
            return commonHelpers.getEntitiesAsType(PLATFORM_OPERATIONS_PATH, PlatformOperationDto.class, params)
                    .get(0)
                    .getId();
        }
    }
}
