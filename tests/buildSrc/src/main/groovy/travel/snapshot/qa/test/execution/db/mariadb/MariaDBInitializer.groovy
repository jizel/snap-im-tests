package travel.snapshot.qa.test.execution.db.mariadb

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import travel.snapshot.qa.DataPlatformTestOrchestration
import travel.snapshot.qa.docker.DockerServiceFactory
import travel.snapshot.qa.docker.ServiceType
import travel.snapshot.qa.docker.manager.impl.MariaDBDockerManager
import travel.snapshot.qa.manager.mariadb.api.MariaDBManager
import travel.snapshot.qa.test.execution.dataplatform.DataPlatformModule
import travel.snapshot.qa.test.execution.dataplatform.DataPlatformModules
import travel.snapshot.qa.util.Properties

/**
 * Initializes MariaDB container with Flyway scripts.
 */
class MariaDBInitializer {

    private static final Logger logger = LoggerFactory.getLogger(MariaDBInitializer)

    private static final String DEFAULT_MARIADB_CONTAINER =
            DockerServiceFactory.MariaDBService.DEFAULT_MARIADB_CONTAINER_ID

    private final String dataPlatformDir

    private final DataPlatformTestOrchestration orchestration

    private List<DataPlatformModule> modules = []

    private String containerId = DEFAULT_MARIADB_CONTAINER

    MariaDBInitializer(DataPlatformTestOrchestration orchestration) {
        this.dataPlatformDir = Properties.Location.dataPlatformRepository
        this.orchestration = orchestration
    }

    /**
     * Adds a module for the initialization
     *
     * @param module module to initialize
     * @return this
     */
    MariaDBInitializer init(DataPlatformModule module) {
        if (module && module.hasDatabase(ServiceType.MARIADB)) {

            logger.info("Adding {} for MariaDB initialization.", module.toString())

            this.modules << module
        }
        this
    }

    /**
     *
     * @param modules modules to perform database initialization for
     * @return this
     */
    MariaDBInitializer init(DataPlatformModule... modules) {
        if (modules) {
            for (DataPlatformModule module : modules) {
                init(module)
            }
        }
        this
    }

    /**
     * Adds modules for the initialization
     *
     * @param modules modules to perform database initialization for
     * @return this
     */
    MariaDBInitializer init(List<DataPlatformModule> modules) {
        for (DataPlatformModule module : modules) {
            init(module)
        }
        this
    }

    /**
     * Adds modules for initialization
     *
     * @param modules modules to pefrom database initialization for
     * @return this
     */
    MariaDBInitializer init(DataPlatformModules modules) {
        for (DataPlatformModule module : modules.modules()) {
            init(module)
        }

        this
    }

    /**
     * Sets container name against which a database initialization will be carried out. When not set,
     * it defaults to "mariadb".
     *
     * @param containerId container ID to set
     * @return this
     */
    MariaDBInitializer container(String containerId) {
        this.containerId = containerId
        this
    }

    /**
     * Executes the Flyway migration.
     *
     * @return this
     */
    MariaDBInitializer execute() {

        final MariaDBDockerManager dockerManager = orchestration.get().getDockerServiceManager(MariaDBDockerManager, containerId)
        final MariaDBManager manager = dockerManager.getServiceManager()

        if (!dockerManager || !manager) {
            throw new IllegalStateException(
                    String.format("Unable to perform database initialization, managers for container %s are null.", containerId))
        }

        modules.each { module ->
            MariaDBModuleConfiguration configuration = (MariaDBModuleConfiguration) module.getDatabaseConfiguration(ServiceType.MARIADB)

            String dataSource = manager.getConfiguration().getDataSource()
            String scheme = configuration.scheme

            if (configuration.flywayScripts) {
                String scripts = "filesystem:" + new File(dataPlatformDir, configuration.flywayScripts).absolutePath
                manager.flyway(dataSource, scheme, scripts).migrate()
            } else {
                manager.flyway(dataSource, scheme).baseline()
            }
        }

        this
    }
}
