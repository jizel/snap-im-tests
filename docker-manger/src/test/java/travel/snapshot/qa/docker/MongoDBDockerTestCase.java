package travel.snapshot.qa.docker;

import com.mongodb.MongoClient;
import org.arquillian.cube.spi.Cube;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import travel.snapshot.qa.category.DockerTest;
import travel.snapshot.qa.docker.manager.impl.MongoDBDockerManager;
import travel.snapshot.qa.manager.mongodb.api.MongoDBManager;
import travel.snapshot.qa.manager.mongodb.configuration.MongoDBManagerConfiguration;
import travel.snapshot.qa.manager.mongodb.impl.MongoDBManagerImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Category(DockerTest.class)
public class MongoDBDockerTestCase {

    private static final Logger logger = Logger.getLogger(MongoDBDockerTestCase.class.getName());

    private static final String MONGODB_CONTAINER_ID = "mongodb";

    private static MongoDBDockerManager mongodb;

    @BeforeClass
    public static void setup() throws Exception {

        final MongoDBManagerConfiguration mongoDBManagerConfiguration = new MongoDBManagerConfiguration.Builder()
                .setServer(new MongoDBManagerConfiguration.MongoHostPortPair("127.0.0.1", 27017))
                .build();

        final MongoDBManager mongoDBManager = new MongoDBManagerImpl(mongoDBManagerConfiguration);

        mongodb = new MongoDBDockerManager(mongoDBManager);
        mongodb.getDockerManager().startManager();

        logger.info("MongoDB Docker manager has started.");
    }

    @AfterClass
    public static void teardown() {
        mongodb.getDockerManager().stopManager();

        logger.info("MongoDB Docker manager has stopped.");
    }

    @Test
    public void dockerContainerTest() {

        final Cube startedMongoDBContainer = mongodb.start(MONGODB_CONTAINER_ID);
        logger.info("MongoDB Docker container has started");

        Assert.assertTrue("Docker MongoDB container is not running!", mongodb.serviceRunning());
        Assert.assertNotNull("Docker MongoDB container is a null object!", startedMongoDBContainer);

        final Cube givenMariaDBContainer = mongodb.getDockerContainer();

        Assert.assertEquals("Started and saved Docker containers are not equal!", startedMongoDBContainer, givenMariaDBContainer);
        Assert.assertEquals("Docker manager started container with different container ID", MONGODB_CONTAINER_ID, startedMongoDBContainer.getId());

        final MongoClient mongoClient = mongodb.getServiceManager().getClient();

        List<String> databaseNames = new ArrayList<String>() {{
            add("local");
            add("admin");
            add("test");
        }};

        for (final String databaseName : mongoClient.listDatabaseNames()) {
            Assert.assertTrue(databaseNames.contains(databaseName));
        }

        Assert.assertNotNull("Returned MongoDB client is a null object!", mongoClient);
        mongodb.getServiceManager().closeClient(mongoClient);

        mongodb.stop(startedMongoDBContainer);
        logger.info("Docker MongoDB container has stopped.");

        Assert.assertFalse("Container has stopped but service is still running.", mongodb.serviceRunning());
    }
}
