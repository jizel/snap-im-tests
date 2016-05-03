package travel.snapshot.qa.manager.jboss;

import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.task.Task;
import org.jboss.as.arquillian.container.domain.Domain;
import org.jboss.as.arquillian.container.domain.ManagementClient;
import org.jboss.as.controller.client.helpers.domain.DomainClient;
import org.jboss.as.controller.client.helpers.domain.ServerIdentity;
import org.jboss.as.controller.client.helpers.domain.ServerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.manager.api.container.ContainerManagerException;
import travel.snapshot.qa.manager.jboss.check.JBossDomainStartChecker;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;
import travel.snapshot.qa.manager.jboss.impl.JBossDomainDeployer;
import travel.snapshot.qa.manager.jboss.impl.ManagementClientFactory;
import travel.snapshot.qa.manager.jboss.impl.ModelControllerClientBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JBossDomainManager extends AbstractJBossManager<ManagementClient, DomainClient> {

    private static final Logger logger = LoggerFactory.getLogger(JBossDomainManager.class);

    private final ManagementClient managementClient;

    private final JBossDomainDeployer deployer;

    public JBossDomainManager() {
        this(new JBossManagerConfiguration.Builder().domain().build());
    }

    /**
     * @param configuration configuration for JBoss domain manager
     * @throws IllegalStateException thrown in case configuration is not 'domain'.
     */
    public JBossDomainManager(final JBossManagerConfiguration configuration) {
        super(configuration);

        if (!configuration.isDomain()) {
            throw new IllegalStateException("Provided JBoss manager configuration is not 'domain' for domain manager.");
        }

        DomainClient domainClient = DomainClient.Factory.create(new ModelControllerClientBuilder.Domain(configuration).build());

        this.managementClient = new ManagementClientFactory.Domain().modelControllerClient(domainClient).build();
        this.deployer = new JBossDomainDeployer(domainClient, configuration);
    }

    public Map<ServerIdentity, ServerStatus> getServerStatuses() {
        return getManagementClient().getControllerClient().getServerStatuses();
    }

    public List<Domain.Server> getServers(final ServerStatus status) {
        return getServerStatuses()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() == status)
                .map(entry -> {
                    final ServerIdentity identity = entry.getKey();

                    return new Domain.Server(identity.getServerName(), identity.getHostName(), identity.getServerGroupName(), false);
                }).collect(Collectors.toList());
    }

    /**
     * @return servers with status STARTED
     */
    public List<Domain.Server> getServers() {
        return getServers(ServerStatus.STARTED);
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
    public void closeManagementClient(ManagementClient managementClient) {
        try {
            managementClient.close();
        } catch (final Exception ex) {
            logger.warn("Caught exception closing ManagementClient", ex);
        }
    }

    @Override
    Task<ManagementClient, Boolean> getStartingTask() {
        return Spacelift.task(getManagementClient(), JBossDomainStartChecker.class);
    }
}
