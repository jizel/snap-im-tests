package travel.snapshot.qa.manager.activemq.check;

import org.arquillian.spacelift.task.Task;
import travel.snapshot.qa.manager.activemq.api.ActiveMQManager;
import travel.snapshot.qa.manager.activemq.api.ActiveMQManagerException;
import travel.snapshot.qa.manager.activemq.configuration.ActiveMQManagerConfiguration;
import travel.snapshot.qa.manager.activemq.impl.ActiveMQManagerImpl;

import javax.jms.Connection;

/**
 * Checks if we are able to get {@link javax.jms.Connection} to ActiveMQ broker.
 */
public class ActiveMQStartedCheckTask extends Task<ActiveMQManagerConfiguration, Boolean> {

    @Override
    protected Boolean process(ActiveMQManagerConfiguration configuration) throws Exception {

        final ActiveMQManager activeMQManager = new ActiveMQManagerImpl(configuration);

        boolean started = false;

        Connection connection = null;

        try {
            connection = activeMQManager.buildConnection();
            started = true;
        } catch (ActiveMQManagerException ex) {
            // intentionally empty
        } finally {
            if (connection != null) {
                activeMQManager.closeConnection(connection);
            }
        }

        return started;
    }
}
