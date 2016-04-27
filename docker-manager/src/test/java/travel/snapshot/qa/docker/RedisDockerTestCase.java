package travel.snapshot.qa.docker;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static travel.snapshot.qa.docker.DockerServiceFactory.RedisService.DEFAULT_REDIS_CONTAINER_ID;

import org.arquillian.cube.spi.Cube;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import travel.snapshot.qa.category.DockerTest;
import travel.snapshot.qa.docker.manager.impl.RedisDockerManager;
import travel.snapshot.qa.manager.redis.api.RedisManager;
import travel.snapshot.qa.manager.redis.configuration.RedisManagerConfiguration;
import travel.snapshot.qa.manager.redis.impl.RedisManagerImpl;

@Category(DockerTest.class)
public class RedisDockerTestCase {

    private static final Logger logger = LoggerFactory.getLogger(RedisDockerTestCase.class);

    private static RedisDockerManager redisDockerManager;

    @BeforeClass
    public static void setup() throws Exception {

        final RedisManager redisManager = new RedisManagerImpl(new RedisManagerConfiguration.Builder().build());

        redisDockerManager = new RedisDockerManager(redisManager);
        redisDockerManager.getDockerManager().startManager();

        logger.info("Redis Docker manager has started.");
    }

    @AfterClass
    public static void teardown() {
        redisDockerManager.getDockerManager().stopManager();

        logger.info("Redis Docker manager has stopped.");
    }

    @Test
    public void dockerContainerTest() {

        final Cube redisContainer = redisDockerManager.start(DEFAULT_REDIS_CONTAINER_ID);
        logger.info("Redis Docker container has started");

        assertTrue("Redis Docker container is not running!", redisDockerManager.serviceRunning());
        assertNotNull("Redis MongoDB container is a null object!", redisContainer);

        Jedis jedis = redisDockerManager.getServiceManager().getJedis();
        assertNotNull("Jedis is a null object.", jedis);

        String status = jedis.set("foo", "bar");

        assertTrue("Result is not OK: " + status, status.equals("OK"));

        String value = jedis.get("foo");

        assertTrue("Returned value is different: " + value, value.equals("bar"));

        redisDockerManager.stop(redisContainer);

        assertFalse("Redis Docker container is running!", redisDockerManager.serviceRunning());
    }
}