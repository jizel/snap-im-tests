package travel.snapshot.dp.qa.easyTests.tests.common;

import travel.snapshot.dp.qa.serenity.BasicSteps;
import travel.snapshot.dp.qa.steps.DbStepDefs;

public class Common extends BasicSteps{
    public static DbStepDefs dbStepDefs = new DbStepDefs();
    public static String YAML_DATA_PATH = "src/test/resources/yaml/%s";
}
