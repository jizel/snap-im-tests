package travel.snapshot.dp.qa.junit.tests.common;

import travel.snapshot.dp.qa.junit.helpers.CustomerHelpers;
import travel.snapshot.dp.qa.junit.helpers.PropertyHelpers;
import travel.snapshot.dp.qa.junit.loaders.EntitiesLoader;
import travel.snapshot.dp.qa.cucumber.serenity.BasicSteps;
import travel.snapshot.dp.qa.cucumber.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.cucumber.serenity.jms.JmsSteps;
import travel.snapshot.dp.qa.cucumber.steps.DbStepDefs;

public class CommonTest extends BasicSteps{
    protected static String YAML_DATA_PATH = "src/test/resources/yaml/%s";

    //    Get EntitiesLoader instance containing all test entity data
    protected static final EntitiesLoader entitiesLoader = EntitiesLoader.getInstance();

    protected static DbStepDefs dbStepDefs = new DbStepDefs();
    protected static final CustomerSteps customerSteps = new CustomerSteps();
    protected static final CustomerHelpers customerHelpers = new CustomerHelpers();
    protected static final PropertyHelpers propertyHelpers = new PropertyHelpers();
    protected static final JmsSteps jmsSteps = new JmsSteps();
}
