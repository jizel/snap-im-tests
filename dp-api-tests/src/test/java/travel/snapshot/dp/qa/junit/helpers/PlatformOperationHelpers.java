package travel.snapshot.dp.qa.junit.helpers;

import static org.junit.Assert.*;

import travel.snapshot.dp.api.identity.model.PlatformOperationDto;
import travel.snapshot.dp.api.type.HttpMethod;

/**
 * Helpers for Platform Operation tests
 */
public class PlatformOperationHelpers {

    public PlatformOperationDto constructPlatformOperation(HttpMethod httpMethod, String uriTemplate) {
        PlatformOperationDto platformOperation = new PlatformOperationDto();
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
