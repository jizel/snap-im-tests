package travel.snapshot.dp.qa.easyTests.suites;

/**
 * Run any classes in as test suite using JUNit Suite
 */

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import travel.snapshot.dp.qa.easyTests.tests.customers.YamlCustomer;

@RunWith(Suite.class)
@SuiteClasses({YamlCustomer.class})
// TODO: multithreading, investigate how it affects test run. Can the annotation be moved to property file?
// @Concurrent(threads="4x")
public class easyTestSuite {

    @BeforeClass
    public static void setUp() {
    }

    @AfterClass
    public static void tearDown() {
        // Do any setting after the whole suites is performed
    }
}
