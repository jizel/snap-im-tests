package travel.snapshot.qa.manager.jboss.api;

import travel.snapshot.qa.manager.api.container.ContainerManager;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;

/**
 * Manages container lifecycle and deployments.
 */
public interface JBossContainerManager extends ContainerManager {

    JBossManagerConfiguration getConfiguration();
}
