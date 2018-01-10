package travel.snapshot.dp.qa.junit.tests;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@IncludeEngines({"junit-jupiter", "junit-vintage"})
@SelectPackages("travel.snapshot.dp.qa.junit.tests.notification")
public class NotificationTestSuite {
    
}
