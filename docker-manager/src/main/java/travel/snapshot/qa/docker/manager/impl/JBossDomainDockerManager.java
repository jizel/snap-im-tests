package travel.snapshot.qa.docker.manager.impl;

import org.arquillian.cube.spi.Cube;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.task.Task;
import org.jboss.as.arquillian.container.domain.ManagementClient;
import travel.snapshot.qa.docker.ServiceType;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.manager.jboss.JBossDomainManager;
import travel.snapshot.qa.manager.jboss.check.JBossDomainStartChecker;

public class JBossDomainDockerManager extends DockerServiceManager<JBossDomainManager> {

    public JBossDomainDockerManager(JBossDomainManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public Cube start(String containerId) {
        final Task<ManagementClient, Boolean> checkingTask = Spacelift.task(serviceManager.getManagementClient(), JBossDomainStartChecker.class);
        return super.start(checkingTask, containerId, ConnectionTimeoutResolver.resolveJBossDomainConnectionTimeout(serviceManager.getConfiguration()), 10);
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
