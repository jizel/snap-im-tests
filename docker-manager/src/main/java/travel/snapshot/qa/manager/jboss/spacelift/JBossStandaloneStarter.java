package travel.snapshot.qa.manager.jboss.spacelift;

import org.arquillian.spacelift.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.manager.jboss.JBossStandaloneManager;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;

/**
 * Spacelift task for starting JBoss container in standalone mode.
 */
public class JBossStandaloneStarter extends Task<JBossManagerConfiguration, JBossStandaloneManager> {

    private static final Logger logger = LoggerFactory.getLogger(JBossStandaloneStarter.class);

    private JBossManagerConfiguration configuration;

    /**
     * Sets configuration for standalone container.
     *
     * @param configuration configuration to use, in case it is null object or it is 'domain' configuration, it will be
     *                      ignored
     * @return this
     */
    public JBossStandaloneStarter configuration(JBossManagerConfiguration configuration) {
        setConfiguration(configuration);
        return this;
    }

    @Override
    protected JBossStandaloneManager process(JBossManagerConfiguration configuration) throws Exception {

        setConfiguration(configuration);

        JBossStandaloneManager jbossManager = new JBossStandaloneManager(this.configuration);

        logger.info("Starting JBoss container located at {} in standalone mode.", this.configuration.getJVM().getJBossHome());

        jbossManager.start();

        return jbossManager;
    }

    private boolean setConfiguration(JBossManagerConfiguration configuration) {
        if (configuration == null) {
            logger.debug("Provided confiugration is a null object and will be ignored.");
            return false;
        }

        if (!configuration.isDomain()) {
            logger.info("Provided confiugration is domain and will be ignored.");
        }

        this.configuration = configuration;
        return true;
    }
}
