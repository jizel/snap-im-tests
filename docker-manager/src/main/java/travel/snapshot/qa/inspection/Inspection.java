package travel.snapshot.qa.inspection;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.NetworkSettings;
import com.github.dockerjava.api.model.NetworkSettings.Network;
import org.arquillian.cube.docker.impl.docker.DockerClientExecutor;
import travel.snapshot.qa.docker.orchestration.DataPlatformOrchestration;
import travel.snapshot.qa.manager.api.configuration.Validate;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Inspects given orchestration for various information.
 */
public class Inspection {

    private DataPlatformOrchestration orchestration;

    private DockerClientExecutor executor;

    private String dockerNetwork = DockerNetwork.BRIDGE.toString();

    public Inspection(final DockerClientExecutor executor) {
        Validate.notNull(executor, "Docker client executor to perform the inspection on must not be a null object.");
        this.executor = executor;
    }

    /**
     * @param orchestration orchestration to perform the inspection on
     */
    public Inspection(final DataPlatformOrchestration orchestration) {
        Validate.notNull(orchestration, "Orchestration to perform the inspection on must not be a null object.");
        this.orchestration = orchestration;
        Validate.notNull(orchestration.getDockerManager().getClientExecutor(), "Docker client executor to perform the inspection on must not be a null object.");
        executor = orchestration.getDockerManager().getClientExecutor();
    }

    public Inspection setDockerNetwork(String dockerNetwork) {
        Validate.notNull(dockerNetwork, "Docker network to set for inspection purposes must not be a null object.");
        this.dockerNetwork = dockerNetwork;
        return this;
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

        NetworkSettings networkSettings = Optional.of(inspectContainerResponse.getNetworkSettings())
                .orElseThrow(() -> new IllegalStateException("Unable to get network settings."));

        Map<String, Network> networkMap = Optional.of(networkSettings.getNetworks())
                .orElseThrow(() -> new IllegalStateException("No networks found."));

        Network network = Optional.of(networkMap.get(dockerNetwork))
                .orElseThrow(() -> new IllegalStateException("Unable to get network name " + dockerNetwork));

        return network.getIpAddress();
    }
}
