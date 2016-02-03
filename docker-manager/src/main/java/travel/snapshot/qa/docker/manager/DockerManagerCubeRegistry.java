package travel.snapshot.qa.docker.manager;

import org.arquillian.cube.spi.Cube;
import org.arquillian.cube.spi.CubeRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Added possibility to remove some Cube and ask for its registration status.
 */
public class DockerManagerCubeRegistry implements CubeRegistry {

    private List<Cube> cubes;

    public DockerManagerCubeRegistry() {
        this.cubes = new ArrayList<Cube>();
    }

    @Override
    public void addCube(Cube cube) {
        this.cubes.add(cube);
    }

    @Override
    public Cube getCube(String id) {
        for (Cube cube : this.cubes) {
            if (cube.getId().equals(id)) {
                return cube;
            }
        }
        return null;
    }

    @Override
    public List<Cube> getCubes() {
        return Collections.unmodifiableList(cubes);
    }

    public void removeCube(String containerId) {
        Cube cubeToRemove = getCube(containerId);

        if (cubeToRemove == null) {
            return;
        }

        cubes.remove(cubeToRemove);
    }

    public boolean isRegistered(String containerId) {
        return getCube(containerId) != null;
    }
}
