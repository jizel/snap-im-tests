package travel.snapshot.qa.manager.jboss;

import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.task.Task;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.ModelControllerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.manager.api.container.ContainerManagerConfigurationException;
import travel.snapshot.qa.manager.api.container.ContainerManagerException;
import travel.snapshot.qa.manager.jboss.check.JBossStandaloneStartChecker;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;
import travel.snapshot.qa.manager.jboss.impl.JBossStandaloneDeployer;
import travel.snapshot.qa.manager.jboss.impl.ManagementClientFactory;
import travel.snapshot.qa.manager.jboss.impl.ModelControllerClientBuilder;

public class JBossStandaloneManager extends AbstractJBossManager<ManagementClient, ModelControllerClient, JBossStandaloneDeployer> {

    private static final Logger logger = LoggerFactory.getLogger(JBossStandaloneManager.class);

    /**
     * Creates manager with default standalone configuration ready to manage local JBoss containers.
     */
    public JBossStandaloneManager() {
        this(new JBossManagerConfiguration.Builder().build());
    }

    /**
     * @param configuration configuration for JBoss standalone manager
     * @throws ContainerManagerConfigurationException thrown in case configuration is not 'standalone'.
     */
    public JBossStandaloneManager(final JBossManagerConfiguration configuration) throws ContainerManagerConfigurationException {
        this(configuration, new ModelControllerClientBuilder.Standalone(configuration).build());
    }

    /**
     * @param configuration         configuration for JBoss standalone manager
     * @param modelControllerClient model controller client to build management client from
     * @throws ContainerManagerConfigurationException thrown in case configuration is not 'standalone'
     */
    public JBossStandaloneManager(final JBossManagerConfiguration configuration, final ModelControllerClient modelControllerClient) throws ContainerManagerConfigurationException {
        this(configuration, new ManagementClientFactory.Standalone(configuration).modelControllerClient(modelControllerClient).build());
    }

    /**
     * @param configuration    configuration for JBoss standalone manager
     * @param managementClient standalone management client
     * @throws ContainerManagerConfigurationException thrown in case configuration is not 'standalone'.
     */
    public JBossStandaloneManager(final JBossManagerConfiguration configuration, final ManagementClient managementClient) throws ContainerManagerConfigurationException {
        super(configuration, managementClient, new JBossStandaloneDeployer(managementClient));

        if (configuration.isDomain()) {
            throw new ContainerManagerConfigurationException("Provided JBoss manager configuration is 'domain' for standalone manager.");
        }
    }

    @Override
    public boolean isRunning() throws ContainerManagerException {
        return getManagementClient().isServerInRunningState();
    }

    @Override
    public ManagementClient getManagementClient() {
        return managementClient;
    }

    @Override
    public ModelControllerClient getModelControllerClient() {
        return getManagementClient().getControllerClient();
    }

    @Override
    public JBossStandaloneDeployer getDeployer() {
        return deployer;
    }

    @Override
    public void closeManagementClient(ManagementClient managementClient) throws ContainerManagerException {
        try {
            managementClient.close();
        } catch (Exception ex) {
            throw new ContainerManagerException(String.format("Closing of JBoss standalone management client has not been successful: %s", ex.getMessage()), ex);
        }
    }

    @Override
    Task<ManagementClient, Boolean> getStartingTask() {
        return Spacelift.task(getManagementClient(), JBossStandaloneStartChecker.class);
    }
}
