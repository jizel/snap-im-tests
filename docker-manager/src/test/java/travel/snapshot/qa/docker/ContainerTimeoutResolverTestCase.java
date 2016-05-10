package travel.snapshot.qa.docker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static travel.snapshot.qa.manager.generic.impl.docker.GenericDockerManager.SERVICE_NAME;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import travel.snapshot.qa.category.UnitTest;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.manager.generic.configuration.GenericConfiguration;

import java.text.MessageFormat;
import java.util.function.ToLongBiFunction;

@Category(UnitTest.class)
public class ContainerTimeoutResolverTestCase {

    private final ToLongBiFunction<GenericConfiguration, String> timeoutResolver = (configuration, serviceType) ->
            DockerServiceManager.resolveTimeout(configuration.getStartupTimeoutInSeconds(), constructProperty(serviceType), serviceType);

    @Before
    public void setup() {

        final String connectionTimeout = getConnectionTimeoutSystemProperty(SERVICE_NAME);

        assertTrue("Connection timeout property is not null nor an empty String.",
                connectionTimeout == null || connectionTimeout.isEmpty());
    }

    @After
    public void teardown() {
        cleanConnectionTimeoutProperty(SERVICE_NAME);
    }

    @Test
    public void connectionTimeoutFromSystemPropertiesTest() {

        final GenericConfiguration configuration = new GenericConfiguration.Builder()
                .setStartupTimeoutInSeconds(45)
                .build();

        assertEquals("Set timeout property differs from the resolved one.", 45L, timeoutResolver.applyAsLong(configuration, SERVICE_NAME));

        System.setProperty(constructProperty(SERVICE_NAME), Long.toString(180));

        assertEquals("Set timeout property differs from the resolved one.", 180, timeoutResolver.applyAsLong(configuration, SERVICE_NAME));

        cleanConnectionTimeoutProperty(SERVICE_NAME);

        assertEquals("Set timeout property differs from the resolved one.", 45, timeoutResolver.applyAsLong(configuration, SERVICE_NAME));
    }

    private String getConnectionTimeoutSystemProperty(String serviceName) {
        return System.getProperty(constructProperty(serviceName));
    }

    private void cleanConnectionTimeoutProperty(String serviceName) {
        System.setProperty(constructProperty(serviceName), "");
    }

    private String constructProperty(String serviceName) {
        return MessageFormat.format("docker.{0}.connection.timeout", serviceName.toLowerCase());
    }
}
