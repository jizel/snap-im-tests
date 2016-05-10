package travel.snapshot.qa.manager.tomcat.docker;

import travel.snapshot.qa.docker.Service;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.manager.tomcat.TomcatManager;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;

import java.util.function.Supplier;

/**
 * Represents Docker service with running Tomcat container. When not explicitly specified the name of the container to
 * start will be "tomcat".
 */
public final class TomcatService implements Service<TomcatManager, TomcatManagerConfiguration> {

    public static final String DEFAULT_TOMCAT_CONTAINER_ID = TomcatDockerManager.SERVICE_NAME;

    private final Supplier<TomcatManagerConfiguration> tomcatConfigurationSupplier = () -> new TomcatManagerConfiguration.Builder().remote().build();

    @Override
    public DockerServiceManager<TomcatManager> init(TomcatManagerConfiguration configuration, String containerId) {

        if (!configuration.isRemote()) {
            throw new IllegalStateException("Used configuration is not remote.");
        }

        return new TomcatDockerManager(new TomcatManager(configuration)).setContainerId(containerId);
    }

    @Override
    public DockerServiceManager<TomcatManager> init(String containerId) {
        return init(tomcatConfigurationSupplier.get(), containerId);
    }

    @Override
    public DockerServiceManager<TomcatManager> init(TomcatManagerConfiguration configuration) {
        return init(configuration, DEFAULT_TOMCAT_CONTAINER_ID);
    }

    @Override
    public DockerServiceManager<TomcatManager> init() {
        return init(tomcatConfigurationSupplier.get());
    }
}
