package travel.snapshot.qa.manager.generic.api;

import travel.snapshot.qa.manager.api.ServiceManager;
import travel.snapshot.qa.manager.generic.configuration.GenericConfiguration;

/**
 * Represents generic service without any client API.
 */
public interface GenericManager extends ServiceManager {

    GenericConfiguration getConfiguration();
}
