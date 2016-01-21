package travel.snapshot.qa.docker;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import travel.snapshot.qa.docker.orchestration.DataPlatformOrchestration;

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
        System.out.println("ok");
    }

}
