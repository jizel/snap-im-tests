package travel.snapshot.qa.manager.jboss.spacelift;

import org.arquillian.spacelift.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.manager.jboss.JBossManager;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;

public class JBossStarter extends Task<JBossManagerConfiguration, JBossManager> {

    private static final Logger logger = LoggerFactory.getLogger(JBossStarter.class);

    private JBossManagerConfiguration configuration;

    public JBossStarter() {
        configuration = new JBossManagerConfiguration();
    }

    public JBossStarter configuration(JBossManagerConfiguration configuration) {
        setConfiguration(configuration);
        return this;
    }

    @Override
    protected JBossManager process(JBossManagerConfiguration configuration) throws Exception {

        setConfiguration(configuration);

        JBossManager jbossManager = new JBossManager(this.configuration);

        logger.info("Starting JBoss container located at {}.", this.configuration.getJBossHome());

        jbossManager.start();

        return jbossManager;
    }

    private void setConfiguration(JBossManagerConfiguration configuration) {
        if (configuration != null) {
            this.configuration = configuration;
        }
    }
}
