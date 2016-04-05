package travel.snapshot.qa.manager.jboss.api;

import travel.snapshot.qa.manager.api.container.ContainerManager;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;

/**
 * Manages container lifecycle and deployments of JBoss container, either in standalone or domain mode.
 */
public interface JBossContainerManager extends ContainerManager<JBossManagerConfiguration> {

    /**
     * @return deployer for underlying container to deploy artefacts either for standalon or domain containers
     */
    JBossContainerDeployer getDeployer();
}
