package travel.snapshot.qa.manager.redis.impl.docker;

import travel.snapshot.qa.docker.Service;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.manager.redis.api.RedisManager;
import travel.snapshot.qa.manager.redis.configuration.RedisManagerConfiguration;
import travel.snapshot.qa.manager.redis.impl.RedisManagerImpl;

import java.util.function.Supplier;

public final class RedisService implements Service<RedisManager, RedisManagerConfiguration> {

    public static final String DEFAULT_REDIS_CONTAINER_ID = RedisDockerManager.SERVICE_NAME;

    private final Supplier<RedisManagerConfiguration> redisConfigurationSupplier = () -> new RedisManagerConfiguration.Builder().build();

    @Override
    public DockerServiceManager<RedisManager> init(RedisManagerConfiguration configuration, String containerId) {
        return new RedisDockerManager(new RedisManagerImpl(configuration)).setContainerId(containerId);
    }

    @Override
    public DockerServiceManager<RedisManager> init(String containerId) {
        return init(redisConfigurationSupplier.get(), containerId);
    }

    @Override
    public DockerServiceManager<RedisManager> init(RedisManagerConfiguration configuration) {
        return init(configuration, DEFAULT_REDIS_CONTAINER_ID);
    }

    @Override
    public DockerServiceManager<RedisManager> init() {
        return init(redisConfigurationSupplier.get());
    }
}