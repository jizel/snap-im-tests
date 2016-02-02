package travel.snapshot.qa.docker.orchestration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.docker.DockerServiceFactory;
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
import java.util.stream.Collectors;

/**
 * Orchestrates the startup and the shutdown of the whole Snapshot Data Platform infrastructure services.
 *
 * You would have to still deploy service modules to Tomcat via TomcatManager and prepare the database e.g. via flyway.
 * The result of this process is to have all Docker containers fully up and running so you can do so.
 */
public class DataPlatformOrchestration {

    private static final Logger logger = LoggerFactory.getLogger(DataPlatformOrchestration.class);

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

    public DataPlatformOrchestration stopService(final ServiceCubePair startedService) {
        getDockerServiceManager(startedService.getServiceType(), startedService.getCube().getId()).stop();
        return this;
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
        startedServices.forEach(serviceCubePair ->
                getDockerServiceManager(serviceCubePair.getServiceType(), serviceCubePair.getCube().getId())
                        .stop(serviceCubePair.getCube()));

        startedServices.clear();
        return this;
    }

    /**
     * Gets Tomcat Docker manager for container with default Tomcat container ID.
     *
     * @return Docker manager for Tomcat
     */
    public TomcatDockerManager getTomcatDockerManager() {
        return getTomcatDockerManager(DockerServiceFactory.TomcatService.DEFAULT_TOMCAT_CONTAINER_ID);
    }

    /**
     * Gets Tomcat Docker manager for container with specified container ID. This is handy when you started multiple
     * Tomcat containers and you want specific Tomcat manager just for that container.
     *
     * @param containerId ID of container where Tomcat is running
     * @return Docker manager for Tomcat for specified container ID
     */
    public TomcatDockerManager getTomcatDockerManager(String containerId) {
        return (TomcatDockerManager) getDockerServiceManager(ServiceType.TOMCAT, containerId);
    }

    /**
     * Gets Mongo Docker manager for container with default Mongo container ID.
     *
     * @return Docker manager for Mongo
     */
    public MongoDBDockerManager getMongoDockerManager() {
        return getMongoDockerManager(DockerServiceFactory.MongoDBService.DEFAULT_MONGODB_CONTAINER_ID);
    }

    /**
     * Gets Mongo Docker manager for container with specified container ID. This is handy when you started multiple
     * Mongo containers and you want specific Mongo manager just for that container.
     *
     * @param containerId ID of container where Mongo is running
     * @return Docker manager for Mongo for specified container ID
     */
    public MongoDBDockerManager getMongoDockerManager(String containerId) {
        return (MongoDBDockerManager) getDockerServiceManager(ServiceType.MONGODB, containerId);
    }

    /**
     * Gets MariaDB Docker manager for container with default MariaDB container ID.
     *
     * @return Docker manager for MariaDB
     */
    public MariaDBDockerManager getMariaDBDockerManager() {
        return getMariaDBDockerManager(DockerServiceFactory.MariaDBService.DEFAULT_MARIADB_CONTAINER_ID);
    }

    /**
     * Gets MariaDB manager for container with specified container ID. This is handy when you started multiple MariaDB
     * containers and you want specific MariaDB manager just for that container.
     *
     * @param containerId ID of container where MariaDB is running
     * @return Docker manager for MariaDB for specified container ID
     */
    public MariaDBDockerManager getMariaDBDockerManager(String containerId) {
        return (MariaDBDockerManager) getDockerServiceManager(ServiceType.MARIADB, containerId);
    }

    /**
     * Gets ActiveMQ Docker manager for container with default ActiveMQ container ID.
     *
     * @return Docker manager for ActiveMQ
     */
    public ActiveMQDockerManager getActiveMQDockerManager() {
        return getActiveMQDockerManager(DockerServiceFactory.ActiveMQService.DEFAULT_ACTIVEMQ_CONTAINER_ID);
    }

    /**
     * Gets ActiveMQ manager for container with specified container ID. This is handy when you start multiple ActiveMQ
     * containers and you want specific ActiveMQ manager just for that container.
     *
     * @param containerId ID of container where ActiveMQ is running
     * @return Docker manager for ActiveMQ
     */
    public ActiveMQDockerManager getActiveMQDockerManager(String containerId) {
        return (ActiveMQDockerManager) getDockerServiceManager(ServiceType.ACTIVEMQ, containerId);
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
     * @param serviceType type of service to get manager of
     * @param containerId container ID to get service manager of
     * @return Docker service manager of given service type and started container of given {@code containerId}
     */
    public DockerServiceManager<?> getDockerServiceManager(final ServiceType serviceType, final String containerId) {
        return dockerServiceManagers.stream().filter(serviceManager ->
                serviceManager.provides() == serviceType && serviceManager.getDockerContainer().getId().equals(containerId))
                .findFirst().orElse(null);
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
}
