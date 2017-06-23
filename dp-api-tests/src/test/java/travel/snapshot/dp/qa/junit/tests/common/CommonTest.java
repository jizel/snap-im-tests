package travel.snapshot.dp.qa.junit.tests.common;

import travel.snapshot.dp.qa.cucumber.serenity.BasicSteps;
import travel.snapshot.dp.qa.cucumber.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.cucumber.serenity.jms.JmsSteps;
import travel.snapshot.dp.qa.cucumber.serenity.properties.PropertySteps;
import travel.snapshot.dp.qa.cucumber.steps.DbStepDefs;
import travel.snapshot.dp.qa.junit.helpers.CustomerHelpers;
import travel.snapshot.dp.qa.junit.helpers.PropertyHelpers;
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

    //    Helpers
    protected static final CustomerHelpers customerHelpers = new CustomerHelpers();
    protected static final PropertyHelpers propertyHelpers = new PropertyHelpers();
    protected static final UserHelpers userHelpers = new UserHelpers();
}
