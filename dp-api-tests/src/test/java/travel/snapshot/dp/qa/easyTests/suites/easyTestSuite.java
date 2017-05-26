package travel.snapshot.dp.qa.easyTests.suites;

/**
 * Created by zelezny on 5/2/2017.
 */

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import travel.snapshot.dp.qa.easyTests.tests.customers.YamlCustomer;

@RunWith(Suite.class)
@Suite.SuiteClasses({YamlCustomer.class})
// TODO: multithreading, investigate how it affects test run. Can the annotation be moved to property file?
// @Concurrent(threads="4x")
public class easyTestSuite {

    @BeforeClass
    public static void setUp() {
        // Do any setting before the whole suites starts
    }

    @AfterClass
    public static void tearDown() {
        // Do any setting after the whole suites is performed
    }
}
