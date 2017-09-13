package travel.snapshot.dp.qa.junit.tests;

import com.googlecode.junittoolbox.WildcardPatternSuite;
import com.googlecode.junittoolbox.SuiteClasses;
import org.junit.runner.RunWith;

@RunWith(WildcardPatternSuite.class)
@SuiteClasses("identity/smoke/*.class")
public class NonDestructiveSmokeTestSuite {

}
