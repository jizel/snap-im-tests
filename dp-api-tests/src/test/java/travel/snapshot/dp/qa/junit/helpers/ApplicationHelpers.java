package travel.snapshot.dp.qa.junit.helpers;


/**
 * Created by zelezny on 6/30/2017.
 */

import lombok.extern.java.Log;
import travel.snapshot.dp.qa.cucumber.serenity.DbUtilsSteps;
import travel.snapshot.dp.qa.cucumber.serenity.applications.ApplicationsSteps;

import java.util.UUID;

@Log
public class ApplicationHelpers extends ApplicationsSteps {

    public ApplicationHelpers() { super();}
    private DbUtilsSteps dbSteps = new DbUtilsSteps();


    public void grantAllPermissions(UUID applicationId) {
        dbSteps.populateApplicationPermissionsTableForApplication(applicationId);
    }
}
