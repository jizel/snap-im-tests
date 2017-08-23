package travel.snapshot.dp.qa.junit.tests;

import com.googlecode.junittoolbox.ExcludeCategories;
import com.googlecode.junittoolbox.SuiteClasses;
import com.googlecode.junittoolbox.WildcardPatternSuite;
import org.junit.runner.RunWith;


/**
 * Test suite that runs all JUnit tests including those annotated by SlowTests class.
 *
 * This class has to be located in a super package of all the classes it runs.
 */

@RunWith(WildcardPatternSuite.class)
@SuiteClasses("*/*.class")
@ExcludeCategories(Categories.Authorization.class)
public class JUnitSlowTestSuite {

}
