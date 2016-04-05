package travel.snapshot.qa.manager.jboss.spacelift;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.manager.jboss.JBossDomainManager;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;

public class JBossDomainStarter extends JBossStarter<JBossDomainManager> {

    private static final Logger logger = LoggerFactory.getLogger(JBossDomainStarter.class);

    @Override
    protected JBossDomainManager process(JBossManagerConfiguration configuration) throws Exception {
        setConfiguration(configuration);

        this.configuration.domain();

        JBossDomainManager jbossManager = new JBossDomainManager(this.configuration);

        logger.info("Starting JBoss container located at {} in {} domain mode.", this.configuration.getJBossHome());

        jbossManager.start();

        return jbossManager;
    }
}
