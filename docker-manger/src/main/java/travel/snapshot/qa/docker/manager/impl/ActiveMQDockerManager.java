package travel.snapshot.qa.docker.manager.impl;

import org.arquillian.cube.spi.Cube;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.task.Task;
import travel.snapshot.qa.docker.ServiceType;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.manager.activemq.api.ActiveMQManager;
import travel.snapshot.qa.manager.activemq.check.ActiveMQStartedCheckTask;
import travel.snapshot.qa.manager.activemq.configuration.ActiveMQManagerConfiguration;

public class ActiveMQDockerManager extends DockerServiceManager<ActiveMQManager> {

    private static final int PRECENDENCE = 100;

    public ActiveMQDockerManager(ActiveMQManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public Cube start(String containerId) {
        final Task<ActiveMQManagerConfiguration, Boolean> checkingTask = Spacelift.task(serviceManager.getConfiguration(), ActiveMQStartedCheckTask.class);
        return super.start(checkingTask, containerId, serviceManager.getConfiguration().getStartupTimeoutInSeconds(), 5);
    }

    @Override
    public ServiceType provides() {
        return ServiceType.ACTIVEMQ;
    }

    @Override
    public int precedence() {
        return PRECENDENCE;
    }
}
