package travel.snapshot.qa.configuration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import org.arquillian.cube.spi.Cube;
import org.jboss.arquillian.core.spi.Manager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import travel.snapshot.qa.category.UnitTest;
import travel.snapshot.qa.docker.manager.ConnectionMode;
import travel.snapshot.qa.docker.manager.DockerContainerLifecycleDecider;
import travel.snapshot.qa.docker.manager.DockerManagerCubeRegistry;

@Category(UnitTest.class)
public class DockerContainerLifecycleDeciderTestCase {

    private static final String CONTAINER_ID = "containerId";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private DockerManagerCubeRegistry registry;

    @Mock
    private Manager manager;

    @Mock
    private Cube cube;

    @Spy
    private DockerContainerLifecycleDecider decider;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        decider.init(registry, manager);
    }

    @Test
    public void shouldThrowExceptionWhenStartAndStopAndAlreadyRunningTest() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Unable to start already started container containerId");

        doReturn(ConnectionMode.STARTANDSTOP).when(decider).getConnectionMode(any());
        doReturn(true).when(decider).isAlreadyRunning(eq(CONTAINER_ID));

        decider.shouldStart(CONTAINER_ID);
    }

    @Test
    public void shouldStartWhenStartAndStopAndNotRunningTest() {
        doReturn(ConnectionMode.STARTANDSTOP).when(decider).getConnectionMode(any());
        doReturn(false).when(decider).isAlreadyRunning(eq(CONTAINER_ID));

        assertTrue(decider.shouldStart(CONTAINER_ID));
    }

    @Test
    public void shouldNotStartWhenStartOrConnectAlreadyRunningTest() {
        doReturn(ConnectionMode.STARTORCONNECT).when(decider).getConnectionMode(any());
        doReturn(true).when(decider).isAlreadyRunning(eq(CONTAINER_ID));

        assertFalse(decider.shouldStart(CONTAINER_ID));
    }

    @Test
    public void shouldStartWhenStartOrConnectNotRunningTest() {
        doReturn(ConnectionMode.STARTORCONNECT).when(decider).getConnectionMode(any());
        doReturn(false).when(decider).isAlreadyRunning(eq(CONTAINER_ID));

        assertTrue(decider.shouldStart(CONTAINER_ID));
    }

    @Test
    public void shouldNotStartWhenStartOrConnectAndLeaveAlreadyRunningTest() {
        doReturn(ConnectionMode.STARTORCONNECTANDLEAVE).when(decider).getConnectionMode(any());
        doReturn(true).when(decider).isAlreadyRunning(eq(CONTAINER_ID));

        assertFalse(decider.shouldStart(CONTAINER_ID));
    }

    @Test
    public void shouldStartWhenStartOrConnectAndLeaveNotRunningTest() {
        doReturn(ConnectionMode.STARTORCONNECTANDLEAVE).when(decider).getConnectionMode(any());
        doReturn(false).when(decider).isAlreadyRunning(eq(CONTAINER_ID));

        assertTrue(decider.shouldStart(CONTAINER_ID));
    }

    // Stops

    @Test
    public void shouldStopWhenStartAndStopAndAlreadyRunningTest() {
        doReturn(ConnectionMode.STARTANDSTOP).when(decider).getConnectionMode(any());
        doReturn(true).when(decider).isAlreadyRunning(eq(CONTAINER_ID));

        assertTrue(decider.shouldStop(CONTAINER_ID));
    }

    @Test
    public void shouldStopWhenStartAndStopAndNotRunningTest() {
        doReturn(ConnectionMode.STARTANDSTOP).when(decider).getConnectionMode(any());
        doReturn(false).when(decider).isAlreadyRunning(eq(CONTAINER_ID));

        assertTrue(decider.shouldStop(CONTAINER_ID));
    }

    @Test
    public void shouldNotStopWhenStartOrConnectAlreadyRunningTest() {
        when(registry.getCube(CONTAINER_ID)).thenReturn(null);

        doReturn(ConnectionMode.STARTORCONNECT).when(decider).getConnectionMode(any());
        doReturn(true).when(decider).isAlreadyRunning(eq(CONTAINER_ID));

        assertFalse(decider.shouldStop(CONTAINER_ID));
    }

    @Test
    public void shouldStopWhenStartOrConnectNotRunningTest() {
        when(registry.isRegistered(CONTAINER_ID)).thenReturn(true);

        doReturn(ConnectionMode.STARTORCONNECT).when(decider).getConnectionMode(manager);
        doReturn(false).when(decider).isAlreadyRunning(eq(CONTAINER_ID));

        assertTrue(decider.shouldStop(CONTAINER_ID));
    }

    @Test
    public void shouldNotStopWhenStartOrConnectAndLeaveAlreadyRunningTest() {
        doReturn(ConnectionMode.STARTORCONNECTANDLEAVE).when(decider).getConnectionMode(manager);
        doReturn(true).when(decider).isAlreadyRunning(eq("containerId"));

        assertFalse(decider.shouldStop(CONTAINER_ID));
    }

    @Test
    public void shouldNotStopWhenStartOrConnectAndLeaveNotRunningTest() {
        doReturn(ConnectionMode.STARTORCONNECTANDLEAVE).when(decider).getConnectionMode(manager);
        doReturn(false).when(decider).isAlreadyRunning(eq(CONTAINER_ID));

        assertFalse(decider.shouldStop(CONTAINER_ID));
    }
}
