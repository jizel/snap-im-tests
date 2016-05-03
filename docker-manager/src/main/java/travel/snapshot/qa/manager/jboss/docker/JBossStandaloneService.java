package travel.snapshot.qa.manager.jboss.docker;

import travel.snapshot.qa.docker.Service;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.manager.jboss.JBossStandaloneManager;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;

import java.util.function.Supplier;

/**
 * Represents Docker service with running JBoss container in standalone mode. When not explicitly specified, the name of
 * the container to start will be "jboss".
 */
public final class JBossStandaloneService implements Service<JBossStandaloneManager, JBossManagerConfiguration> {

    public static final String DEFAULT_JBOSS_STANDALONE_CONTAINER_ID = JBossStandaloneDockerManager.SERVICE_NAME;

    private final Supplier<JBossManagerConfiguration> jbossConfigurationSupplier = () -> new JBossManagerConfiguration.Builder().remote().build();

    /**
     * @param configuration configuration of a service
     * @param containerId   container ID which this service will manage
     * @return respective Docker manager for standalone JBoss container
     * @throws IllegalStateException in case provided {@code configuration} is "domain" or not "remote".
     */
    @Override
    public DockerServiceManager<JBossStandaloneManager> init(JBossManagerConfiguration configuration, String containerId) {

        if (configuration.isDomain()) {
            throw new IllegalStateException("Used configuration is 'domain' for standalone service.");
        }

        if (!configuration.isRemote()) {
            throw new IllegalStateException("Used configuration is not 'remote'.");
        }

        return new JBossStandaloneDockerManager(new JBossStandaloneManager(configuration)).setContainerId(containerId);
    }

    @Override
    public DockerServiceManager<JBossStandaloneManager> init(String containerId) {
        return init(jbossConfigurationSupplier.get(), containerId);
    }

    @Override
    public DockerServiceManager<JBossStandaloneManager> init(JBossManagerConfiguration configuration) {
        return init(configuration, DEFAULT_JBOSS_STANDALONE_CONTAINER_ID);
    }

    @Override
    public DockerServiceManager<JBossStandaloneManager> init() {
        return init(jbossConfigurationSupplier.get());
    }
}
