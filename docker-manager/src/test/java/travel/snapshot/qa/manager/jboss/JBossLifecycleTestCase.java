package travel.snapshot.qa.manager.jboss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.arquillian.spacelift.Spacelift;
import org.jboss.as.arquillian.container.domain.Domain;
import org.jboss.as.controller.client.helpers.domain.ServerIdentity;
import org.jboss.as.controller.client.helpers.domain.ServerStatus;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import travel.snapshot.qa.category.JBossTest;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;
import travel.snapshot.qa.manager.jboss.configuration.JVM;
import travel.snapshot.qa.manager.jboss.configuration.Util;
import travel.snapshot.qa.manager.jboss.spacelift.JBossDomainStarter;
import travel.snapshot.qa.manager.jboss.spacelift.JBossStandaloneStarter;
import travel.snapshot.qa.manager.jboss.spacelift.JBossStopper;

import java.util.List;
import java.util.Map;

@Category(JBossTest.class)
public class JBossLifecycleTestCase {

    private static JBossManagerConfiguration configuration = new JBossManagerConfiguration.Builder()
            .setJVM(new JVM.Builder().setJBossHome(Util.getJBossHome()).build())
            .setContainerType(Util.getContainerType())
            .build();

    private static JBossManagerConfiguration domainConfiguration = new JBossManagerConfiguration.Builder()
            .setJVM(new JVM.Builder().setJBossHome(Util.getJBossHome()).build())
            .setContainerType(Util.getContainerType())
            .domain()
            .build();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void startAndStopJBossContainerStandalone() {
        JBossStandaloneManager standaloneManager = Spacelift.task(configuration, JBossStandaloneStarter.class).execute().await();

        assertTrue("Server is not running!", standaloneManager.isRunning());
        assertTrue("Server is not in running state!", standaloneManager.getManagementClient().isServerInRunningState());

        Spacelift.task(standaloneManager, JBossStopper.class).execute().await();
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

        Spacelift.task(domainManager, JBossStopper.class).execute().await();
    }

    @Test
    @Ignore("run this only if getenv on JBOSS_HOME is null and jboss.home system property is null as well")
    public void startAndStopJBossContainerWithoutJBossHome() {

        expectedException.expect(Exception.class);

        Spacelift.task(JBossStandaloneStarter.class).then(JBossStopper.class).execute().await();
    }
}
