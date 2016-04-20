package travel.snapshot.qa.manager.generic.impl;

import travel.snapshot.qa.manager.generic.api.GenericManager;
import travel.snapshot.qa.manager.generic.configuration.GenericConfiguration;

public class GenericManagerImpl implements GenericManager {

    private final GenericConfiguration configuration;

    public GenericManagerImpl() {
        configuration = new GenericConfiguration.Builder().build();
    }

    public GenericManagerImpl(final GenericConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public GenericConfiguration getConfiguration() {
        return configuration;
    }
}
