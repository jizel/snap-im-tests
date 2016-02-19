package travel.snapshot.qa.docker.manager.impl;

import org.arquillian.cube.spi.Cube;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.task.Task;
import travel.snapshot.qa.docker.ServiceType;
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

    public TomcatDockerManager(final TomcatManager tomcatManager) {
        super(tomcatManager);
    }

    @Override
    public Cube start(final String containerId) {

        if (super.checkingTask == null) {
            final Task<TomcatManagerConfiguration, Boolean> checkingTask = Spacelift.task(serviceManager.getConfiguration(), TomcatStartedCheckTask.class);
            return super.start(checkingTask, containerId, serviceManager.getConfiguration().getStartupTimeoutInSeconds(), 5);
        }

        return super.start(containerId, serviceManager.getConfiguration().getStartupTimeoutInSeconds(), 5);
    }

    @Override
    public ServiceType provides() {
        return ServiceType.TOMCAT;
    }
}
