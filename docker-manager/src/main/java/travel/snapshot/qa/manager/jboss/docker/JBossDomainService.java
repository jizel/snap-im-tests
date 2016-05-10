package travel.snapshot.qa.manager.jboss.docker;

import travel.snapshot.qa.docker.Service;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.manager.jboss.JBossDomainManager;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;

import java.util.function.Supplier;

/**
 * Represents Docker service with running JBoss container in domain mode. When not explicitly specified, the name of the
 * container to start will be "jboss_domain".
 */
public final class JBossDomainService implements Service<JBossDomainManager, JBossManagerConfiguration> {

    public static final String DEFAULT_JBOSS_DOMAIN_CONTAINER_ID = JBossDomainDockerManager.SERVICE_NAME;

    private final Supplier<JBossManagerConfiguration> jbossConfigurationSupplier = () -> new JBossManagerConfiguration.Builder().domain().remote().build();

    /**
     * @param configuration configuration of a service
     * @param containerId   container ID which this service will manage
     * @return respective Docker manager for domain JBoss container
     * @throws IllegalStateException in case provided {@code configuration} is not "domain" or "remote".
     */
    @Override
    public DockerServiceManager<JBossDomainManager> init(JBossManagerConfiguration configuration, String containerId) {

        if (!configuration.isDomain()) {
            throw new IllegalStateException("Used configuratoin is not 'domain' for domain service.");
        }

        if (!configuration.isRemote()) {
            throw new IllegalStateException("Used configuration is not 'remote'.");
        }

        return new JBossDomainDockerManager(new JBossDomainManager(configuration)).setContainerId(containerId);
    }

    @Override
    public DockerServiceManager<JBossDomainManager> init(String containerId) {
        return init(jbossConfigurationSupplier.get(), containerId);
    }

    @Override
    public DockerServiceManager<JBossDomainManager> init(JBossManagerConfiguration configuration) {
        return init(configuration, DEFAULT_JBOSS_DOMAIN_CONTAINER_ID);
    }

    @Override
    public DockerServiceManager<JBossDomainManager> init() {
        return init(jbossConfigurationSupplier.get());
    }
}
