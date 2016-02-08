package travel.snapshot.qa.docker;

import static travel.snapshot.qa.docker.DockerServiceFactory.MariaDBService.DEFAULT_MARIADB_CONTAINER_ID;

import org.apache.commons.io.IOUtils;
import org.arquillian.cube.spi.Cube;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.category.DockerTest;
import travel.snapshot.qa.docker.manager.impl.MariaDBDockerManager;
import travel.snapshot.qa.manager.mariadb.api.MariaDBManager;
import travel.snapshot.qa.manager.mariadb.configuration.MariaDBManagerConfiguration;
import travel.snapshot.qa.manager.mariadb.impl.MariaDBManagerImpl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;

@Category(DockerTest.class)
public class MariaDBDockerTestCase {

    private static final Logger logger = LoggerFactory.getLogger(MariaDBDockerTestCase.class);

    private static final String SQL_SCRIPT = "script.sql";

    private static MariaDBDockerManager mariaDBDockerManager;

    private static Reader sqlScriptReader;

    @BeforeClass
    public static void setup() throws Exception {

        sqlScriptReader = new BufferedReader(new InputStreamReader(MariaDBDockerTestCase.class.getClassLoader().getResourceAsStream(SQL_SCRIPT)));

        final MariaDBManager mariaDBManager = new MariaDBManagerImpl(new MariaDBManagerConfiguration.Builder().build());

        mariaDBDockerManager = new MariaDBDockerManager(mariaDBManager);
        mariaDBDockerManager.getDockerManager().startManager();

        logger.info("MariaDB Docker manager has started.");
    }

    @AfterClass
    public static void teardown() {
        IOUtils.closeQuietly(sqlScriptReader);

        mariaDBDockerManager.getDockerManager().stopManager();

        logger.info("MariaDB Docker manager has stopped.");
    }

    @Test
    public void dockerContainerTest() {

        final Cube startedMariaDBContainer = mariaDBDockerManager.start(DEFAULT_MARIADB_CONTAINER_ID);
        logger.info("MariaDB Docker container has started");

        Assert.assertTrue("Docker MariaDB container is not running!", mariaDBDockerManager.serviceRunning());
        Assert.assertNotNull("Docker MariaDB container is a null object!", startedMariaDBContainer);

        final Cube givenMariaDBContainer = mariaDBDockerManager.getDockerContainer();

        Assert.assertEquals("Started and saved Docker containers are not equal!", startedMariaDBContainer, givenMariaDBContainer);
        Assert.assertEquals("Docker manager started container with different container ID", DEFAULT_MARIADB_CONTAINER_ID, startedMariaDBContainer.getId());

        final Connection connection = mariaDBDockerManager.getServiceManager().getConnection();

        Assert.assertNotNull("Returned JDBC connection is a null object!", connection);

        logger.info("Going to execute SQL script in " + SQL_SCRIPT);

        mariaDBDockerManager.getServiceManager().executeScript(connection, sqlScriptReader);

        logger.info("SQL script executed");

        mariaDBDockerManager.getServiceManager().closeConnection(connection);

        logger.info("Flyway migration starting.");

        mariaDBDockerManager.getServiceManager().flyway("testDatabase").migrate();

        logger.info("Flyway migratiton has finished");

        mariaDBDockerManager.stop(startedMariaDBContainer);
        logger.info("Docker MariaDB container has stopped.");

        Assert.assertFalse("Container has stopped but service is still running.", mariaDBDockerManager.serviceRunning());
    }
}
