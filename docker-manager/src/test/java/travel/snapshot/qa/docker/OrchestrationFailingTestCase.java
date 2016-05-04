package travel.snapshot.qa.docker;

import org.arquillian.cube.spi.Cube;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import travel.snapshot.qa.category.UnitTest;
import travel.snapshot.qa.docker.orchestration.Orchestration;
import travel.snapshot.qa.manager.generic.impl.docker.GenericDockerManager;

import java.util.List;

@Category(UnitTest.class)
public class OrchestrationFailingTestCase {

    private final Orchestration ORCHESTRATION = new Orchestration().start();

    @Mock
    private GenericDockerManager genericDockerManager;

    @Mock
    private Cube cube;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        Mockito.doReturn(false).when(genericDockerManager).serviceRunning();

        Mockito.doThrow(new RuntimeException("This service failed to stop intentionally.")).when(genericDockerManager).stop();
        Mockito.doReturn(cube).when(genericDockerManager).start();
        Mockito.doReturn(cube).when(genericDockerManager).getDockerContainer();

        Mockito.doReturn("fake_service").when(cube).getId();
        Mockito.doReturn("fake_service").when(genericDockerManager).provides();

        ORCHESTRATION.with(genericDockerManager);
    }

    @Test
    public void failingToStopDockerServiceTest() {
        ORCHESTRATION.start();

        List<ServiceCubePair> services = ORCHESTRATION.startServices();

        ORCHESTRATION.stopServices(services);

        Assert.assertTrue(ORCHESTRATION.getStartedContainers().isEmpty());
    }
}
