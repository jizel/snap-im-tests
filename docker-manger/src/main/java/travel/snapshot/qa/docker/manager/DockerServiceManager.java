package travel.snapshot.qa.docker.manager;

import org.arquillian.cube.spi.Cube;
import org.arquillian.spacelift.task.Task;
import travel.snapshot.qa.connection.ConnectionCheck;
import travel.snapshot.qa.docker.ServiceType;
import travel.snapshot.qa.manager.api.ServiceManager;
import travel.snapshot.qa.manager.tomcat.configuration.Validate;

/**
 * This abstract class presents a base Docker service manager from which any particular Docker service manager
 * implementation inherits. This class again wraps core Arquillian Cube lifecycle so any implementation is self-
 * contained when it comes to the managing of the lifecycle of container it runs at.
 */
public abstract class DockerServiceManager<T extends ServiceManager> implements Comparable<DockerServiceManager> {

    protected final static int DEFAULT_PRECEDENCE = 0;

    protected final DockerManager dockerManager = DockerManager.instance();

    protected final T serviceManager;

    protected Cube dockerContainer;

    protected String containerId;

    protected boolean started = false;

    public DockerServiceManager(final T serviceManager) {
        Validate.notNull(serviceManager, "Service manager can not be a null object!");
        this.serviceManager = serviceManager;
    }

    /**
     * Sets container ID which will be started when parameterless {@code start} method is called.
     *
     * @param containerId ID of container to start
     * @param <T>         type of concrete Docker service manager implementation
     * @return Docker service manager implementation
     */
    public <T extends DockerServiceManager<? extends ServiceManager>> T setContainerId(final String containerId) {
        this.containerId = containerId;
        return (T) this;
    }

    /**
     * Starts Docker container of specified container ID.
     *
     * @param containerId id of Docker container to start
     * @return started Docker container as Arquillian Cube
     */
    public abstract Cube start(final String containerId);

    /**
     * Starts container with container ID previously set with {@link DockerServiceManager#setContainerId(String)}.
     *
     * @return started Docker container as Arquillian Cube
     */
    public Cube start() {
        return start(this.containerId);
    }

    /**
     * Starts Docker container of specified ID. Start of a Docker container is considered successful once {@code
     * checkingTask} returns and does not throw any exception.
     *
     * @param checkingTask        task which checks if underlying service is fully up and running after its underlying
     *                            container is started
     * @param containerId         id of Docker container to start
     * @param timeout             time after service's start is considered unsuccessful when checking task is not done
     * @param reexecutionInterval interval after checking task will execute itself again to check if service is running
     * @return started Docker container as Arquillian Cube
     * @throws IllegalStateException    if container to start is already started
     * @throws IllegalArgumentException if container ID is a null object or an empty String
     */
    public Cube start(final Task<?, Boolean> checkingTask, final String containerId, long timeout, long reexecutionInterval) {

        Validate.notNullOrEmpty(containerId, "Container ID to start must not be a null object or an empty String!");

        if (started) {
            throw new IllegalStateException(String.format("Unable to start already started container '%s'", containerId));
        }

        dockerContainer = dockerManager.start(containerId);

        new ConnectionCheck.Builder(checkingTask).timeout(timeout).reexecutionInterval(reexecutionInterval).build().execute();

        started = true;

        return dockerContainer;
    }

    /**
     * Stops underlying Docker container. This call fails if there is not any container started yet.
     */
    public void stop() {
        stop(dockerContainer);
    }

    /**
     * Stops given Docker container
     *
     * @param container container to stop
     */
    public void stop(final Cube container) {
        Validate.notNull(container, "Container to stop must not be a null object!");
        stop(container.getId());
    }

    /**
     * Stops a Docker container according to its ID.
     *
     * @param containerId ID of a container to stop
     * @return this manager
     */
    public <T extends DockerServiceManager<? extends ServiceManager>> T stop(final String containerId) {

        Validate.notNullOrEmpty(containerId, "Container ID to stop must not be a null object or an empty String!");

        if (!started) {
            throw new IllegalStateException(String.format("Unable to stop non running container '%s'", containerId));
        }

        dockerManager.stop(containerId);

        started = false;

        return (T) this;
    }

    /**
     * @return Docker manager object as a singleton
     */
    public DockerManager getDockerManager() {
        return dockerManager;
    }

    /**
     * @return container abstraction from the Arquillian Cube point of view
     */
    public Cube getDockerContainer() {
        return dockerContainer;
    }

    /**
     * @return Service manager which presents interface to service running inside Docker container / Arquillian Cube
     */
    public T getServiceManager() {
        return serviceManager;
    }

    /**
     * @return true if underlying service is considered to be started, false otherwise
     */
    public boolean serviceRunning() {
        return started;
    }

    /**
     * @return type of service this Docker service manager handles
     */
    public abstract ServiceType provides();

    /**
     * In case of containers which depend on other containers and their services to be already running, precedence has
     * to be set. Lets say that there are two containers, c1 and c2. c2 depends on c1 to be fully started hence we have
     * to set the precedence of c1 to be bigger then the precedence of c2. When these values are same, the order in
     * which these services will be started is random. When this method is not overriden, default precedence is of value
     * 0.
     *
     * @return bigger the number is, sooner underlying service will be started
     */
    public int precedence() {
        return DEFAULT_PRECEDENCE;
    }

    @Override
    public int compareTo(DockerServiceManager other) {
        return this.precedence() - other.precedence();
    }
}
