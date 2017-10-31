package travel.snapshot.dp.qa.junit.tests;

import com.googlecode.junittoolbox.ExcludeCategories;
import com.googlecode.junittoolbox.SuiteClasses;
import com.googlecode.junittoolbox.WildcardPatternSuite;
import org.junit.runner.RunWith;
import travel.snapshot.dp.qa.junit.tests.Categories;

/**
 * JUnit test suite that runs all tests in the identity package.
 *
 * This class has to be located in a super package of all the classes it runs.
 */
@RunWith(WildcardPatternSuite.class)
@SuiteClasses("**/identity/**/*.class")
@ExcludeCategories(Categories.Authorization.class)
public class IdentitySlowSuite {

}
