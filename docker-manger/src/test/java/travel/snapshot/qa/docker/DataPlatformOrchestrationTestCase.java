package travel.snapshot.qa.docker;

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
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import travel.snapshot.qa.docker.manager.DockerManager;
import travel.snapshot.qa.docker.orchestration.DataPlatformOrchestration;
import travel.snapshot.qa.manager.activemq.api.ActiveMQManager;
import travel.snapshot.qa.manager.mariadb.api.MariaDBManager;
import travel.snapshot.qa.manager.mongodb.api.MongoDBManager;
import travel.snapshot.qa.manager.tomcat.TomcatManager;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(JUnit4.class)
@Ignore("be sure you have Docker set up on your host")
public class DataPlatformOrchestrationTestCase {

    private static final DataPlatformOrchestration orchestration = new DataPlatformOrchestration();

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @BeforeClass
    public static void setup() {

        final DockerService service = DockerService.INSTANCE;

        orchestration.with(service.activemq().init())
                .with(service.mariadb().init())
                .with(service.mongo().init())
                .with(service.tomcat().init());

        orchestration.startServices();
    }

    @AfterClass
    public static void teardown() {
        orchestration.stopServices();
        orchestration.stop();
    }

    @Test
    public void test() {

        MariaDBManager mariaDBManager = orchestration.getMariaDBDockerManager().getServiceManager();
        ActiveMQManager activeMQManager = orchestration.getActiveMQDockerManager().getServiceManager();
        MongoDBManager mongoDBManager = orchestration.getMongoDockerManager().getServiceManager();
        TomcatManager tomcatManager = orchestration.getTomcatDockerManager().getServiceManager();

        // MariaDB

        final Connection sqlConnecetion = mariaDBManager.getConnection();
        Assert.assertNotNull(sqlConnecetion);
        mariaDBManager.closeConnection(sqlConnecetion);

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
    public void clientExecutorTest() throws Exception {
        final DockerClientExecutor dockerClientExecutor = DockerManager.instance().getClientExecutor();

        final List<Container> containers = dockerClientExecutor.listRunningContainers();

        for (final Container container : containers) {
            System.out.println("container id: " + container.getId());
            System.out.println("container image: " + container.getImage());
            System.out.println("container names: ");
            Arrays.asList(container.getNames()).forEach(name -> System.out.println(name));
        }

        dockerClientExecutor.pingDockerServer();

        final List<ChangeLog> changeLogs = dockerClientExecutor.inspectChangesOnContainerFilesystem("tomcat");

        for (final ChangeLog changeLog : changeLogs) {
            System.out.println(String.format("%s -> %s", changeLog.getKind(), changeLog.getPath()));
        }
    }

    @Test
    @Ignore("be sure to execute this test in a way that system property ${tomcat.mount.dir} gets expanded in arquillian.xml file.")
    public void mountingFolderTest() throws Exception {
        final File configDir = testFolder.newFolder("configDir");

        final DockerClientExecutor dockerClientExecutor = DockerManager.instance().getClientExecutor();

        try (final InputStream inputStream = dockerClientExecutor.getFileOrDirectoryFromContainerAsTar("tomcat", "/data/tomcat/config")) {
            TarArchive tarArchive = new TarArchive(inputStream);
            tarArchive.extractContents(configDir);
            tarArchive.closeArchive();
        }

        Assert.assertTrue("Archive should contain test.properties file!", new File(configDir, "config/test.properties").exists());
    }
}
