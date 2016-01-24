package travel.snapshot.qa.inspection;

import com.github.dockerjava.api.command.InspectContainerResponse;
import org.arquillian.cube.docker.impl.docker.DockerClientExecutor;
import travel.snapshot.qa.docker.ServiceType;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.docker.orchestration.DataPlatformOrchestration;
import travel.snapshot.qa.manager.tomcat.configuration.Validate;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Inspects given orchestration for various information.
 */
public class Inspection {

    private final DataPlatformOrchestration orchestration;

    private final DockerClientExecutor executor;

    /**
     * @param orchestration orchestration to perform the inspection on
     */
    public Inspection(final DataPlatformOrchestration orchestration) {
        Validate.notNull(orchestration, "Orchestration to perform the inspection on must not be a null object.");
        this.orchestration = orchestration;
        this.executor = orchestration.getDockerManager().getClientExecutor();
    }

    /**
     * Returns external IPs of all running containers in a map. Key is container ID, value is its external IP address.
     *
     * @return map of IPs and their container IDs
     */
    public Map<String, String> inspectAllIPs() {
        return orchestration.getStartedContainers().stream()
                .collect(Collectors.toMap(c -> c.getCube().getId(), c -> inspectIP(c.getCube().getId())));
    }

    /**
     * Gets external IP address of the service of given type.
     *
     * @param serviceType service type to get the external IP address of
     * @return external IP address of the specified service of given type
     * @throws InspectionException in case service manager of given type was not found or it is not possible to get the
     *                             IP address
     */
    public String inspectIP(ServiceType serviceType) {
        final DockerServiceManager serviceManager = orchestration.getDockerServiceManager(serviceType);

        if (serviceManager == null) {
            throw new InspectionException(String.format("Unable to get IP of service type '%s'. Such service is " +
                    "not up and running.", serviceType.toString()));
        }

        return inspectIP(serviceManager.getDockerContainer().getId());
    }

    /**
     * Gets external IP of the specified container by its container ID.
     *
     * @param containerId id of container to get external IP address from
     * @return external IP address of the specified container
     * @throws InspectionException in case no such container with given ID is found or inspection is not successful
     */
    public String inspectIP(String containerId) {
        InspectContainerResponse inspectContainerResponse;

        try {
            inspectContainerResponse = executor.inspectContainer(containerId);
        } catch (Exception ex) {
            throw new InspectionException(String.format("Unable to get IP of the container '%s'", containerId), ex);
        }

        return inspectContainerResponse.getNetworkSettings().getIpAddress();
    }
}
