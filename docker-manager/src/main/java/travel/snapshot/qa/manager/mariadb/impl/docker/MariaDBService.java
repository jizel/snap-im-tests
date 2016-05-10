package travel.snapshot.qa.manager.mariadb.impl.docker;

import travel.snapshot.qa.docker.Service;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.manager.mariadb.api.MariaDBManager;
import travel.snapshot.qa.manager.mariadb.configuration.MariaDBManagerConfiguration;
import travel.snapshot.qa.manager.mariadb.impl.MariaDBManagerImpl;

import java.util.function.Supplier;

/**
 * Represents Docker service with running MariaDB container. When not explicitly specified, the name of the container to
 * start will be "mariadb".
 */
public final class MariaDBService implements Service<MariaDBManager, MariaDBManagerConfiguration> {

    public static final String DEFAULT_MARIADB_CONTAINER_ID = MariaDBDockerManager.SERVICE_NAME;

    private final Supplier<MariaDBManagerConfiguration> mariadbConfigurationSupplier = () -> new MariaDBManagerConfiguration.Builder().build();

    @Override
    public DockerServiceManager<MariaDBManager> init(MariaDBManagerConfiguration configuration, String containerId) {
        return new MariaDBDockerManager(new MariaDBManagerImpl(configuration)).setContainerId(containerId);
    }

    @Override
    public DockerServiceManager<MariaDBManager> init(String containerId) {
        return init(mariadbConfigurationSupplier.get(), containerId);
    }

    @Override
    public DockerServiceManager<MariaDBManager> init(MariaDBManagerConfiguration configuration) {
        return init(configuration, DEFAULT_MARIADB_CONTAINER_ID);
    }

    @Override
    public DockerServiceManager<MariaDBManager> init() {
        return init(mariadbConfigurationSupplier.get());
    }
}
