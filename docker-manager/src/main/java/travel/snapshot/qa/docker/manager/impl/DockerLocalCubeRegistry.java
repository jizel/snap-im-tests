package travel.snapshot.qa.docker.manager.impl;

import org.arquillian.cube.impl.model.LocalCubeRegistry;

public class DockerLocalCubeRegistry extends LocalCubeRegistry {

    public boolean isRegistered(String containerId) {
        return getCube(containerId) != null;
    }
}
