package travel.snapshot.qa.docker;

import static travel.snapshot.qa.docker.DockerServiceFactory.ActiveMQService.DEFAULT_ACTIVEMQ_CONTAINER_ID;
import static travel.snapshot.qa.docker.DockerServiceFactory.MariaDBService.DEFAULT_MARIADB_CONTAINER_ID;
import static travel.snapshot.qa.docker.DockerServiceFactory.MongoDBService.DEFAULT_MONGODB_CONTAINER_ID;
import static travel.snapshot.qa.docker.DockerServiceFactory.TomcatService.DEFAULT_TOMCAT_CONTAINER_ID;
import static travel.snapshot.qa.docker.DockerServiceFactory.activemq;
import static travel.snapshot.qa.docker.DockerServiceFactory.mariadb;
import static travel.snapshot.qa.docker.DockerServiceFactory.mongodb;
import static travel.snapshot.qa.docker.DockerServiceFactory.tomcat;

import com.github.dockerjava.api.model.Container;
import com.mongodb.MongoClient;
import org.arquillian.cube.ChangeLog;
import org.arquillian.cube.docker.impl.docker.DockerClientExecutor;
import org.jboss.shrinkwrap.impl.base.io.tar.TarArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.category.OrchestrationTest;
import travel.snapshot.qa.docker.manager.DockerManager;
import travel.snapshot.qa.docker.orchestration.DataPlatformOrchestration;
import travel.snapshot.qa.manager.activemq.api.ActiveMQManager;
import travel.snapshot.qa.manager.mariadb.api.MariaDBManager;
import travel.snapshot.qa.manager.mongodb.api.MongoDBManager;
import travel.snapshot.qa.manager.tomcat.TomcatManager;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Category(OrchestrationTest.class)
public class DataPlatformOrchestrationTestCase {

    private static final Logger logger = LoggerFactory.getLogger(DataPlatformOrchestrationTestCase.class);

    private static final DataPlatformOrchestration ORCHESTRATION = new DataPlatformOrchestration();

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @BeforeClass
    public static void setup() {

        ORCHESTRATION.with(activemq().init(DEFAULT_ACTIVEMQ_CONTAINER_ID))
                .with(mariadb().init(DEFAULT_MARIADB_CONTAINER_ID))
                .with(mongodb().init(DEFAULT_MONGODB_CONTAINER_ID))
                .with(tomcat().init(DEFAULT_TOMCAT_CONTAINER_ID))
                .startServices();
    }

    @AfterClass
    public static void teardown() {
        ORCHESTRATION.stopServices();
        ORCHESTRATION.stop();
    }

    @Test
    public void test() {

        MariaDBManager mariaDBManager = ORCHESTRATION.getMariaDBDockerManager().getServiceManager();
        ActiveMQManager activeMQManager = ORCHESTRATION.getActiveMQDockerManager().getServiceManager();
        MongoDBManager mongoDBManager = ORCHESTRATION.getMongoDockerManager().getServiceManager();
        TomcatManager tomcatManager = ORCHESTRATION.getTomcatDockerManager().getServiceManager();

        // MariaDB

        final Connection sqlConnection = mariaDBManager.getConnection();
        Assert.assertNotNull(sqlConnection);
        mariaDBManager.closeConnection(sqlConnection);

        // ActiveMQ

        final javax.jms.Connection activeMQConnection = activeMQManager.buildConnection();
        Assert.assertNotNull(activeMQConnection);
        activeMQManager.closeConnection(activeMQConnection);

        // MongoDB

        final MongoClient mongoClient = mongoDBManager.getClient();
        Assert.assertNotNull(mongoClient);
        mongoClient.listDatabases().maxTime(1, TimeUnit.MINUTES);
        mongoClient.close();

        // Tomcat

        Assert.assertTrue(tomcatManager.isRunning());
        // there are already 2 deployments in clean Tomcat - its managers
        Assert.assertEquals(2, tomcatManager.listDeployments().size());
    }

    @Test
    public void ipInspectionTest() {
        Map<String, String> ips = ORCHESTRATION.inspectAllIPs();

        logger.info("Resolved container IPs: {}", ips);

        Assert.assertEquals("There are less resolved IPs than running containers!",
                ORCHESTRATION.getStartedContainers().size(),
                ips.size());

        String tomcatResolvedIpFromMap = ips.get(DEFAULT_TOMCAT_CONTAINER_ID);
        String tomcatResolvedIpFromIPMethod = ORCHESTRATION.inspectIP(DEFAULT_TOMCAT_CONTAINER_ID);

        Assert.assertEquals(tomcatResolvedIpFromMap, tomcatResolvedIpFromIPMethod);

        String mariadbResolvedIp = ips.get(DEFAULT_MARIADB_CONTAINER_ID);

        Assert.assertNotEquals("Some resolved IPs for different containers are same!",
                tomcatResolvedIpFromMap, mariadbResolvedIp);

        List<String> ipAddressesList = new ArrayList<>(ips.values());
        Set<String> ipAddressesSet = new HashSet<>(ipAddressesList);

        Assert.assertEquals("IP addresses are not unique across containers!",
                ipAddressesList.size(), ipAddressesSet.size());
    }

    @Test
    public void clientExecutorTest() throws Exception {
        final DockerClientExecutor dockerClientExecutor = DockerManager.instance().getClientExecutor();

        final List<Container> containers = dockerClientExecutor.listRunningContainers();

        for (final Container container : containers) {
            logger.info("container id: {}", container.getId());
            logger.info("container image: {}", container.getImage());
            logger.info("container names: {}", Arrays.asList(container.getNames()));
        }

        dockerClientExecutor.pingDockerServer();

        final List<ChangeLog> changeLogs = dockerClientExecutor.inspectChangesOnContainerFilesystem(DEFAULT_TOMCAT_CONTAINER_ID);

        for (final ChangeLog changeLog : changeLogs) {
            System.out.println(String.format("%s -> %s", changeLog.getKind(), changeLog.getPath()));
        }
    }

    @Test
    @Ignore("be sure to execute this test in a way that system property ${tomcat.mount.dir} gets expanded in arquillian.xml file.")
    public void mountingFolderTestManually() throws Exception {
        final File configDir = testFolder.newFolder("configDirManual");

        final DockerClientExecutor dockerClientExecutor = DockerManager.instance().getClientExecutor();

        try (final InputStream inputStream = dockerClientExecutor.getFileOrDirectoryFromContainerAsTar(DEFAULT_TOMCAT_CONTAINER_ID, "/data/tomcat/config")) {
            TarArchive tarArchive = new TarArchive(inputStream);
            tarArchive.extractContents(configDir);
            tarArchive.closeArchive();
        }

        Assert.assertTrue("Archive should contain test.properties file!", new File(configDir, "config/test.properties").exists());
    }

    @Test
    @Ignore("be sure to execute this test in a way that system property ${tomcat.mount.dir} gets expanded in arquillian.xml file.")
    public void mountingFolderTestByAPI() throws Exception {
        final File destinationDir = testFolder.newFolder("configDirByAPI");
        final File sourceDir = new File("/data/tomcat/config");

        ORCHESTRATION.getTomcatDockerManager().fetch(sourceDir, destinationDir);

        Assert.assertTrue("Archive should contain test.properties file!", new File(destinationDir, "config/test.properties").exists());
    }
}