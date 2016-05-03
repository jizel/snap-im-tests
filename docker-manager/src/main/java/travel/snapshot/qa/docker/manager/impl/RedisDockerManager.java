package travel.snapshot.qa.docker.manager.impl;

import static travel.snapshot.qa.docker.ServiceType.REDIS;

import org.arquillian.cube.spi.Cube;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.task.Task;
import travel.snapshot.qa.docker.ServiceType;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.manager.redis.api.RedisManager;
import travel.snapshot.qa.manager.redis.check.RedisStartedCheckTask;
import travel.snapshot.qa.manager.redis.configuration.RedisManagerConfiguration;

/**
 * Implements Redis Docker service with respective startup checking task.
 *
 * Redis has default precedence.
 */
public class RedisDockerManager extends DockerServiceManager<RedisManager> {

    private static final String REDIS_CONNECTION_TIMEOUT_PROPERTY = "docker.redis.connection.timeout";

    public RedisDockerManager(RedisManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public Cube start(String containerId) {
        final Task<RedisManagerConfiguration, Boolean> checkingTask = Spacelift.task(serviceManager.getConfiguration(), RedisStartedCheckTask.class);

        final long timeout = resolveTimeout(serviceManager.getConfiguration().getStartupTimeoutInSeconds(), REDIS_CONNECTION_TIMEOUT_PROPERTY, REDIS);

        return super.start(checkingTask, containerId, timeout, 5);
    }

    @Override
    public ServiceType provides() {
        return REDIS;
    }
}
