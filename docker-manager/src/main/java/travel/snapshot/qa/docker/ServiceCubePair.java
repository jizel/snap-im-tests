package travel.snapshot.qa.docker;

import org.arquillian.cube.spi.Cube;
import travel.snapshot.qa.docker.orchestration.DataPlatformOrchestration;

import java.util.List;

/**
 * Helper class which instances are returned in a list to caller after containers are started by {@link
 * DataPlatformOrchestration#startServices()}. That list is in turn passed to {@link
 * DataPlatformOrchestration#stopServices(List)}.
 */
public class ServiceCubePair {

    private final ServiceType serviceType;

    private final Cube cube;

    public ServiceCubePair(final ServiceType serviceType, final Cube cube) {
        this.serviceType = serviceType;
        this.cube = cube;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public Cube getCube() {
        return cube;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceCubePair that = (ServiceCubePair) o;

        return serviceType == that.serviceType && cube.equals(that.cube);
    }

    @Override
    public int hashCode() {
        int result = serviceType.hashCode();
        result = 31 * result + cube.hashCode();
        return result;
    }
}
