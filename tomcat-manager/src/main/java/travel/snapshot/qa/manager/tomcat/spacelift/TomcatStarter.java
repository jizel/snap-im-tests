package travel.snapshot.qa.manager.tomcat.spacelift;

import org.arquillian.spacelift.task.Task;
import travel.snapshot.qa.manager.tomcat.TomcatManager;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;

import java.util.logging.Logger;

/**
 * Starts a Tomcat container. This fails when a container is already started.
 */
public class TomcatStarter extends Task<TomcatManagerConfiguration, TomcatManager> {

    private static final Logger logger = Logger.getLogger(TomcatStarter.class.getName());

    private TomcatManagerConfiguration configuration;

    public TomcatStarter() {
        configuration = new TomcatManagerConfiguration.Builder().build();
    }

    public TomcatStarter configuration(TomcatManagerConfiguration configuration) {
        setConfiguration(configuration);
        return this;
    }

    @Override
    protected TomcatManager process(TomcatManagerConfiguration configuration) throws Exception {

        setConfiguration(configuration);

        TomcatManager manager = new TomcatManager(this.configuration);

        logger.info(String.format("Starting Tomcat container located at %s", this.configuration.getCatalinaHome()));

        manager.start();

        return manager;
    }

    private void setConfiguration(TomcatManagerConfiguration configuration) {
        if (configuration != null) {
            this.configuration = configuration;
        }
    }
}
