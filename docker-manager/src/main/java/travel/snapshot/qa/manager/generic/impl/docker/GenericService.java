package travel.snapshot.qa.manager.generic.impl.docker;

import travel.snapshot.qa.docker.Service;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.manager.generic.api.GenericManager;
import travel.snapshot.qa.manager.generic.configuration.GenericConfiguration;
import travel.snapshot.qa.manager.generic.impl.GenericManagerImpl;

import java.util.function.Supplier;

/**
 * Represents dummy Docker service which does not really manage anything. It just starts and stops a container.
 */
public class GenericService implements Service<GenericManager, GenericConfiguration> {

    public static final String DEFAULT_GENERIC_CONTAINER_ID = GenericDockerManager.SERVICE_NAME;

    private final Supplier<GenericConfiguration> genericConfigurationSupplier = () -> new GenericConfiguration.Builder().build();

    @Override
    public DockerServiceManager<GenericManager> init(GenericConfiguration configuration, String containerId) {
        return new GenericDockerManager(new GenericManagerImpl(configuration)).setContainerId(containerId);
    }

    @Override
    public DockerServiceManager<GenericManager> init(String containerId) {
        return init(genericConfigurationSupplier.get(), DEFAULT_GENERIC_CONTAINER_ID);
    }

    @Override
    public DockerServiceManager<GenericManager> init(GenericConfiguration configuration) {
        return init(configuration, DEFAULT_GENERIC_CONTAINER_ID);
    }

    @Override
    public DockerServiceManager<GenericManager> init() {
        return init(genericConfigurationSupplier.get());
    }
}
