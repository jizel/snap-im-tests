package travel.snapshot.qa.manager.activemq.api;

import travel.snapshot.qa.manager.activemq.configuration.ActiveMQManagerConfiguration;
import travel.snapshot.qa.manager.api.ServiceManager;

public interface ActiveMQManager extends ServiceManager {

    ActiveMQManagerConfiguration getConfiguration();
}
