package travel.snapshot.qa.docker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static travel.snapshot.qa.docker.ServiceType.GENERIC;

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

    private final ToLongBiFunction<GenericConfiguration, ServiceType> timeoutResolver = (configuration, serviceType) ->
            DockerServiceManager.resolveTimeout(configuration.getStartupTimeoutInSeconds(), constructProperty(serviceType), serviceType);

    @Before
    public void setup() {

        final String connectionTimeout = getConnectionTimeoutSystemProperty(GENERIC);

        assertTrue("Connection timeout property is not null nor an empty String.",
                connectionTimeout == null || connectionTimeout.isEmpty());
    }

    @After
    public void teardown() {
        cleanConnectionTimeoutProperty(GENERIC);
    }

    @Test
    public void connectionTimeoutFromSystemPropertiesTest() {

        final GenericConfiguration configuration = new GenericConfiguration.Builder()
                .setStartupTimeoutInSeconds(45)
                .build();

        assertEquals("Set timeout property differs from the resolved one.", 45L, timeoutResolver.applyAsLong(configuration, GENERIC));

        System.setProperty(constructProperty(GENERIC), Long.toString(180));

        assertEquals("Set timeout property differs from the resolved one.", 180, timeoutResolver.applyAsLong(configuration, GENERIC));

        cleanConnectionTimeoutProperty(GENERIC);

        assertEquals("Set timeout property differs from the resolved one.", 45, timeoutResolver.applyAsLong(configuration, GENERIC));
    }

    private String getConnectionTimeoutSystemProperty(ServiceType serviceType) {
        return System.getProperty(constructProperty(serviceType));
    }

    private void cleanConnectionTimeoutProperty(ServiceType serviceType) {
        System.setProperty(constructProperty(serviceType), "");
    }

    private String constructProperty(ServiceType serviceType) {
        return MessageFormat.format("docker.{0}.connection.timeout", serviceType.name().toLowerCase());
    }
}
