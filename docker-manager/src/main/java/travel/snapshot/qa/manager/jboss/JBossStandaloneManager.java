package travel.snapshot.qa.manager.jboss;

import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.task.Task;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.ModelControllerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.IoUtils;
import travel.snapshot.qa.manager.api.container.ContainerManagerException;
import travel.snapshot.qa.manager.jboss.check.JBossStandaloneStartChecker;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;
import travel.snapshot.qa.manager.jboss.impl.JBossStandaloneDeployer;
import travel.snapshot.qa.manager.jboss.impl.ManagementClientFactory;

public class JBossStandaloneManager extends AbstractJBossManager<ManagementClient, ModelControllerClient> {

    private static final Logger logger = LoggerFactory.getLogger(JBossStandaloneManager.class);

    private final ManagementClient managementClient;

    private final JBossStandaloneDeployer deployer;

    public JBossStandaloneManager() {
        this(new JBossManagerConfiguration());
    }

    public JBossStandaloneManager(JBossManagerConfiguration configuration) throws ContainerManagerException {
        super(configuration);
        this.managementClient = new ManagementClientFactory.Standalone(configuration).build();
        this.deployer = new JBossStandaloneDeployer(managementClient);
    }

    @Override
    public boolean isRunning() throws ContainerManagerException {
        return managementClient.isServerInRunningState();
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
    public void closeManagementClient(ManagementClient managementClient) {
        try {
            IoUtils.safeClose(managementClient);
        } catch (final Exception ex) {
            logger.warn("Caught exception closing ManagementClient", ex);
        }
    }

    @Override
    Task<ManagementClient, Boolean> getStartingTask() {
        return Spacelift.task(getManagementClient(), JBossStandaloneStartChecker.class);
    }
}
