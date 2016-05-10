package travel.snapshot.qa.manager.mariadb.impl.docker;

import org.arquillian.cube.spi.Cube;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.task.Task;
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

    public static final String SERVICE_NAME = "mariadb";

    private static final String MARIADB_CONNECTION_TIMEOUT_PROPERTY = "docker." + SERVICE_NAME + ".connection.timeout";

    private static final int PRECENDENCE = 100;

    public MariaDBDockerManager(final MariaDBManager mariaDBManager) {
        super(mariaDBManager);
    }

    @Override
    public Cube start(final String containerId) {
        final Task<MariaDBManagerConfiguration, Boolean> checkingTask = Spacelift.task(serviceManager.getConfiguration(), MariaDBStartedCheckTask.class);

        final long timeout = resolveTimeout(serviceManager.getConfiguration().getStartupTimeoutInSeconds(), MARIADB_CONNECTION_TIMEOUT_PROPERTY, provides());

        return super.start(checkingTask, containerId, timeout, 5);
    }

    @Override
    public String provides() {
        return SERVICE_NAME;
    }

    @Override
    public int precedence() {
        return PRECENDENCE;
    }
}
