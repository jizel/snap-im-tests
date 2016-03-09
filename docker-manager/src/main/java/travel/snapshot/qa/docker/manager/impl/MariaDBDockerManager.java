package travel.snapshot.qa.docker.manager.impl;

import org.arquillian.cube.spi.Cube;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.task.Task;
import travel.snapshot.qa.docker.ServiceType;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.manager.mariadb.api.MariaDBManager;
import travel.snapshot.qa.manager.mariadb.check.MariaDBStartedCheckTask;
import travel.snapshot.qa.manager.mariadb.configuration.MariaDBManagerConfiguration;

/**
 * Implements MariaDB Docker service with respective startup checking task.
 *
 * MariaDB has precedence 100.
 */
public class MariaDBDockerManager extends DockerServiceManager<MariaDBManager> {

    private static final int PRECENDENCE = 100;

    public MariaDBDockerManager(final MariaDBManager mariaDBManager) {
        super(mariaDBManager);
    }

    @Override
    public Cube start(final String containerId) {
        final Task<MariaDBManagerConfiguration, Boolean> checkingTask = Spacelift.task(serviceManager.getConfiguration(), MariaDBStartedCheckTask.class);
        return super.start(checkingTask, containerId, ConnectionTimeoutResolver.resolveMariaDBConnectionTimeout(serviceManager.getConfiguration()), 5);
    }

    @Override
    public ServiceType provides() {
        return ServiceType.MARIADB;
    }

    @Override
    public int precedence() {
        return PRECENDENCE;
    }
}
