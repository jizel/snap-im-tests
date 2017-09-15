package travel.snapshot.dp.qa.junit.tests.common;

import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATION_VERSIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.COMMERCIAL_SUBSCRIPTIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_ROLES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_ROLES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PASSWORD;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_USER_NAME;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.ENTITIES_TO_DELETE;

import net.serenitybdd.core.Serenity;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import travel.snapshot.dp.qa.junit.loaders.EntitiesLoader;
import travel.snapshot.dp.qa.junit.tests.Categories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Non destructive smoke tests.
 *
 * Test classes extending this class use keycloak authorization and clean everything they made so they can be
 * performed on various environments without deleting whole DB.
 */
@RunWith(SerenityRunner.class)
@Category(Categories.Authorization.class)
public class CommonSmokeTest extends CommonTest {


    @Before
    public void setUp() throws Exception {
        // To clean after normal tests we first remove all test entities
        dbStepDefs.defaultEntitiesAreDeleted();
        dbStepDefs.defaultEntitiesAreCreated();
        EntitiesLoader loader = entitiesLoader.getInstance();
        loadDefaultTestEntities();
        Map<String, Object> testClient1 = loader.getClients().get("client1");
        keycloakHelpers.createClient(testClient1);
        String clientId = (String) testClient1.get("clientId");
        String clientSecret = (String) testClient1.get("secret");
        authorizationHelpers.getToken(DEFAULT_SNAPSHOT_USER_NAME, DEFAULT_PASSWORD, clientId, clientSecret);
        Map<String, List<String>> thingsToDelete = new HashMap<>();
        Serenity.setSessionVariable(ENTITIES_TO_DELETE).to(thingsToDelete);
    }

    @After
    public void cleanUp() throws Throwable {
        removeCreatedEntities(Serenity.sessionVariableCalled(ENTITIES_TO_DELETE));
        dbStepDefs.defaultEntitiesAreDeleted();
    }



    private void removeCreatedEntities(Map<String, List<UUID>> registry) {

        List<UUID> commercialSubscriptionIds = registry.getOrDefault(COMMERCIAL_SUBSCRIPTIONS_PATH, new ArrayList<UUID>());
        List<UUID> customerRoleIds = registry.getOrDefault(USER_CUSTOMER_ROLES_PATH, new ArrayList<UUID>());
        List<UUID> propertyRoleIds = registry.getOrDefault(USER_PROPERTY_ROLES_PATH, new ArrayList<UUID>());
        List<UUID> userGroupIds = registry.getOrDefault(USER_GROUPS_PATH, new ArrayList<UUID>());
        List<UUID> userPropertySetIds = registry.getOrDefault(USER_PROPERTY_SET_RELATIONSHIPS_PATH, new ArrayList<UUID>());
        List<UUID> userPropertyIds = registry.getOrDefault(USER_PROPERTY_RELATIONSHIPS_PATH, new ArrayList<UUID>());
        List<UUID> propertySetPropertyIds = registry.getOrDefault(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, new ArrayList<UUID>());
        List<UUID> propertyIds = registry.getOrDefault(PROPERTIES_PATH, new ArrayList<UUID>());
        List<UUID> propertySetIds = registry.getOrDefault(PROPERTY_SETS_PATH, new ArrayList<UUID>());
        List<UUID> customerPropertyIds = registry.getOrDefault(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, new ArrayList<UUID>());
        List<UUID> customerUserIds = registry.getOrDefault(USER_CUSTOMER_RELATIONSHIPS_PATH, new ArrayList<UUID>());
        List<UUID> customerIds = registry.getOrDefault(CUSTOMERS_PATH, new ArrayList<UUID>());
        List<UUID> userIds = registry.getOrDefault(USERS_PATH, new ArrayList<UUID>());
        List<UUID> applicationIds = registry.getOrDefault(APPLICATIONS_PATH, new ArrayList<UUID>());
        List<UUID> appVersionIds = registry.getOrDefault(APPLICATION_VERSIONS_PATH, new ArrayList<UUID>());

        customerRoleIds.forEach(dbSteps::deleteRole);
        propertyRoleIds.forEach(dbSteps::deleteRole);
        commercialSubscriptionIds.forEach(dbSteps::deleteCommercialSubscription);
        appVersionIds.forEach(dbSteps::deleteAppVersion);
        applicationIds.forEach(dbSteps::deleteApplication);
        userGroupIds.forEach(dbSteps::deleteUserPropertySet);
        userPropertySetIds.forEach(dbSteps::deleteUserPropertySet);
        userPropertyIds.forEach(dbSteps::deleteUserProperty);
        propertySetPropertyIds.forEach(dbSteps::deletePropertySetProperty);
        propertySetIds.forEach(dbSteps::deletePropertySet);
        customerPropertyIds.forEach(dbSteps::deleteCustomerProperty);
        propertyIds.forEach(dbSteps::deleteProperty);
        customerUserIds.forEach(dbSteps::deleteCustomerUser);
        customerIds.forEach(dbSteps::deleteCustomer);
        userIds.forEach(dbSteps::deleteUser);
        dbSteps.deleteAddress();
    }
}

