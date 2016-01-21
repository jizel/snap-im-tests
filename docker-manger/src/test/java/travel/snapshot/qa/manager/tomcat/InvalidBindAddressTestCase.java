package travel.snapshot.qa.manager.tomcat;

import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.execution.ExecutionException;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;
import travel.snapshot.qa.manager.tomcat.spacelift.TomcatStarter;

/**
 * This test executes another Tomcat instance which gets terminated upon JVM shutdown and we do not have any possibility
 * to terminate it here which would clash with other tests. We are just checking the right exception. If you want to
 * execute this test, execute just this one.
 */
@RunWith(JUnit4.class)
@Ignore
public class InvalidBindAddressTestCase {

    private static final String INVALID_BIND_ADDRESS = "locahost"; // typo involved

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void invalidBindAddressTest() {

        expectedException.expect(ExecutionException.class);
        expectedException.expectMessage("Execution of a task failed. Unable to trigger condition within 5 seconds.");

        final TomcatManagerConfiguration configuration = new TomcatManagerConfiguration.Builder()
                .setBindAddress(INVALID_BIND_ADDRESS)
                .setBindPort(8080)
                .setStartupTimeoutInSeconds(5)
                .build();

        Spacelift.task(configuration, TomcatStarter.class).execute().await();
    }
}
