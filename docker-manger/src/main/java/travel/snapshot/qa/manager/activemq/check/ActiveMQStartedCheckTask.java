package travel.snapshot.qa.manager.activemq.check;

import org.arquillian.spacelift.task.Task;
import travel.snapshot.qa.connection.ConnectionCheck;
import travel.snapshot.qa.connection.ConnectionCheckExecutor;
import travel.snapshot.qa.manager.activemq.configuration.ActiveMQManagerConfiguration;

/**
 * Simple check for TCP connection to ActiveMQ broker.
 */
public class ActiveMQStartedCheckTask extends Task<ActiveMQManagerConfiguration, Boolean> {

    @Override
    protected Boolean process(ActiveMQManagerConfiguration configuration) throws Exception {

        // TODO
        // There should be check if we can get javax.jms.Connection
        // it will be added here after the refactoring of integration-jms-client where
        // that helper API is already implemented

        boolean started = false;

        try {
            new ConnectionCheck.Builder()
                    .timeout(configuration.getStartupTimeoutInSeconds())
                    .reexecutionInterval(5)
                    .host(configuration.getBrokerAddress())
                    .port(configuration.getBrokerPort())
                    .build()
                    .execute();
            started = true;
        } catch (ConnectionCheckExecutor.ConnectionCheckException ex) {

        }

        return started;
    }
}
