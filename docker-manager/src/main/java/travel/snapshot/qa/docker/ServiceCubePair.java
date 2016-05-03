package travel.snapshot.qa.docker;

import org.arquillian.cube.spi.Cube;
import travel.snapshot.qa.docker.orchestration.Orchestration;

import java.util.List;

/**
 * Helper class which instances are returned in a list to caller after containers are started by {@link
 * Orchestration#startServices()}. That list is in turn passed to {@link Orchestration#stopServices(List)}.
 */
public class ServiceCubePair {

    private final String serviceName;

    private final Cube cube;

    public ServiceCubePair(final String serviceName, final Cube cube) {
        this.serviceName = serviceName;
        this.cube = cube;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Cube getCube() {
        return cube;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceCubePair that = (ServiceCubePair) o;

        return serviceName.equals(that.serviceName) && cube.equals(that.cube);
    }

    @Override
    public int hashCode() {
        int result = serviceName.hashCode();
        result = 31 * result + cube.hashCode();
        return result;
    }
}
