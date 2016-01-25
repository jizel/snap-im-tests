package travel.snapshot.qa.docker.orchestration;

import travel.snapshot.qa.docker.ServiceCubePair;
import travel.snapshot.qa.docker.ServiceType;
import travel.snapshot.qa.docker.manager.DockerManager;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.docker.manager.impl.ActiveMQDockerManager;
import travel.snapshot.qa.docker.manager.impl.MariaDBDockerManager;
import travel.snapshot.qa.docker.manager.impl.MongoDBDockerManager;
import travel.snapshot.qa.docker.manager.impl.TomcatDockerManager;
import travel.snapshot.qa.inspection.Inspection;
import travel.snapshot.qa.inspection.InspectionException;
import travel.snapshot.qa.manager.api.ServiceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Orchestrates the startup and the shutdown of the whole Snapshot Data Platform infrastructure services.
 *
 * You would have to still deploy service modules to Tomcat via TomcatManager and prepare the database e.g. via flyway.
 * The result of this process is to have all Docker containers fully up and running so you can do so.
 */
public class DataPlatformOrchestration {

    private static final Logger logger = Logger.getLogger(DataPlatformOrchestration.class.getName());

    private final DockerManager dockerManager = DockerManager.instance();

    private final List<DockerServiceManager<? extends ServiceManager>> dockerServiceManagers = new ArrayList<>();

    private final List<ServiceCubePair> startedContainers = new ArrayList<>();

    /**
     * Underlying {@link DockerManager} instance is started after the constructor is done.
     */
    public DataPlatformOrchestration() {
        start();
    }

    /**
     * Starts underlying {@link DockerManager}.
     *
     * @return this
     */
    public DataPlatformOrchestration start() {
        dockerManager.startManager();
        logger.info("Data platform orchestration has started.");
        return this;
    }

    /**
     * Stops underlying {@link DockerManager}.
     *
     * @return this
     */
    public DataPlatformOrchestration stop() {
        dockerManager.stopManager();
        logger.info("Data platform orchestration has stopped.");
        return this;
    }

    /**
     * Starts Docker services.
     *
     * @return list of started Arquillian Cubes with their service types.
     */
    public List<ServiceCubePair> startServices() {

        Collections.sort(dockerServiceManagers, Comparator.reverseOrder());

        final List<ServiceCubePair> started = dockerServiceManagers.stream()
                .map(this::startService)
                .collect(Collectors.toList());

        startedContainers.addAll(started);
        return startedContainers;
    }

    /**
     * Starts Docker service of provided manager.
     *
     * @param serviceManager service to start
     * @return Arquillian Cube with its service type
     */
    public ServiceCubePair startService(final DockerServiceManager<?> serviceManager) {
        return new ServiceCubePair(serviceManager.provides(), serviceManager.start());
    }

    /**
     * Stops all started services.
     *
     * @return this
     */
    public DataPlatformOrchestration stopServices() {
        stopServices(startedContainers);
        return this;
    }

    /**
     * Stops given services.
     *
     * @param startedServices list of {@link ServiceCubePair}s to stop
     * @return this
     */
    public DataPlatformOrchestration stopServices(final List<ServiceCubePair> startedServices) {
        startedServices.forEach(serviceCubePair -> getDockerServiceManager(serviceCubePair.getServiceType())
                .stop(serviceCubePair.getCube()));

        startedServices.clear();
        return this;
    }

    /**
     * @return Docker manager for Tomcat
     */
    public TomcatDockerManager getTomcatDockerManager() {
        return (TomcatDockerManager) getDockerServiceManager(ServiceType.TOMCAT);
    }

    /**
     * @return Docker manager for Mongo
     */
    public MongoDBDockerManager getMongoDockerManager() {
        return (MongoDBDockerManager) getDockerServiceManager(ServiceType.MONGODB);
    }

    /**
     * @return Docker manager for MariaDB
     */
    public MariaDBDockerManager getMariaDBDockerManager() {
        return (MariaDBDockerManager) getDockerServiceManager(ServiceType.MARIADB);
    }

    public ActiveMQDockerManager getActiveMQDockerManager() {
        return (ActiveMQDockerManager) getDockerServiceManager(ServiceType.ACTIVEMQ);
    }

    /**
     * Adds Docker service managers sto this orchestration.
     *
     * @param dockerServiceManagers Docker service manages to add to this orchestration
     * @return this
     */
    @SafeVarargs
    public final DataPlatformOrchestration with(final DockerServiceManager<? extends ServiceManager>... dockerServiceManagers) {
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
    public DataPlatformOrchestration with(final DockerServiceManager<? extends ServiceManager> dockerServiceManager) {
        this.dockerServiceManagers.add(dockerServiceManager);
        return this;
    }

    public DockerManager getDockerManager() {
        return dockerManager;
    }

    /**
     * Returns unmodifiable view to started containers.
     *
     * @return unmodifiable list of started containers
     */
    public List<ServiceCubePair> getStartedContainers() {
        return Collections.unmodifiableList(startedContainers);
    }

    /**
     * Returns Docker service manager for given {@code serviceType} or null if there is not such service of given type.
     *
     * @param serviceType service to get the service manager of
     * @return Docker service manager of given service type
     */
    public DockerServiceManager<?> getDockerServiceManager(final ServiceType serviceType) {
        return dockerServiceManagers.stream().filter(serviceManager -> serviceManager.provides() == serviceType)
                .findFirst().orElse(null);
    }

    /**
     * @return external IP address of all running services
     */
    public Map<String, String> inspectAllIPs() {
        return new Inspection(this).inspectAllIPs();
    }

    /**
     * Gets the external IP address of the specified {@code serviceType}.
     *
     * @param serviceType type of service to get the external IP address from
     * @return external IP address of specified {@code serviceType}
     * @throws InstantiationException in case there is not such service to get the IP address of
     */
    public String inspectIP(final ServiceType serviceType) throws InspectionException {
        return new Inspection(this).inspectIP(serviceType);
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
}
