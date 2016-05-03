package travel.snapshot.qa.manager.activemq.impl.docker;

import org.arquillian.cube.spi.Cube;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.task.Task;
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

    public static final String SERVICE_NAME = "activemq";

    private static final String ACTIVEMQ_CONNECTION_TIMEOUT_PROPERTY = "docker." + SERVICE_NAME + ".connection.timeout";

    private static final int PRECENDENCE = 100;

    public ActiveMQDockerManager(ActiveMQManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public Cube start(String containerId) {
        final Task<ActiveMQManagerConfiguration, Boolean> checkingTask = Spacelift.task(serviceManager.getConfiguration(), ActiveMQStartedCheckTask.class);

        final long timeout = resolveTimeout(serviceManager.getConfiguration().getStartupTimeoutInSeconds(), ACTIVEMQ_CONNECTION_TIMEOUT_PROPERTY, provides());

        return super.start(checkingTask, containerId, timeout, 5);
    }

    @Override
    public String provides() {
        return SERVICE_NAME;
    }

    @Override
    public int precedence() {
        return PRECENDENCE;
    }
}
