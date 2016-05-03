package travel.snapshot.qa.docker.manager;

import org.arquillian.cube.docker.impl.docker.DockerClientExecutor;
import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.config.descriptor.api.ExtensionDef;
import org.jboss.arquillian.core.spi.Manager;
import org.jboss.arquillian.core.spi.Validate;
import travel.snapshot.qa.docker.manager.impl.DockerLocalCubeRegistry;

import java.util.Optional;

/**
 * Performs checks to see if some container should be started on not. When container was not started by us and it is
 * already running, we can just connect to it. The fact if we connect to it in such case depends on {@link
 * ConnectionMode}.
 */
public class DockerContainerLifecycleDecider {

    private DockerLocalCubeRegistry registry;

    private Manager manager;

    public DockerContainerLifecycleDecider init(final DockerLocalCubeRegistry registry, final Manager manager) {

        Validate.notNull(manager, "Manager must not be a null object.");
        Validate.notNull(registry, "Registry must not be a null object.");

        this.registry = registry;
        this.manager = manager;

        return this;
    }

    /**
     * Decides if container should be started or not. Starting logic is same as in Arquillian Cube. We are outside of
     * Arquillian so we have to deal with this on our own.
     *
     * @param containerId ID of container to make a decision for
     * @return true if container with given ID should be started, false otherwise
     */
    public boolean shouldStart(final String containerId) {

        Validate.notNull(manager, "You have not called init method before calling this one.");

        final boolean isAlreadyRunning = isAlreadyRunning(containerId);

        switch (getConnectionMode(manager)) {
            case STARTANDSTOP:
                if (isAlreadyRunning) {
                    throw new IllegalStateException(String.format("Unable to start already started container %s.", containerId));
                }
                return true;
            case STARTORCONNECT:
            case STARTORCONNECTANDLEAVE:
                return !isAlreadyRunning;
            default:
                throw new IllegalStateException("Unable to resolve starting status.");
        }
    }

    /**
     * Decides if container should be stopped or not. Stopping logic is same as in Arquillian Cube. We are outside of
     * Arquillian so we have to deal with this on our own.
     *
     * @param containerId ID of container to make a decision for
     * @return true if container with given ID should be stopped, false otherwise
     */
    public boolean shouldStop(final String containerId) {

        Validate.notNull(manager, "You have not called init method before calling this one.");
        Validate.notNull(registry, "You have not called init method before calling this one.");

        switch (getConnectionMode(manager)) {
            case STARTANDSTOP:
                return true;
            case STARTORCONNECT:
                // if there is such Cube, it means we started it and it was not already running so we will stop it
                // if there is not such Cube, it means it was already started so we will not stop it
                return registry.isRegistered(containerId);
            case STARTORCONNECTANDLEAVE:
                return false;
            default:
                throw new IllegalStateException("Unable to resolve stopping status.");
        }
    }

    /**
     * Checks if there is already running container with given ID.
     *
     * @param containerId ID of container to check
     * @return true if container with given ID is already running, false otherwise
     */
    public boolean isAlreadyRunning(String containerId) {

        return manager.resolve(DockerClientExecutor.class)
                .listRunningContainers()
                .stream()
                .filter(container -> {
                            for (String name : container.getNames()) {
                                if (name.contains(containerId)) {
                                    return true;
                                }
                            }

                            return false;
                        }
                )
                .findFirst()
                .isPresent();
    }

    /**
     * Returns connection mode of Arquillian Cube as specified in docker extension or the default one when not set.
     *
     * Default connection mode is {@link ConnectionMode#STARTANDSTOP}.
     *
     * @param manager manager to resolve connection mode on
     * @return connection mode of Arquillian Cube
     */
    public ConnectionMode getConnectionMode(final Manager manager) {

        Validate.notNull(manager, "Manager must not be null object.");

        final ArquillianDescriptor descriptor = manager.resolve(ArquillianDescriptor.class);

        final Optional<ExtensionDef> dockerExtension = descriptor.getExtensions().stream()
                .filter(extensionDef -> extensionDef.getExtensionName().equals("docker"))
                .findFirst();

        final String arquillianConnectionMode = Optional.ofNullable(dockerExtension.orElseThrow(() ->
                new IllegalStateException("There was not docker extension found in your arquillian.xml file."))
                .getExtensionProperties().get("connectionMode"))
                .orElse(ConnectionMode.STARTANDSTOP.name());

        return ConnectionMode.valueOf(ConnectionMode.class, arquillianConnectionMode);
    }
}
