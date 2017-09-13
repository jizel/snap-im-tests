package travel.snapshot.dp.qa.junit.helpers;

import static org.junit.Assert.*;

import travel.snapshot.dp.api.identity.model.PlatformOperationCreateDto;
import travel.snapshot.dp.api.identity.model.PlatformOperationDto;
import travel.snapshot.dp.api.type.HttpMethod;

/**
 * Helpers for Platform Operation tests
 */
public class PlatformOperationHelpers {

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
}
