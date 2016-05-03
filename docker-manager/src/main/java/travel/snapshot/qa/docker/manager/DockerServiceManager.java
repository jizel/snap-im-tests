package travel.snapshot.qa.docker.manager;

import org.arquillian.cube.docker.impl.docker.DockerClientExecutor;
import org.arquillian.cube.spi.Cube;
import org.arquillian.spacelift.task.Task;
import org.jboss.shrinkwrap.impl.base.io.tar.TarArchive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.connection.ConnectionCheck;
import travel.snapshot.qa.manager.api.ServiceManager;
import travel.snapshot.qa.manager.api.configuration.Validate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * This abstract class presents a base Docker service manager from which any particular Docker service manager
 * implementation inherits. This class again wraps core Arquillian Cube lifecycle so any implementation is self-
 * contained when it comes to the managing of the lifecycle of container it runs at.
 */
public abstract class DockerServiceManager<T extends ServiceManager> implements Comparable<DockerServiceManager> {

    private static final Logger logger = LoggerFactory.getLogger(DockerServiceManager.class);

    protected final static int DEFAULT_PRECEDENCE = 0;

    protected final DockerManager dockerManager = DockerManager.instance();

    protected final T serviceManager;

    protected Cube dockerContainer;

    protected String containerId;

    protected boolean started = false;

    protected Task<?, Boolean> checkingTask;

    protected LifecycleHookExecutor<T> lifecycleHookExecutor = new LifecycleHookExecutor<>();

    private int precedence = DEFAULT_PRECEDENCE;

