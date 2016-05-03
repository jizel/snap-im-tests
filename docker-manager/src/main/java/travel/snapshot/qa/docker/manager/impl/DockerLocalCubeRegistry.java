package travel.snapshot.qa.docker.manager.impl;

import org.arquillian.cube.impl.model.LocalCubeRegistry;

public class DockerLocalCubeRegistry extends LocalCubeRegistry {

    /**
     * Checks if a container with {@code containerId} is already present in a registry.
     *
     * @param containerId container ID to ask for registration
     * @return true if container with given {@code containerId} is already registered, false otherwise
     */
    public boolean isRegistered(String containerId) {
        return getCube(containerId) != null;
    }
}
