package travel.snapshot.qa.docker;

import static travel.snapshot.qa.connection.Protocol.TCP;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import travel.snapshot.qa.category.DockerTest;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
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

    private static final GenericConfiguration configuration = new GenericConfiguration.Builder()
            .setBindPort(PORT)
            .setProtocol(TCP)
            .build();

    private static DockerServiceManager<GenericManager> genericDockerManager;

    @BeforeClass
    public static void setup() {
        genericDockerManager = new GenericService().init(configuration, CONTAINER_ID);

        genericDockerManager.getDockerManager().startManager();
    }

    @AfterClass
    public static void teardown() {
        genericDockerManager.getDockerManager().stopManager();
    }

    @Before
    public void setupTest() {
        genericDockerManager.start();
    }

    @After
    public void afterTest() {
        genericDockerManager.stop();
    }

    @Test
    public void testTCPDockerGenericService() throws Exception {
        Assert.assertTrue("service is not running", genericDockerManager.serviceRunning());
    }
}