    public DockerServiceManager(final T serviceManager) {
        Validate.notNull(serviceManager, "Service manager can not be a null object.");
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
     * Sets custom checking task to use. When this is not set, you have to provide your checking task explicitly when
     * calling task version of start method. This also provides a way how to specify your custom checking task for a
     * service.
     *
     * @param checkingTask checking task to use
     * @return this
     */
    public <T extends DockerServiceManager<? extends ServiceManager>> T setCheckingTask(final Task<?, Boolean> checkingTask) {
        this.checkingTask = checkingTask;
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
     * @throws IllegalArgumentException if container ID is a null object or an empty String or checking task is a null
     *                                  object
     */
    public Cube start(final Task<?, Boolean> checkingTask, final String containerId, long timeout, long reexecutionInterval) {

        Validate.notNullOrEmpty(containerId, "Container ID to start must not be a null object or an empty String.");
        Validate.notNull(checkingTask, "Checking task must not be a null object.");

        if (started) {
            throw new IllegalStateException(String.format("Unable to start already started container '%s'.", containerId));
        }

        lifecycleHookExecutor.executeBeforeStartHooks(serviceManager);

        dockerContainer = dockerManager.start(containerId);

        new ConnectionCheck.Builder(checkingTask).timeout(timeout).reexecutionInterval(reexecutionInterval).build().execute();

        started = true;

        lifecycleHookExecutor.executeAfterStartHooks(serviceManager);

        return dockerContainer;
    }

    /**
     * Starts container with checking task already set by {@link #setCheckingTask(Task)} method.
     *
     * @param containerId         id of Docker container to start
     * @param timeout             time after service's start is considered unsuccessful when checking task is not done
     * @param reexecutionInterval interval after checking task will execute itself again to check if service is running
     * @throws IllegalStateException    if container to start is already started
     * @throws IllegalArgumentException if container ID is a null object or an empty String
     */
    public Cube start(final String containerId, long timeout, long reexecutionInterval) {
        return start(this.checkingTask, containerId, timeout, reexecutionInterval);
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
        Validate.notNull(container, "Container to stop must not be a null object.");
        stop(container.getId());
    }

    /**
     * Stops a Docker container according to its ID.
     *
     * @param containerId ID of a container to stop
     * @return this manager
     */
    public <T extends DockerServiceManager<? extends ServiceManager>> T stop(final String containerId) {

        Validate.notNullOrEmpty(containerId, "Container ID to stop must not be a null object or an empty String.");

        if (!started) {
            throw new IllegalStateException(String.format("Unable to stop non running container '%s'.", containerId));
        }

        lifecycleHookExecutor.executeBeforeStopHooks(serviceManager);

        dockerManager.stop(containerId);

        started = false;

        lifecycleHookExecutor.executeAfterStopHooks(serviceManager);

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
     * @return lifecycle hook executor for adding hooks before / after containers start / stop
     */
    public LifecycleHookExecutor<T> getLifecycleHookExecutor() {
        return lifecycleHookExecutor;
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
    public abstract String provides();

    /**
     * Transfers resources between container and host. {@code from} file resides effectively in container. {@code from}
     * file can represent directory as well.
     *
     * @param containerId container ID for which file transfer will be carried out
     * @param from        source from where files will be fetched on container side
     * @param to          destination where files will be stored on host side
     */
    public void fetch(final String containerId, final String from, final String to) {
        Validate.notNullOrEmpty(containerId, "Container ID to copy file from can not be a null object or an empty String.");
        Validate.notNullOrEmpty(from, "Location where to copy files from can not be a null object or an empty String.");
        Validate.notNullOrEmpty(to, "Location where to copy files from the container can not be a null object or an empty String.");

        final DockerClientExecutor executor = this.dockerManager.getClientExecutor();

        try (final InputStream inputStream = executor.getFileOrDirectoryFromContainerAsTar(containerId, from)) {
            TarArchive tarArchive = new TarArchive(inputStream);
            tarArchive.extractContents(new File(to));
            tarArchive.closeArchive();
        } catch (IOException ex) {
            throw new RuntimeException(String.format("Unable to copy content from %s to %s for container %s.",
                    from, to, containerId), ex);
        }
    }

    /**
     * Transfers resources between container and host. {@code from} file resides effectively in container. {@code from}
     * file can represent directory as well.
     *
     * When this version of fetch method is used, it is expected that container ID was previously set by {@link
     * #setContainerId(String)} method.
     *
     * @param from source from where files will be fetched on container side
     * @param to   destination where files will be stored on host side
     */
    public void fetch(final String from, final String to) {
        fetch(this.containerId, from, to);
    }

    /**
     * Transfers resources between container and host. {@code from} file resides effectively in container. {@code from}
     * file can represent directory as well.
     *
     * When this version of fetch method is used, it is expected that container ID was previously set by {@link
     * #setContainerId(String)} method.
     *
     * @param from source from where files will be fetched on container side
     * @param to   destination where files will be stored on host side
     */
    public void fetch(final File from, final File to) {
        fetch(this.containerId, from, to);
    }

    /**
     * Transfers resources between container and host. {@code from} file resides effectively in container. {@code from}
     * file can represent directory as well.
     *
     * @param containerId container ID for which file transfer will be carried out
     * @param from        source from where files will be fetched on container side
     * @param to          destination where files will be stored on host side
     */
    public void fetch(final String containerId, final File from, final File to) {
        fetch(containerId, from.getAbsolutePath(), to.getAbsolutePath());
    }

    /**
     * In case of containers which depend on other containers and their services to be already running, precedence has
     * to be set. Lets say that there are two containers, c1 and c2. c2 depends on c1 to be fully started hence we have
     * to set the precedence of c1 to be bigger then the precedence of c2. When these values are same, the order in
     * which these services will be started is random. When this method is not overridden, default precedence is of
     * value 0.
     *
     * @return bigger the number is, sooner underlying service will be started
     */
    public int precedence() {
        return precedence;
    }

    /**
     * @param precedence precedence to set
     * @see #precedence()
     */
    public void setPrecedence(int precedence) {
        this.precedence = precedence;
    }

    @Override
    public int compareTo(DockerServiceManager other) {
        return this.precedence() - other.precedence();
    }

    /**
     * Resolves timeout either set in configuration of a service or set by system property of a given name.
     *
     * Timeout set it measured in seconds.
     *
     * Set system property overrides timeout value set programmatically in service configuration.
     *
     * @param setTimeout      timeout value from service configuration
     * @param timeoutProperty system property to resolve timeout from
     * @param serviceType     type of service a timeout is being resolved for
     * @return resolved timeout
     */
    public static long resolveTimeout(long setTimeout, String timeoutProperty, String serviceType) {

        long connectionTimeOut = setTimeout;

        String resolvedSystemProperty = System.getProperty(timeoutProperty);

        if (resolvedSystemProperty == null) {
            return connectionTimeOut;
        }

        resolvedSystemProperty = resolvedSystemProperty.trim();

        if (resolvedSystemProperty.isEmpty()) {
            return connectionTimeOut;
        }

        try {
            connectionTimeOut = Long.parseLong(resolvedSystemProperty);

            if (connectionTimeOut <= 0) {
                throw new NumberFormatException("Connection timeout was lower than 0.");
            }
        } catch (NumberFormatException ex) {
            logger.info("Connection timeout for {} service was not valid: {}.", serviceType, resolvedSystemProperty);
        }

        logger.info(String.format("Resolved service timeout for %s is %s seconds.", serviceType, connectionTimeOut));

        return connectionTimeOut;
    }
}
