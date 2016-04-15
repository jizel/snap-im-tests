package travel.snapshot.qa.manager.jboss.check;

import org.arquillian.spacelift.task.Task;
import org.jboss.as.arquillian.container.ManagementClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
