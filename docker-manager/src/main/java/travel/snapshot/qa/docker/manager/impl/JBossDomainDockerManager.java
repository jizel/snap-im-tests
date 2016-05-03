package travel.snapshot.qa.docker.manager.impl;

import static travel.snapshot.qa.docker.ServiceType.JBOSS_DOMAIN;

import org.arquillian.cube.spi.Cube;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.task.Task;
import org.jboss.as.arquillian.container.domain.ManagementClient;
import travel.snapshot.qa.docker.ServiceType;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.manager.jboss.JBossDomainManager;
import travel.snapshot.qa.manager.jboss.check.JBossDomainStartChecker;

/**
 * Implements JBoss domain manager service with respective startup checking task.
 *
 * JBoss domain manager has default precedence.
 */
public class JBossDomainDockerManager extends DockerServiceManager<JBossDomainManager> {

    private static final String JBOSS_DOMAIN_CONNECTION_TIMEOUT_PROPERTY = "docker.jboss.domain.connection.timeout";

    public JBossDomainDockerManager(JBossDomainManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public Cube start(String containerId) {
        final Task<ManagementClient, Boolean> checkingTask = Spacelift.task(serviceManager.getManagementClient(), JBossDomainStartChecker.class);

        final long timeout = resolveTimeout(serviceManager.getConfiguration().getStartupTimeoutInSeconds(), JBOSS_DOMAIN_CONNECTION_TIMEOUT_PROPERTY, JBOSS_DOMAIN);

        return super.start(checkingTask, containerId, timeout, 10);
    }

    @Override
    public JBossDomainDockerManager stop(String containerId) {
        serviceManager.closeManagementClient(serviceManager.getManagementClient());
        return super.stop(containerId);
    }

    @Override
    public ServiceType provides() {
        return ServiceType.JBOSS_DOMAIN;
    }
}
