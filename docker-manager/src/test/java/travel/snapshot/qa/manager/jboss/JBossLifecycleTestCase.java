package travel.snapshot.qa.manager.jboss;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static travel.snapshot.qa.manager.jboss.configuration.ContainerType.WILDFLY;

import org.arquillian.spacelift.Spacelift;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.arquillian.container.domain.Domain;
import org.jboss.as.controller.client.helpers.domain.ServerIdentity;
import org.jboss.as.controller.client.helpers.domain.ServerStatus;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import travel.snapshot.qa.category.JBossTest;
import travel.snapshot.qa.manager.Util;
import travel.snapshot.qa.manager.api.container.ContainerManagerException;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;
import travel.snapshot.qa.manager.jboss.configuration.JVM;
import travel.snapshot.qa.manager.jboss.spacelift.JBossDomainStarter;
import travel.snapshot.qa.manager.jboss.spacelift.JBossStandaloneStarter;
import travel.snapshot.qa.manager.jboss.spacelift.JBossStopper;

import java.util.List;
import java.util.Map;

@Category(JBossTest.class)
public class JBossLifecycleTestCase {

    private JBossManagerConfiguration configuration = new JBossManagerConfiguration.Builder()
            .setJVM(new JVM.Builder().setJBossHome(Util.getJBossHome(WILDFLY)).build())
            .setContainerType(Util.getContainerType())
            .build();

    private JBossManagerConfiguration domainConfiguration = new JBossManagerConfiguration.Builder()
            .setJVM(new JVM.Builder().setJBossHome(Util.getJBossHome(WILDFLY)).build())
            .setContainerType(Util.getContainerType())
            .domain()
            .build();

    private JBossManagerConfiguration invalidLocalConfiguration = new JBossManagerConfiguration.Builder()
            .setJVM(new JVM.Builder().setJBossHome(Util.getJBossHome(WILDFLY)).build())
            .remote() // remote flag means that we will not try to start it locally but in test we try to do it
            .build();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private ManagementClient standaloneManagementClient;

    @Mock
    private org.jboss.as.arquillian.container.domain.ManagementClient domainManagementClient;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void localStartWithRemoteConfigurationWillFail() {
        expectedException.expect(ContainerManagerException.class);
        expectedException.expectMessage("Could not start JBoss container: Starting of JBoss container is allowed only if 'remote' is false.");

        new JBossStandaloneManager(invalidLocalConfiguration).start();
    }

    @Test
    public void startAndStopJBossContainerStandalone() {
        JBossStandaloneManager standaloneManager = Spacelift.task(configuration, JBossStandaloneStarter.class).execute().await();

        assertTrue("Server is not running!", standaloneManager.isRunning());
        assertTrue("Server is not in running state!", standaloneManager.getManagementClient().isServerInRunningState());
        assertTrue("Server is not in running state!", standaloneManager.getManagementClient().isServerInRunningState());
        assertNotNull(standaloneManager.getModelControllerClient());

        Spacelift.task(standaloneManager, JBossStopper.class).execute().await();
    }

    @Test
    public void startAndStopJBossContainerStandaloneWithoutSpacelift() {
        JBossStandaloneManager standaloneManager = new JBossStandaloneManager();

        standaloneManager.start();
        standaloneManager.stop();
    }

    @Test
    public void startAndStopJBossContainerDomainWithoutSpacelift() {
        JBossDomainManager domainManager = new JBossDomainManager();

        domainManager.start();
        domainManager.stop();
    }

    @Test
    public void testFailingCloseOfStandaloneManagementClient() {
        expectedException.expect(ContainerManagerException.class);
        expectedException.expectCause(is(instanceOf(RuntimeException.class)));
        expectedException.expectMessage("Closing of JBoss standalone management client has not been successful: intentionally thrown exception");

        Mockito.doThrow(new RuntimeException("intentionally thrown exception")).when(standaloneManagementClient).close();

        new JBossStandaloneManager().closeManagementClient(standaloneManagementClient);
    }

    @Test
    public void testFailingCloseOfDomainManagementClient() {
        expectedException.expect(ContainerManagerException.class);
        expectedException.expectCause(is(instanceOf(RuntimeException.class)));
        expectedException.expectMessage("Closing of JBoss domain management client has not been successful: intentionally thrown exception");

        Mockito.doThrow(new RuntimeException("intentionally thrown exception")).when(domainManagementClient).close();

        new JBossDomainManager().closeManagementClient(domainManagementClient);
    }

    @Test
    public void startAndStopJBossContainerDomain() {
        JBossDomainManager domainManager = Spacelift.task(domainConfiguration, JBossDomainStarter.class).execute().await();

        assertTrue("Configuration is not running!", domainManager.isRunning());
        assertTrue("Configuration is not in running state!", domainManager.getManagementClient().isDomainInRunningState());

        Map<ServerIdentity, ServerStatus> serverStatusMap = domainManager.getServerStatuses();
        List<Domain.Server> domainServers = domainManager.getServers();

        System.out.println(serverStatusMap);
        System.out.println(domainServers);

        domainManager.getManagementClient().stopServerGroup("main-server-group");

        // ensure there are 2 stopped servers when stopping main-server-group

        assertEquals(2, domainManager.getServers(ServerStatus.STOPPED).size());

        domainManager.getManagementClient().startServerGroup("main-server-group");

        // ensure there are 2 started servers when we start stopped server group again

        assertEquals(2, domainManager.getServers(ServerStatus.STARTED).size());

        // stop server group again and get stopped servers
        domainManager.getManagementClient().stopServerGroup("main-server-group");
        List<Domain.Server> stoppedServers = domainManager.getServers(ServerStatus.STOPPED);
        assertEquals(2, stoppedServers.size());

        // start previously stopped servers one by one

        domainManager.getManagementClient().startServer(stoppedServers.get(0));
        domainManager.getManagementClient().startServer(stoppedServers.get(1));

        // ensure these servers are started

        assertEquals(2, domainManager.getServers(ServerStatus.STARTED).size());

        assertNotNull(domainManager.getModelControllerClient());

        Spacelift.task(domainManager, JBossStopper.class).execute().await();
    }

    @Test
    @Ignore("run this only if getenv on JBOSS_HOME is null and jboss.home system property is null as well")
    public void startAndStopJBossContainerWithoutJBossHome() {
        expectedException.expect(Exception.class);
        Spacelift.task(JBossStandaloneStarter.class).then(JBossStopper.class).execute().await();
    }
}
