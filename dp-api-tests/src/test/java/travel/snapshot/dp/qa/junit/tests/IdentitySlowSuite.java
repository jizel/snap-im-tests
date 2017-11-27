package travel.snapshot.dp.qa.junit.tests;

import static travel.snapshot.dp.qa.junit.tests.Tags.AUTHORIZATION_TEST;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.ExcludePackages;
import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

/**
 * JUnit test suite that runs all tests in the identity package.
 *
 * This class has to be located in a super package of all the classes it runs.
 */
@RunWith(JUnitPlatform.class)
@SelectPackages("travel.snapshot.dp.qa.junit.tests.identity")
@ExcludePackages("travel.snapshot.dp.qa.junit.tests.identity.smoke")
@ExcludeTags(AUTHORIZATION_TEST)
public class IdentitySlowSuite {

}
