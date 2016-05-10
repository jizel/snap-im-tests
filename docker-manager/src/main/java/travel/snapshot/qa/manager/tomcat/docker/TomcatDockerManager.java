package travel.snapshot.qa.manager.tomcat.docker;

import org.arquillian.cube.spi.Cube;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.task.Task;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.manager.tomcat.TomcatManager;
import travel.snapshot.qa.manager.tomcat.check.TomcatStartedCheckTask;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;

/**
 * Implements Tomcat Docker service with respective startup checking task.
 *
 * Tomcat has default precedence.
 */
public class TomcatDockerManager extends DockerServiceManager<TomcatManager> {

    public static final String SERVICE_NAME = "tomcat";

    private static final String TOMCAT_CONNECTION_TIMEOUT_PROPERTY = "docker.tomcat.connection.timeout";

    public TomcatDockerManager(final TomcatManager tomcatManager) {
        super(tomcatManager);
    }

    @Override
    public Cube start(final String containerId) {

        final long timeout = resolveTimeout(serviceManager.getConfiguration().getStartupTimeoutInSeconds(), TOMCAT_CONNECTION_TIMEOUT_PROPERTY, provides());

        if (super.checkingTask == null) {
            final Task<TomcatManagerConfiguration, Boolean> checkingTask = Spacelift.task(serviceManager.getConfiguration(), TomcatStartedCheckTask.class);
            return super.start(checkingTask, containerId, timeout, 5);
        }

        return super.start(containerId, timeout, 5);
    }

    @Override
    public String provides() {
        return SERVICE_NAME;
    }
}
