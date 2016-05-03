package travel.snapshot.qa.manager.jboss.check;

import org.arquillian.spacelift.task.Task;
import org.jboss.as.arquillian.container.ManagementClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Checks if a JBoss container in a standalone mode is started or not by checking its state by management client.
 *
 * @see ManagementClient#isServerInRunningState()
 */
public class JBossStandaloneStartChecker extends Task<ManagementClient, Boolean> {

    private static final Logger logger = LoggerFactory.getLogger(JBossStandaloneStartChecker.class);

    @Override
    protected Boolean process(ManagementClient managementClient) throws Exception {

        try {
            return managementClient.isServerInRunningState();
        } catch (Exception ex) {
            if (logger.isDebugEnabled()) {
                ex.printStackTrace();
            }
        }

        return false;
    }
}
