package travel.snapshot.qa.manager.jboss.spacelift;

import org.arquillian.spacelift.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.manager.jboss.JBossDomainManager;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;

/**
 * Spacelift task for starting JBoss container in domain mode.
 */
public class JBossDomainStarter extends Task<JBossManagerConfiguration, JBossDomainManager> {

    private static final Logger logger = LoggerFactory.getLogger(JBossDomainStarter.class);

    private JBossManagerConfiguration configuration = new JBossManagerConfiguration.Builder().domain().build();

    /**
     * Sets configuration for domain container.
     *
     * @param configuration configuration to use, in case it is null object or it is not 'domain' configuration, it will
     *                      be ignored
     * @return this
     */
    public JBossDomainStarter configuration(JBossManagerConfiguration configuration) {
        setConfiguration(configuration);
        return this;
    }

    @Override
    protected JBossDomainManager process(JBossManagerConfiguration configuration) throws Exception {

        setConfiguration(configuration);

        JBossDomainManager jbossManager = new JBossDomainManager(this.configuration);

        logger.info("Starting JBoss container located at {} in domain mode.", this.configuration.getJVM().getJBossHome());

        jbossManager.start();

        return jbossManager;
    }

    private boolean setConfiguration(JBossManagerConfiguration configuration) {
        if (configuration == null) {
            logger.debug("Provided confiugration is a null object and will be ignored.");
            return false;
        }

        if (!configuration.isDomain()) {
            logger.info("Provided confiugration is not domain and will be ignored.");
        }

        this.configuration = configuration;
        return true;
    }
}
