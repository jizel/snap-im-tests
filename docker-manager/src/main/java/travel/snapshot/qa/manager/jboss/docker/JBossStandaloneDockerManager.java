package travel.snapshot.qa.manager.jboss.docker;

import org.arquillian.cube.spi.Cube;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.task.Task;
import org.jboss.as.arquillian.container.ManagementClient;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.manager.jboss.JBossStandaloneManager;
import travel.snapshot.qa.manager.jboss.check.JBossStandaloneStartChecker;

/**
 * Implements JBoss Standalone Docker service with respective startup checking task.
 *
 * JBoss standalone has default precedence.
 */
public class JBossStandaloneDockerManager extends DockerServiceManager<JBossStandaloneManager> {

    public static final String SERVICE_NAME = "jboss_standalone";

    private static final String JBOSS_STANDALONE_CONNECTION_TIMEOUT_PROPERTY = "docker.jboss.standalone.connection.timeout";

    public JBossStandaloneDockerManager(JBossStandaloneManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public Cube start(String containerId) {
        final Task<ManagementClient, Boolean> checkingTask = Spacelift.task(serviceManager.getManagementClient(), JBossStandaloneStartChecker.class);

        final long timeout = resolveTimeout(serviceManager.getConfiguration().getStartupTimeoutInSeconds(), JBOSS_STANDALONE_CONNECTION_TIMEOUT_PROPERTY, provides());

        return super.start(checkingTask, containerId, timeout, 10);
    }

    @Override
    public JBossStandaloneDockerManager stop(String containerId) {
        serviceManager.closeManagementClient(serviceManager.getManagementClient());
        return super.stop(containerId);
    }

    @Override
    public String provides() {
        return SERVICE_NAME;
    }
}
