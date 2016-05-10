package travel.snapshot.qa.manager.generic.impl.docker;

import org.arquillian.cube.spi.Cube;
import org.arquillian.spacelift.task.Task;
import travel.snapshot.qa.connection.ConnectionCheck;
import travel.snapshot.qa.connection.ConnectionCheckExecutor;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.manager.generic.api.GenericManager;
import travel.snapshot.qa.manager.generic.configuration.GenericConfiguration;

/**
 * Docker manager without any underlying service where user is not able to interact with it by some API calls.
 */
public class GenericDockerManager extends DockerServiceManager<GenericManager> {

    public static final String SERVICE_NAME = "generic";

    private static final String GENERIC_CONNECTION_TIMEOUT_PROPERTY = "docker." + SERVICE_NAME + ".connection.timeout";

    public GenericDockerManager(GenericManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public Cube start(String containerId) {

        if (checkingTask == null) {
            checkingTask = createCheckingTask(serviceManager.getConfiguration());
        }

        final long timeout = resolveTimeout(serviceManager.getConfiguration().getStartupTimeoutInSeconds(), GENERIC_CONNECTION_TIMEOUT_PROPERTY, provides());

        return super.start(checkingTask, containerId, timeout, 10);
    }

    @Override
    public String provides() {
        return SERVICE_NAME;
    }

    /**
     * Builds specific connection check task from given configuration.
     *
     * @param configuration configuration to build a respective connection check task from
     * @return built connection check task according to {@code configuration}
     */
    private Task<ConnectionCheck, Boolean> createCheckingTask(final GenericConfiguration configuration) {

        final ConnectionCheck connectionCheck = new ConnectionCheck.Builder()
                .host(configuration.getBindAddress())
                .port(configuration.getBindPort())
                .protocol(configuration.getProtocol())
                .build();

        final Task<ConnectionCheck, Boolean> connectionCheckTask;

        switch (configuration.getProtocol()) {
            case TCP:
                connectionCheckTask = new ConnectionCheckExecutor.TCPConnectionCheckTask().connectionCheck(connectionCheck);
                break;
            case UDP:
                connectionCheckTask = new ConnectionCheckExecutor.UDPConnectionCheckTask().connectionCheck(connectionCheck);
                break;
            default:
                throw new IllegalStateException(String.format("Unable to build connection task for protocol \"%s\"", configuration.getProtocol()));
        }

        return connectionCheckTask;
    }
}
