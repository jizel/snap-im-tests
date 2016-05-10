package travel.snapshot.qa.manager.jboss;

import static org.jboss.as.controller.client.helpers.domain.ServerStatus.STARTED;

import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.task.Task;
import org.jboss.as.arquillian.container.domain.Domain.Server;
import org.jboss.as.arquillian.container.domain.ManagementClient;
import org.jboss.as.controller.client.helpers.domain.DomainClient;
import org.jboss.as.controller.client.helpers.domain.ServerIdentity;
import org.jboss.as.controller.client.helpers.domain.ServerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.manager.api.container.ContainerManagerConfigurationException;
import travel.snapshot.qa.manager.api.container.ContainerManagerException;
import travel.snapshot.qa.manager.jboss.check.JBossDomainStartChecker;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;
import travel.snapshot.qa.manager.jboss.impl.JBossDomainDeployer;
import travel.snapshot.qa.manager.jboss.impl.ManagementClientFactory;
import travel.snapshot.qa.manager.jboss.impl.ModelControllerClientBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JBossDomainManager extends AbstractJBossManager<ManagementClient, DomainClient, JBossDomainDeployer> {

    private static final Logger logger = LoggerFactory.getLogger(JBossDomainManager.class);

    /**
     * Creates manager with default domain configuration ready to manage local JBoss containers.
     */
    public JBossDomainManager() {
        this(new JBossManagerConfiguration.Builder().domain().build());
    }

    /**
     * @param configuration configuration for JBoss domain manager
     * @throws ContainerManagerConfigurationException thrown in case configuration is not 'domain'.
     */
    public JBossDomainManager(final JBossManagerConfiguration configuration) throws ContainerManagerConfigurationException {
        this(configuration, DomainClient.Factory.create(new ModelControllerClientBuilder.Domain(configuration).build()));
    }

    /**
     * @param configuration configuration for JBoss domain manager
     * @param domainClient  domain model controller client to build domain management client from
     * @throws ContainerManagerConfigurationException thrown in case configuration is not 'domain'.
     */
    public JBossDomainManager(final JBossManagerConfiguration configuration, final DomainClient domainClient) throws ContainerManagerConfigurationException {
        this(configuration, new ManagementClientFactory.Domain().modelControllerClient(domainClient).build());
    }

    /**
     * @param configuration    configuration for JBoss domain manager
     * @param managementClient domain management client
     * @throws ContainerManagerConfigurationException thrown in case configuration is not 'domain'.
     */
    public JBossDomainManager(final JBossManagerConfiguration configuration, final ManagementClient managementClient) throws ContainerManagerConfigurationException {
        super(configuration, managementClient, new JBossDomainDeployer(managementClient.getControllerClient(), configuration));

        if (!configuration.isDomain()) {
            throw new ContainerManagerConfigurationException("Provided JBoss manager configuration is 'standalone' for domain manager.");
        }
    }

    public Map<ServerIdentity, ServerStatus> getServerStatuses() {
        return getManagementClient().getControllerClient().getServerStatuses();
    }

    public List<Server> getServers(final ServerStatus status) {
        return getServerStatuses()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() == status)
                .map(entry -> {
                    final ServerIdentity identity = entry.getKey();

                    return new Server(identity.getServerName(), identity.getHostName(), identity.getServerGroupName(), false);
                }).collect(Collectors.toList());
    }

    /**
     * @return servers with status STARTED
     */
    public List<Server> getServers() {
        return getServers(STARTED);
    }

    @Override
    public ManagementClient getManagementClient() {
        return managementClient;
    }

    @Override
    public DomainClient getModelControllerClient() {
        return getManagementClient().getControllerClient();
    }

    @Override
    public boolean isRunning() throws ContainerManagerException {
        return getManagementClient().isDomainInRunningState();
    }

    @Override
    public JBossDomainDeployer getDeployer() {
        return deployer;
    }

    @Override
    public void closeManagementClient(ManagementClient managementClient) throws ContainerManagerException {
        try {
            managementClient.close();
        } catch (Exception ex) {
            throw new ContainerManagerException(String.format("Closing of JBoss domain management client has not been successful: %s", ex.getMessage()), ex);
        }
    }

    @Override
    Task<ManagementClient, Boolean> getStartingTask() {
        return Spacelift.task(getManagementClient(), JBossDomainStartChecker.class);
    }
}
