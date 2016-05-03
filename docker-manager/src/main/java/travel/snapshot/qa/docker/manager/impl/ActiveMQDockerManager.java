package travel.snapshot.qa.docker.manager.impl;

import static travel.snapshot.qa.docker.ServiceType.ACTIVEMQ;

import org.arquillian.cube.spi.Cube;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.task.Task;
import travel.snapshot.qa.docker.ServiceType;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.manager.activemq.api.ActiveMQManager;
import travel.snapshot.qa.manager.activemq.check.ActiveMQStartedCheckTask;
import travel.snapshot.qa.manager.activemq.configuration.ActiveMQManagerConfiguration;

/**
 * Implements ActiveMQ Docker service with respective startup checking task.
 *
 * ActiveMQ has precedence 100.
 */
public class ActiveMQDockerManager extends DockerServiceManager<ActiveMQManager> {

    private static final String ACTIVEMQ_CONNECTION_TIMEOUT_PROPERTY = "docker.activemq.connection.timeout";

    private static final int PRECENDENCE = 100;

    public ActiveMQDockerManager(ActiveMQManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public Cube start(String containerId) {
        final Task<ActiveMQManagerConfiguration, Boolean> checkingTask = Spacelift.task(serviceManager.getConfiguration(), ActiveMQStartedCheckTask.class);

        final long timeout = resolveTimeout(serviceManager.getConfiguration().getStartupTimeoutInSeconds(), ACTIVEMQ_CONNECTION_TIMEOUT_PROPERTY, ACTIVEMQ);

        return super.start(checkingTask, containerId, timeout, 5);
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
