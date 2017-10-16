package travel.snapshot.dp.qa.junit.helpers;

import travel.snapshot.dp.api.identity.model.ApplicationPermissionCreateDto;

import java.util.UUID;

/**
 * Helper methods for all permissions endpoints
 * - /identity/application_permissions
 * - /identity/effective_permissions
 */
public class PermissionHelpers {

    public static ApplicationPermissionCreateDto constructAppPermission(UUID applicationId, UUID platformOperationId) {
        ApplicationPermissionCreateDto applicationPermissionDto = new ApplicationPermissionCreateDto();
        applicationPermissionDto.setApplicationId(applicationId);
        applicationPermissionDto.setPlatformOperationId(platformOperationId);

        return applicationPermissionDto;
    }
}
