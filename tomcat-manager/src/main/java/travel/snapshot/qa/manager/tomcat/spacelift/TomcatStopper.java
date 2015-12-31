package travel.snapshot.qa.manager.tomcat.spacelift;

import org.arquillian.spacelift.task.Task;
import travel.snapshot.qa.manager.tomcat.TomcatManager;
import travel.snapshot.qa.manager.tomcat.configuration.Validate;

/**
 * Stops a Tomcat container instance. This task returns null after it successfully terminates.
 */
public class TomcatStopper extends Task<TomcatManager, Void> {

    @Override
    protected Void process(TomcatManager manager) throws Exception {

        Validate.notNull(manager, "TomcatManager must not be null object.");

        manager.stop();

        return null;
    }

}
