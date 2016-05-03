package travel.snapshot.qa.docker.manager.impl;

import static travel.snapshot.qa.docker.ServiceType.MONGODB;

import org.arquillian.cube.spi.Cube;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.task.Task;
import travel.snapshot.qa.docker.ServiceType;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.manager.mongodb.api.MongoDBManager;
import travel.snapshot.qa.manager.mongodb.check.MongoDBStartCheckTask;
import travel.snapshot.qa.manager.mongodb.configuration.MongoDBManagerConfiguration;

/**
 * Implements MongoDB Docker service with respective startup checking task.
 *
 * MongoDB has precedence 100.
 */
public class MongoDBDockerManager extends DockerServiceManager<MongoDBManager> {

    private static final String MONGODB_CONNECTION_TIMEOUT_PROPERTY = "docker.mongodb.connection.timeout";

    private static final int PRECEDENCE = 100;

    public MongoDBDockerManager(final MongoDBManager mongoDBManager) {
        super(mongoDBManager);
    }

    @Override
    public Cube start(final String containerId) {
        final Task<MongoDBManagerConfiguration, Boolean> checkingTask = Spacelift.task(serviceManager.getConfiguration(), MongoDBStartCheckTask.class);

        final long timeout = resolveTimeout(serviceManager.getConfiguration().getStartupTimeoutInSeconds(), MONGODB_CONNECTION_TIMEOUT_PROPERTY, MONGODB);

        return super.start(checkingTask, containerId, timeout, 10);
    }

    @Override
    public ServiceType provides() {
        return MONGODB;
    }

    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}
