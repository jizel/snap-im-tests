package travel.snapshot.qa.manager.api.container;

import travel.snapshot.qa.manager.api.ServiceManager;

/**
 * Marker interface which all concrete implementations of application container managers have to implement.
 */
public interface ContainerManager extends ServiceManager {

    /**
     * Starts a container.
     */
    void start() throws ContainerManagerException;

    /**
     * Stops a container.
     */
    void stop() throws ContainerManagerException;

    /**
     * Checks if container is running.
     *
     * @return true if container is running, false otherwise.
     */
    boolean isRunning() throws ContainerManagerException;
}
