package travel.snapshot.qa.manager.mongodb.impl.docker;

import travel.snapshot.qa.docker.Service;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.manager.mongodb.api.MongoDBManager;
import travel.snapshot.qa.manager.mongodb.configuration.MongoDBManagerConfiguration;
import travel.snapshot.qa.manager.mongodb.impl.MongoDBManagerImpl;

import java.util.function.Supplier;

/**
 * Represents Docker service with running Mongo container. When not explicitly specified, the name of the container to
 * start will be "mongodb".
 */
public final class MongoDBService implements Service<MongoDBManager, MongoDBManagerConfiguration> {

    public static final String DEFAULT_MONGODB_CONTAINER_ID = MongoDBDockerManager.SERVICE_NAME;

    private final Supplier<MongoDBManagerConfiguration> mongoDBConfigurationSupplier = () -> new MongoDBManagerConfiguration.Builder().build();

    @Override
    public DockerServiceManager<MongoDBManager> init(MongoDBManagerConfiguration configuration, String containerId) {
        return new MongoDBDockerManager(new MongoDBManagerImpl(configuration)).setContainerId(containerId);
    }

    @Override
    public DockerServiceManager<MongoDBManager> init(String containerId) {
        return init(mongoDBConfigurationSupplier.get(), containerId);
    }

    @Override
    public DockerServiceManager<MongoDBManager> init(MongoDBManagerConfiguration configuration) {
        return init(configuration, DEFAULT_MONGODB_CONTAINER_ID);
    }

    @Override
    public DockerServiceManager<MongoDBManager> init() {
        return init(mongoDBConfigurationSupplier.get());
    }
}
