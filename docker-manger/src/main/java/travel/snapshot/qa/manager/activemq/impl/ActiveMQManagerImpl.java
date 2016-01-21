package travel.snapshot.qa.manager.activemq.impl;

import travel.snapshot.qa.manager.activemq.api.ActiveMQManager;
import travel.snapshot.qa.manager.activemq.configuration.ActiveMQManagerConfiguration;

public class ActiveMQManagerImpl implements ActiveMQManager {

    private final ActiveMQManagerConfiguration configuration;

    public ActiveMQManagerImpl() {
        this(new ActiveMQManagerConfiguration.Builder().build());
    }

    public ActiveMQManagerImpl(final ActiveMQManagerConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public ActiveMQManagerConfiguration getConfiguration() {
        return configuration;
    }
}
