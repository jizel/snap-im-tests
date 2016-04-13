package travel.snapshot.qa.manager.jboss.spacelift;

import org.arquillian.spacelift.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.manager.api.configuration.Validate;
import travel.snapshot.qa.manager.jboss.api.JBossContainerManager;

/**
 * Stops either standalone or domain JBoss instances.
 */
public class JBossStopper extends Task<JBossContainerManager, Void> {

    private static final Logger logger = LoggerFactory.getLogger(JBossStopper.class);

    @Override
    protected Void process(JBossContainerManager manager) throws Exception {

        Validate.notNull(manager, "Container manager you want to stop container with is a null object!");

        logger.info("Stopping JBoss container located at {}.", manager.getConfiguration().getJVM().getJBossHome());

        manager.stop();

        return null;
    }
}
