package travel.snapshot.qa.docker.manager.impl;

import org.arquillian.cube.spi.Cube;
import org.arquillian.spacelift.task.Task;
import travel.snapshot.qa.connection.ConnectionCheck;
import travel.snapshot.qa.connection.ConnectionCheckExecutor;
import travel.snapshot.qa.docker.ServiceType;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.manager.generic.api.GenericManager;
import travel.snapshot.qa.manager.generic.configuration.GenericConfiguration;

/**
 * Docker manager without any underlying service where user is not able to interact with it by some API calls.
 */
public class GenericDockerManager extends DockerServiceManager<GenericManager> {

    public GenericDockerManager(GenericManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public Cube start(String containerId) {

        if (checkingTask == null) {
            checkingTask = createCheckingTask(serviceManager.getConfiguration());
        }

        return super.start(checkingTask, containerId, ConnectionTimeoutResolver.resolveGenericConnectionTimeout(serviceManager.getConfiguration()), 10);
    }

    @Override
    public ServiceType provides() {
        return ServiceType.GENERIC;
    }

    private Task<ConnectionCheck, Boolean> createCheckingTask(GenericConfiguration configuration) {

        final ConnectionCheck connectionCheck = new ConnectionCheck.Builder()
                .host(configuration.getBindAddress())
                .port(configuration.getBindPort())
                .protocol(configuration.getProtocol())
                .build();

        Task<ConnectionCheck, Boolean> connectionCheckTask;

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
