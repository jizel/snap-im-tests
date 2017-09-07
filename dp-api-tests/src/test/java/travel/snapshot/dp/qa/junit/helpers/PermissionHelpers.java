package travel.snapshot.dp.qa.junit.helpers;

import travel.snapshot.dp.api.identity.model.ApplicationPermissionDto;

import java.util.UUID;

/**
 * Helper methods for all permissions endpoints
 * - /identity/application_permissions
 * - /identity/effective_permissions
 */
public class PermissionHelpers {

    public ApplicationPermissionDto constructAppPermission(UUID applicationId, UUID platformOperationId) {
        ApplicationPermissionDto applicationPermissionDto = new ApplicationPermissionDto();
        applicationPermissionDto.setApplicationId(applicationId);
        applicationPermissionDto.setPlatformOperationId(platformOperationId);

        return applicationPermissionDto;
    }
}
