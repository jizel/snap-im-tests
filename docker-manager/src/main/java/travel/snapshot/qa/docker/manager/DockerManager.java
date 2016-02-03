package travel.snapshot.qa.docker.manager;

import org.arquillian.cube.docker.impl.docker.DockerClientExecutor;
import org.arquillian.cube.spi.Cube;
import org.arquillian.cube.spi.CubeRegistry;
import org.arquillian.cube.spi.event.CreateCube;
import org.arquillian.cube.spi.event.DestroyCube;
import org.arquillian.cube.spi.event.StartCube;
import org.arquillian.cube.spi.event.StopCube;
import org.jboss.arquillian.core.impl.loadable.LoadableExtensionLoader;
import org.jboss.arquillian.core.spi.Manager;
import org.jboss.arquillian.core.spi.ManagerBuilder;
import org.jboss.arquillian.core.spi.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wraps Arquillian Cube core services into a separate helper class. We do not depend on Arquillian runner - its core
 * lifecycle is extracted to this class hence it can be easily encapsulated into your tests.
 *
 * Underlying Arquillian Core Manager for eventing and dependency injection is started automatically in the constructor
 * of DockerManager.
 *
 * You have also access to the raw Arquillian Docker client executor which enables you do perform low-level actions
 * calling {@link #getClientExecutor()}. calling {@link #getClientExecutor()}
 *
 * @see Manager
 * @see DockerClientExecutor
 */
public final class DockerManager {

    private static final Logger logger = LoggerFactory.getLogger(DockerManager.class);

    private static final String INVALID_CONTAINER_ID_MESSAGE = "Container ID can not be a null object nor an empty String!";

    private Manager manager;

    private static boolean isManagerStarted;

    /**
     * Returned DockerManager object has already Arquillian Core Manager started.
     *
     * @return instance of DockerManger class
     */
    public static DockerManager instance() {
        return DockerManagerHolder.instance;
    }

    private static final class DockerManagerHolder {
        public static final DockerManager instance = new DockerManager();
    }

    private DockerManager() {
    }

    /**
     * Starts container with specified ID.
     *
     * @param containerId id of container to start
     * @return Arquillian Cube abstraction of a started container
     * @throws IllegalArgumentException if {@code containerId} is a null object or an empty String.
     */
    public Cube start(final String containerId) {
        Validate.notNullOrEmpty(containerId, INVALID_CONTAINER_ID_MESSAGE);

        startManager();

        logger.info("Starting container {}.", containerId);

        manager.fire(new CreateCube(containerId));
        manager.fire(new StartCube(containerId));

        logger.info("Started container {}.", containerId);

        return getContainer(containerId);
    }

    /**
     * Stops container with specified ID.
     *
     * @param containerId id of container to stop
     * @throws IllegalArgumentException if {@code containerId} is a null object or an empty String
     * @throws IllegalStateException    if manager is not started prior to this method call.
     */
    public void stop(final String containerId) {
        Validate.notNullOrEmpty(containerId, INVALID_CONTAINER_ID_MESSAGE);

        if (!isManagerStarted) {
            throw new IllegalStateException("Manager is not started!");
        }

        logger.info("Stopping container {}.", containerId);

        manager.fire(new StopCube(containerId));
        manager.fire(new DestroyCube(containerId));

        logger.info("Stopped container {}.", containerId);
    }

    /**
     * Stops Arqullian Cube container.
     *
     * @param container container to stop
     */
    public void stop(final Cube container) {
        stop(container.getId());
    }

    /**
     * Gets Arquillian Cube container by specified {@code containerId}.
     *
     * @param containerId id of a container to get.
     * @return Arquillian Cube container with specified containerId or null if such container is not found.
     */
    public Cube getContainer(final String containerId) {
        Validate.notNullOrEmpty(containerId, INVALID_CONTAINER_ID_MESSAGE);
        return manager.resolve(CubeRegistry.class).getCube(containerId);
    }

    /**
     * Returns Docker client executor which enables you to do low-level interactions with Arquillian Cube.
     *
     * @return Docker client executor instance
     * @throws IllegalStateException if Arquillian Core Manager was not started
     */
    public DockerClientExecutor getClientExecutor() {
        if (!isManagerStarted) {
            throw new IllegalStateException("Arquillian Core Manager is not started.");
        }

        return manager.resolve(DockerClientExecutor.class);
    }

    /**
     * Starts Arquillian Core Manager.
     *
     * This method is called automatically after you call {@link #start(String)} hence you do not need to call it
     * explicitly.
     */
    public void startManager() {
        if (!isManagerStarted) {
            manager = ManagerBuilder.from().extension(LoadableExtensionLoader.class).create();
            manager.start();
            isManagerStarted = true;
            logger.info("Arquillian Core Manager has started.");
        }
    }

    /**
     * Stops Arquillian Core Manager.
     *
     * This method should be called when you finish the interaction with the Arquillian Core and Cube. This method will
     * not be called explicitly anywhere and it is the responsibility of the user of this class to stop it.
     */
    public void stopManager() {
        if (isManagerStarted) {
            manager.shutdown();
            manager = null;
            isManagerStarted = false;
            logger.info("Arquillian Core Manager has stopped.");
        }
    }
}
