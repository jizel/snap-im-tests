package travel.snapshot.qa.docker;

import com.mongodb.MongoClient;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import travel.snapshot.qa.docker.orchestration.DataPlatformOrchestration;
import travel.snapshot.qa.manager.activemq.api.ActiveMQManager;
import travel.snapshot.qa.manager.mariadb.api.MariaDBManager;
import travel.snapshot.qa.manager.mongodb.api.MongoDBManager;
import travel.snapshot.qa.manager.tomcat.TomcatManager;

import java.sql.Connection;
import java.util.concurrent.TimeUnit;

@RunWith(JUnit4.class)
public class DataPlatformOrchestrationTestCase {

    private static final DataPlatformOrchestration orchestration = new DataPlatformOrchestration();

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
}
