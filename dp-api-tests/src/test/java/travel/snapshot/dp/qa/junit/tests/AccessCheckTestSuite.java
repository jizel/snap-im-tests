package travel.snapshot.dp.qa.junit.tests;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectPackages("travel.snapshot.dp.qa.junit.tests.identity.access_checks")
public class AccessCheckTestSuite {
}