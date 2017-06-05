package travel.snapshot.dp.qa.junit.tests.common;

import travel.snapshot.dp.qa.junit.helpers.CustomerHelpers;
import travel.snapshot.dp.qa.junit.loaders.EntitiesLoader;
import travel.snapshot.dp.qa.serenity.BasicSteps;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.steps.DbStepDefs;

public class Common extends BasicSteps{
    protected static String YAML_DATA_PATH = "src/test/resources/yaml/%s";

    //    Get EntitiesLoader instance containing all test entity data
    protected static final EntitiesLoader entitiesLoader = EntitiesLoader.getInstance();

    protected static DbStepDefs dbStepDefs = new DbStepDefs();
    protected static final CustomerSteps customerSteps = new CustomerSteps();
    protected static final CustomerHelpers customerHelpers = new CustomerHelpers();
}
