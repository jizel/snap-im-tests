package travel.snapshot.qa.manager.tomcat.spacelift;

import org.arquillian.spacelift.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.manager.tomcat.TomcatManager;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;

/**
 * Starts a Tomcat container. This fails when a container is already started.
 */
public class TomcatStarter extends Task<TomcatManagerConfiguration, TomcatManager> {

    private static final Logger logger = LoggerFactory.getLogger(TomcatStarter.class);

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

        logger.info("Starting Tomcat container located at {}", this.configuration.getCatalinaHome());

        manager.start();

        return manager;
    }

    private void setConfiguration(TomcatManagerConfiguration configuration) {
        if (configuration != null) {
            this.configuration = configuration;
        }
    }
}
