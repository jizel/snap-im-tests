package travel.snapshot.dp.qa.serenity.applications;

import com.jayway.restassured.response.Response;
import net.thucydides.core.annotations.Step;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.BasicSteps;

/**
 * Created by zelezny on 2/16/2017.
 */
public class ApplicationVersionsSteps extends BasicSteps {

    private static final String SESSION_CREATED_APPLICATION = "created_application";
    private static final String SESSION_APPLICATIONS = "applications";
    private static final String SESSION_APPLICATION_ID = "application_id";
    private static final String SESSION_APPLICATION_VERSION_ID = "version_id";
    private static final String SESSION_CREATED_APPLICATION_VERSIONS = "created_application_version";
    private static final String APPLICATIONS_VERSIONS_PATH = "/identity/application_versions";

    public ApplicationVersionsSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(APPLICATIONS_VERSIONS_PATH);
    }


    @Step
    public Response getApplicationVersion(String versionId){
        return getApplicationVersionByUser(DEFAULT_SNAPSHOT_USER_ID, versionId);
    }

    @Step
    public Response getApplicationVersionByUser(String requestorId, String versionId){
        Response response = getEntityByUser(requestorId, versionId);
        setSessionResponse(response);
        return response;
    }
}
