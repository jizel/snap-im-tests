package travel.snapshot.dp.qa.junit.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import travel.snapshot.dp.qa.junit.tests.configurations.ConfigurationTypesTests;
import travel.snapshot.dp.qa.junit.tests.configurations.ConfigurationsTests;

/**
 * Test suite for configuration module
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ConfigurationsTests.class, ConfigurationTypesTests.class})
public class ConfigurationsTestSuite {

}