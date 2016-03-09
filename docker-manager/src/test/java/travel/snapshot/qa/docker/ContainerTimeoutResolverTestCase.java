package travel.snapshot.qa.docker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import travel.snapshot.qa.category.UnitTest;
import travel.snapshot.qa.docker.manager.impl.ConnectionTimeoutResolver;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;

import java.text.MessageFormat;

@Category(UnitTest.class)
public class ContainerTimeoutResolverTestCase {

    @Before
    public void setup() {

        String connectionTimeout = getConnectionTimeoutSystemProperty(ServiceType.TOMCAT);

        assertTrue("Connection timeout property is not null nor an empty String!",
                connectionTimeout == null || connectionTimeout.isEmpty());
    }

    @After
    public void teardown() {
        cleanConnectionTimeoutProperty(ServiceType.TOMCAT);
    }

    @Test
    public void connectionTimeoutFromSystemPropertiesTest() {

        final TomcatManagerConfiguration configuration = new TomcatManagerConfiguration.Builder()
                .setStartupTimeoutInSeconds(45)
                .build();

        assertEquals("Set timeout property differs from the resolved one.", 45, ConnectionTimeoutResolver.resolveTomcatConnectionTimeout(configuration));

        System.setProperty(constructProperty(ServiceType.TOMCAT), Long.toString(180));

        assertEquals("Set timeout property differs from the resolved one.", 180, ConnectionTimeoutResolver.resolveTomcatConnectionTimeout(configuration));

        cleanConnectionTimeoutProperty(ServiceType.TOMCAT);

        assertEquals("Set timeout property differs from the resolved one.", 45, ConnectionTimeoutResolver.resolveTomcatConnectionTimeout(configuration));
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
