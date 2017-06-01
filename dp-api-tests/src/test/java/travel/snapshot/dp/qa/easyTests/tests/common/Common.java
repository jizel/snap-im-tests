package travel.snapshot.dp.qa.easyTests.tests.common;

import travel.snapshot.dp.qa.easyTests.tests.customers.CustomerHelpers;
import travel.snapshot.dp.qa.serenity.BasicSteps;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.steps.DbStepDefs;

public class Common extends BasicSteps{
    protected static String YAML_DATA_PATH = "src/test/resources/yaml/%s";

    protected static DbStepDefs dbStepDefs = new DbStepDefs();
    protected static final CustomerSteps customerSteps = new CustomerSteps();
    protected static final CustomerHelpers customerHelpers = new CustomerHelpers();
}
