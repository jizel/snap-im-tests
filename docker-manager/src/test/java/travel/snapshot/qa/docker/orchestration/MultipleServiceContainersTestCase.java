package travel.snapshot.qa.docker.orchestration;

import static travel.snapshot.qa.manager.mongodb.impl.docker.MongoDBService.DEFAULT_MONGODB_CONTAINER_ID;

import org.arquillian.cube.spi.Cube;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;
import travel.snapshot.qa.category.OrchestrationTest;
import travel.snapshot.qa.manager.mongodb.impl.docker.MongoDBDockerManager;
import travel.snapshot.qa.docker.orchestration.Orchestration;
import travel.snapshot.qa.manager.mongodb.configuration.MongoDBManagerConfiguration;
import travel.snapshot.qa.manager.mongodb.configuration.MongoHostPortPair;
import travel.snapshot.qa.manager.mongodb.impl.docker.MongoDBService;

@Category(OrchestrationTest.class)
public class MultipleServiceContainersTestCase {

    private static final Orchestration ORCHESTRATION = new Orchestration();

    private static final String SECOND_MONGODB_CONTAINER_ID = "mongodb_2";

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @BeforeClass
    public static void setup() {

        MongoDBService mongoDBService = new MongoDBService();

        // here we start two docker containers of different
        // container ID but they will run the same services

        final String hostIp = new MongoDBManagerConfiguration.Builder().resolveHostIp();

        final MongoDBManagerConfiguration mongoDBManagerConfiguration1 = new MongoDBManagerConfiguration.Builder()
                .setServer(new MongoHostPortPair(hostIp, 27017))
                .build();

        final MongoDBManagerConfiguration mongoDBManagerConfiguration2 = new MongoDBManagerConfiguration.Builder()
                .setServer(new MongoHostPortPair(hostIp, 27018))
                .build();

        ORCHESTRATION.with(mongoDBService.init(mongoDBManagerConfiguration1, DEFAULT_MONGODB_CONTAINER_ID))
                .with(mongoDBService.init(mongoDBManagerConfiguration2, SECOND_MONGODB_CONTAINER_ID))
                .startServices();
    }

    @AfterClass
    public static void teardown() {
        ORCHESTRATION.stopServices();
        ORCHESTRATION.stop();
    }

    @Test
    public void multipleContainersTest() {
        final MongoDBDockerManager manager1 = ORCHESTRATION.getDockerServiceManager(MongoDBDockerManager.class, DEFAULT_MONGODB_CONTAINER_ID);
        final MongoDBDockerManager manager2 = ORCHESTRATION.getDockerServiceManager(MongoDBDockerManager.class, SECOND_MONGODB_CONTAINER_ID);

        Assert.assertNotNull(manager1);
        Assert.assertNotNull(manager2);

        Assert.assertNotSame("Returned managers are equal for different containers!", manager1, manager2);

        final Cube mongo1 = manager1.getDockerContainer();
        final Cube mongo2 = manager2.getDockerContainer();

        Assert.assertNotSame("Returned Cubes from different managers are equal!", mongo1, mongo2);

        final String mongoIP1 = ORCHESTRATION.inspectIP(DEFAULT_MONGODB_CONTAINER_ID);
        final String mongoIP2 = ORCHESTRATION.inspectIP(SECOND_MONGODB_CONTAINER_ID);

        Assert.assertNotEquals(mongoIP1, mongoIP2);
    }
}
