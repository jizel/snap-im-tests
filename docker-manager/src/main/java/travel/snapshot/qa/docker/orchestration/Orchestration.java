package travel.snapshot.qa.docker.orchestration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.docker.ServiceCubePair;
import travel.snapshot.qa.docker.manager.DockerManager;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.inspection.Inspection;
import travel.snapshot.qa.inspection.InspectionException;
import travel.snapshot.qa.manager.api.ServiceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Orchestrates the startup and the shutdown of the whole infrastructure services.
 */
public final class Orchestration {

    private static final Logger logger = LoggerFactory.getLogger(Orchestration.class);

    private final DockerManager dockerManager = DockerManager.instance();

    private final List<DockerServiceManager<? extends ServiceManager>> dockerServiceManagers = new ArrayList<>();

    private final List<ServiceCubePair> startedContainers = new ArrayList<>();

    /**
     * Starts underlying {@link DockerManager}.
     *
     * @return this
     */
    public Orchestration start() {
        dockerManager.startManager();
        logger.info("Orchestration has started.");
        return this;
    }

    /**
     * Stops underlying {@link DockerManager}.
     *
     * @return this
     */
    public Orchestration stop() {
        dockerManager.stopManager();
        logger.info("Orchestration has stopped.");
        return this;
    }

    /**
     * Starts Docker services.
     *
     * @return list of started Arquillian Cubes with their service types.
     */
    public List<ServiceCubePair> startServices() {

        Collections.sort(dockerServiceManagers, Comparator.reverseOrder());

        dockerServiceManagers.stream().forEach(serviceManager -> {
            if (!serviceManager.serviceRunning()) {
                startService(serviceManager);
            } else {
                logger.info("Service {} is already running.", serviceManager.provides());
            }
        });

        return startedContainers;
    }

    /**
     * Starts Docker service of provided manager.
     *
     * @param serviceManager service to start
     * @return Arquillian Cube with its service type
     */
    public ServiceCubePair startService(final DockerServiceManager<?> serviceManager) {
        ServiceCubePair serviceCubePair = new ServiceCubePair(serviceManager.provides(), serviceManager.start());
        startedContainers.add(serviceCubePair);
        return serviceCubePair;
    }

    /**
     * Stops a started service
     *
     * @param startedService service to stop
     * @return this
     */
    public Orchestration stopService(final ServiceCubePair startedService) {
        getDockerServiceManager(startedService.getServiceName(), startedService.getCube().getId()).stop();
        deleteServices(Arrays.asList(startedService));
        return this;
    }

    /**
     * Stops all started services.
     *
     * @return this
     */
    public Orchestration stopServices() {
        stopServices(startedContainers);
        return this;
    }

    /**
     * Stops given services.
     *
     * @param startedServices list of {@link ServiceCubePair}s to stop
     * @return this
     */
    public Orchestration stopServices(final List<ServiceCubePair> startedServices) {

        List<ServiceCubePair> stoppedServices = new ArrayList<>();

        for (ServiceCubePair serviceCubePair : startedServices) {
            try {
                getDockerServiceManager(serviceCubePair.getServiceName(), serviceCubePair.getCube().getId()).stop();
            } catch (Exception ex) {
                logger.info("Unable to stop a service: {}", ex.getMessage());
            }
            stoppedServices.add(serviceCubePair);
        }

        deleteServices(stoppedServices);

        return this;
    }

    /**
     * Adds Docker service managers sto this orchestration.
     *
     * @param dockerServiceManagers Docker service manages to add to this orchestration
     * @return this
     */
    @SafeVarargs
    public final Orchestration with(final DockerServiceManager<? extends ServiceManager>... dockerServiceManagers) {
        for (final DockerServiceManager<? extends ServiceManager> dockerServiceManager : dockerServiceManagers) {
            with(dockerServiceManager);
        }
        return this;
    }

    /**
     * Adds Docker service manager to this orchestration.
     *
     * @param dockerServiceManager Docker service manager to add to this orchestration.
     * @return this
     */
    public Orchestration with(final DockerServiceManager<? extends ServiceManager> dockerServiceManager) {
        this.dockerServiceManagers.add(dockerServiceManager);
        return this;
    }

    public DockerManager getDockerManager() {
        return dockerManager;
    }

    /**
     * Returns unmodifiable view of started containers.
     *
     * @return unmodifiable list of started containers
     */
    public List<ServiceCubePair> getStartedContainers() {
        return Collections.unmodifiableList(startedContainers);
    }

    /**
     * Returns Docker service manager for given {@code serviceType} and {@code containerId} or null if there is not such
     * service manager
     *
     * @param serviceName name of service to get manager of
     * @param containerId container ID to get service manager of
     * @return Docker service manager of given service type and started container of given {@code containerId}
     */
    public DockerServiceManager<?> getDockerServiceManager(final String serviceName, final String containerId) {
        return dockerServiceManagers.stream().filter(serviceManager ->
                serviceManager.provides().equals(serviceName) && serviceManager.getDockerContainer().getId().equals(containerId))
                .findFirst().orElse(null);
    }

    public <T extends DockerServiceManager<? extends ServiceManager>> T getDockerServiceManager(final Class<T> clazz, final String containerId) {
        return (T) dockerServiceManagers.stream()
                .filter(serviceManager -> serviceManager.getClass() == clazz && serviceManager.getDockerContainer().getId().equals(containerId))
                .findFirst().get();
    }

    /**
     * Gets the map of all running services. Keys are container IDs, values are their IPs.
     *
     * @return external IP address of all running services
     */
    public Map<String, String> inspectAllIPs() {
        return new Inspection(this).inspectAllIPs();
    }

    /**
     * Gets the external IP address of the specified {@code containerId}.
     *
     * @param containerId container ID to the the external IP address of
     * @return external IP address of the container with specified {@code containerId}
     * @throws InspectionException in case such container of given id is not found
     */
    public String inspectIP(final String containerId) throws InspectionException {
        return new Inspection(this).inspectIP(containerId);
    }

    private void deleteServices(List<ServiceCubePair> servicesToDelete) {
        startedContainers.removeAll(servicesToDelete);
    }
}
