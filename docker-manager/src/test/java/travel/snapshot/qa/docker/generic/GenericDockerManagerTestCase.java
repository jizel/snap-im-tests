package travel.snapshot.qa.docker.generic;

import static travel.snapshot.qa.connection.Protocol.TCP;
import static travel.snapshot.qa.connection.Protocol.UDP;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import travel.snapshot.qa.category.DockerTest;
import travel.snapshot.qa.connection.Protocol;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.manager.api.configuration.ConfigurationException;
import travel.snapshot.qa.manager.generic.api.GenericManager;
import travel.snapshot.qa.manager.generic.configuration.GenericConfiguration;
import travel.snapshot.qa.manager.generic.impl.docker.GenericService;

/**
 * For test purposes we just start Tomcat container and check TCP connection by generic service.
 */
@Category(DockerTest.class)
public class GenericDockerManagerTestCase {

    private static final String CONTAINER_ID = "tomcat_tests";

    private static final int PORT = 8080;

    private static final String ADDRESS = "127.0.0.1";

    private static DockerServiceManager<GenericManager> genericDockerManager;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testTCPDockerGenericService() throws Exception {
        startAndStopGenericService(getGenericConfiguration(TCP));
    }

    @Test
    @Ignore("There is not yet any known Docker container which would expose UDP port.")
    public void testUDPDockerGenericService() throws Exception {
        startAndStopGenericService(getGenericConfiguration(UDP));
    }

    @Test
    public void invalidGenericConfigurationTest() {
        expectedException.expect(ConfigurationException.class);
        expectedException.expectMessage("Startup timeout in seconds (-100) can not be lower than 0.");

        getGenericConfiguration(TCP, -100);
    }

    private void startAndStopGenericService(final GenericConfiguration genericConfiguration) {
        genericDockerManager = new GenericService().init(genericConfiguration, CONTAINER_ID);

        genericDockerManager.getDockerManager().startManager();

        genericDockerManager.start();

        Assert.assertTrue("service is not running", genericDockerManager.serviceRunning());

        genericDockerManager.stop();

        genericDockerManager.getDockerManager().stopManager();
    }

    // helpers

    private GenericConfiguration getGenericConfiguration(final Protocol protocol) {
        return getGenericConfiguration(protocol, 120L);
    }

    private GenericConfiguration getGenericConfiguration(final Protocol protocol, long startupTimeout) {
        return new GenericConfiguration.Builder()
                .setBindAddress(ADDRESS)
                .setBindPort(PORT)
                .setProtocol(protocol)
                .setStartupTimeoutInSeconds(startupTimeout)
                .build();
    }
}