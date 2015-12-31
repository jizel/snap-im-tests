package travel.snapshot.qa.manager.tomcat.check;

import org.arquillian.spacelift.task.Task;
import travel.snapshot.qa.manager.tomcat.api.ContainerManagerException;
import travel.snapshot.qa.manager.tomcat.command.TomcatListCommand;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;
import travel.snapshot.qa.manager.tomcat.impl.TomcatCommandExecutor;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Checks if a container backed by some configuration is started or not by executing of a listing command and getting
 * some output.
 */
public class TomcatStartedCheckTask extends Task<TomcatManagerConfiguration, Boolean> {

    private static final Logger logger = Logger.getLogger(TomcatStartedCheckTask.class.getName());

    @Override
    protected Boolean process(TomcatManagerConfiguration configuration) throws Exception {

        TomcatCommandExecutor listCommandExecutor = new TomcatCommandExecutor(configuration);

        boolean running = false;

        try {
            listCommandExecutor.execute(new TomcatListCommand());

            running = true;
        } catch (ContainerManagerException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof UnknownHostException) {
                logger.info(ex.getCause().getMessage());
            }
        } catch (Exception ex) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(ex.getMessage());
            }
        }

        return running;
    }

}
