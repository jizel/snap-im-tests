package travel.snapshot.qa.docker.manager.impl;

import org.arquillian.cube.spi.Cube;
import org.arquillian.spacelift.task.Task;
import travel.snapshot.qa.connection.ConnectionCheck;
import travel.snapshot.qa.docker.ServiceType;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.manager.activemq.api.ActiveMQManager;
import travel.snapshot.qa.manager.tomcat.configuration.Validate;

public class ActiveMQDockerManager extends DockerServiceManager<ActiveMQManager> {

    private static final int PRECENDENCE = 100;

    public ActiveMQDockerManager(ActiveMQManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public Cube start(String containerId) {
        return this.start(null, containerId, serviceManager.getConfiguration().getStartupTimeoutInSeconds(), 5);
    }

    @Override
    public Cube start(final Task<?, Boolean> checkingTask, final String containerId, long timeout, long reexecutionInterval) {

        Validate.notNullOrEmpty(containerId, "Container ID to start must not be a null object or an empty String!");

        if (started) {
            throw new IllegalStateException(String.format("Unable to start already started container '%s'", containerId));
        }

        dockerContainer = dockerManager.start(containerId);

        new ConnectionCheck.Builder()
                .host(serviceManager.getConfiguration().getBrokerAddress())
                .port(serviceManager.getConfiguration().getBrokerPort())
                .timeout(timeout)
                .reexecutionInterval(reexecutionInterval)
                .build()
                .execute();

        started = true;

        return dockerContainer;
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
