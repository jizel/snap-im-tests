package travel.snapshot.qa.manager.activemq.impl.docker;

import travel.snapshot.qa.docker.Service;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.manager.activemq.api.ActiveMQManager;
import travel.snapshot.qa.manager.activemq.configuration.ActiveMQManagerConfiguration;
import travel.snapshot.qa.manager.activemq.impl.ActiveMQManagerImpl;

import java.util.function.Supplier;

public final class ActiveMQService implements Service<ActiveMQManager, ActiveMQManagerConfiguration> {

    public static final String DEFAULT_ACTIVEMQ_CONTAINER_ID = ActiveMQDockerManager.SERVICE_NAME;

    private final Supplier<ActiveMQManagerConfiguration> activeMQConfigurationSupplier = () -> new ActiveMQManagerConfiguration.Builder().build();

    @Override
    public DockerServiceManager<ActiveMQManager> init(ActiveMQManagerConfiguration configuration, String containerId) {
        return new ActiveMQDockerManager(new ActiveMQManagerImpl(configuration)).setContainerId(containerId);
    }

    @Override
    public DockerServiceManager<ActiveMQManager> init(String containerId) {
        return init(activeMQConfigurationSupplier.get(), containerId);
    }

    @Override
    public DockerServiceManager<ActiveMQManager> init(ActiveMQManagerConfiguration configuration) {
        return init(configuration, DEFAULT_ACTIVEMQ_CONTAINER_ID);
    }

    @Override
    public DockerServiceManager<ActiveMQManager> init() {
        return init(activeMQConfigurationSupplier.get());
    }
}
