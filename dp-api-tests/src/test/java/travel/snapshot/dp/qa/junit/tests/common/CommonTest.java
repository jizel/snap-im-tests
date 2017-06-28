package travel.snapshot.dp.qa.junit.tests.common;

import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerRoleDto;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
import travel.snapshot.dp.qa.cucumber.serenity.BasicSteps;
import travel.snapshot.dp.qa.cucumber.serenity.configuration.ConfigurationSteps;
import travel.snapshot.dp.qa.cucumber.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.cucumber.serenity.jms.JmsSteps;
import travel.snapshot.dp.qa.cucumber.serenity.partners.PartnerSteps;
import travel.snapshot.dp.qa.cucumber.serenity.properties.PropertySteps;
import travel.snapshot.dp.qa.cucumber.serenity.property_sets.PropertySetSteps;
import travel.snapshot.dp.qa.cucumber.serenity.roles.RoleBaseSteps;
import travel.snapshot.dp.qa.cucumber.serenity.users.UsersSteps;
import travel.snapshot.dp.qa.cucumber.steps.DbStepDefs;
import travel.snapshot.dp.qa.junit.helpers.CustomerHelpers;
import travel.snapshot.dp.qa.junit.helpers.PartnerHelpers;
import travel.snapshot.dp.qa.junit.helpers.PropertyHelpers;
import travel.snapshot.dp.qa.junit.helpers.PropertySetHelpers;
import travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers;
import travel.snapshot.dp.qa.junit.helpers.RoleHelpers;
import travel.snapshot.dp.qa.junit.helpers.UserGroupHelpers;
import travel.snapshot.dp.qa.junit.helpers.UserHelpers;
import travel.snapshot.dp.qa.junit.loaders.EntitiesLoader;

public class CommonTest extends BasicSteps{
    protected static String YAML_DATA_PATH = "src/test/resources/yaml/%s";
    protected static final String NOTIFICATION_CRUD_TOPIC = "Notifications.crud";
    protected static final String JMS_SUBSCRIPTION_NAME = "Test";

    //    Get EntitiesLoader instance containing all test entity data
    protected static final EntitiesLoader entitiesLoader = EntitiesLoader.getInstance();

    //    Steps
    protected static DbStepDefs dbStepDefs = new DbStepDefs();
    protected static final CustomerSteps customerSteps = new CustomerSteps();
    protected static final JmsSteps jmsSteps = new JmsSteps();
    protected static final PropertySteps propertySteps = new PropertySteps();
    protected static final UsersSteps userSteps = new UsersSteps();
    protected static final RoleBaseSteps roleBaseSteps = new RoleBaseSteps();
    protected static final PropertySetSteps propertySetSteps = new PropertySetSteps();
    protected static final PartnerSteps partnerSteps = new PartnerSteps();
    protected static final ConfigurationSteps configurationSteps = new ConfigurationSteps();

    //    Helpers
    protected static final CustomerHelpers customerHelpers = new CustomerHelpers();
    protected static final PropertyHelpers propertyHelpers = new PropertyHelpers();
    protected static final PropertySetHelpers propertySetHelpers = new PropertySetHelpers();
    protected static final UserHelpers userHelpers = new UserHelpers();
    protected static final RoleHelpers roleHelpers = new RoleHelpers();
    protected static final PartnerHelpers partnerHelpers = new PartnerHelpers();
    protected static final RelationshipsHelpers relationshipsHelpers = new RelationshipsHelpers();
    protected static final UserGroupHelpers userGroupHelpers = new UserGroupHelpers();

//    Basic test entities
    protected static final UserCreateDto testUser1 = entitiesLoader.getUserDtos().get("user1");
    protected static final UserCreateDto testSnapshotUser1 = entitiesLoader.getSnapshotUserDtos().get("snapshotUser1");
    protected static final CustomerCreateDto testCustomer1 = entitiesLoader.getCustomerDtos().get("customer1");
    protected static final PropertyDto testProperty1 = entitiesLoader.getPropertyDtos().get("property1");
    protected static final PropertyDto testProperty2 = entitiesLoader.getPropertyDtos().get("property2");
    protected static final PropertySetDto testPropertySet1 = entitiesLoader.getPropertySetDtos().get("propertySet1");
    protected static final CustomerRoleDto testCustomerRole1 = entitiesLoader.getCustomerRoleDtos().get("customerRole1");
    protected static final PartnerDto testPartner1 = entitiesLoader.getPartnerDtos().get("partner1");
    protected static final UserGroupDto testUserGroup1 = entitiesLoader.getUserGroupDtos().get("user_group1");
}
