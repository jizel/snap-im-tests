package travel.snapshot.qa.manager.jboss.spacelift;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.manager.jboss.JBossStandaloneManager;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;

public class JBossStandaloneStarter extends JBossStarter<JBossStandaloneManager> {

    private static final Logger logger = LoggerFactory.getLogger(JBossStandaloneStarter.class);

    @Override
    protected JBossStandaloneManager process(JBossManagerConfiguration configuration) throws Exception {
        setConfiguration(configuration);

        JBossStandaloneManager jbossManager = new JBossStandaloneManager(this.configuration);

        logger.info("Starting JBoss container located at {} in standalone mode.", this.configuration.getJBossHome());

        jbossManager.start();

        return jbossManager;
    }

}
