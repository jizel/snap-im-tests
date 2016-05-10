package travel.snapshot.qa.test.execution.db.mariadb

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import travel.snapshot.qa.DataPlatformTestOrchestration
import travel.snapshot.qa.ServiceType
import travel.snapshot.qa.manager.mariadb.api.MariaDBManager
import travel.snapshot.qa.manager.mariadb.impl.docker.MariaDBDockerManager
import travel.snapshot.qa.manager.mariadb.impl.docker.MariaDBService
import travel.snapshot.qa.test.execution.dataplatform.DataPlatformModule
import travel.snapshot.qa.test.execution.dataplatform.DataPlatformModules

import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement

/**
 * Drops a database for some module from the MariaDB instance.
 */
class MariaDBDropper {

    private static final Logger logger = LoggerFactory.getLogger(MariaDBDropper)

    private static
    final String DEFAULT_MARIADB_CONTAINER = MariaDBService.DEFAULT_MARIADB_CONTAINER_ID

    private final DataPlatformTestOrchestration orchestration

    private List<DataPlatformModule> modules = []

    private String containerId = DEFAULT_MARIADB_CONTAINER

    MariaDBDropper(DataPlatformTestOrchestration orchestration) {
        this.orchestration = orchestration
    }

    /**
     * Adds a module for which the database will be dropped.
     *
     * @param module module to drop a database for
     * @return this
     */
    MariaDBDropper drop(DataPlatformModule module) {
        if (module && module.hasDatabase(ServiceType.MARIADB)) {

            logger.info("Adding {} for MariaDB database dropping.", module.toString())

            this.modules << module
        }
        this
    }

    /**
     * Adds modules for which the database will be dropped.
     *
     * @param modules modules to drop a database for
     * @return this
     */
    MariaDBDropper drop(DataPlatformModule... modules) {
        if (modules) {
            for (DataPlatformModule module : modules) {
                drop(module)
            }
        }
        this
    }

    /**
     * Adds modules for which the database will be dropped.
     *
     * @param modules modules to drop a database for
     * @return this
     */
    MariaDBDropper drop(List<DataPlatformModule> modules) {
        for (DataPlatformModule module : modules) {
            drop(module)
        }
        this
    }

    /**
     * Adds modules for which the database will be dropped.
     *
     * @param modules modules to drop a database for
     * @return this
     */
    MariaDBDropper drop(DataPlatformModules modules) {
        drop(modules.modules())
        this
    }

    /**
     * Sets container name against which a database dropping will be carried out. When not set,
     * it defaults to "mariadb".
     *
     * @param containerId container ID to set
     * @return this
     */
    MariaDBDropper container(String containerId) {
        this.containerId = containerId
        this
    }

    /**
     * Executes dropping of the databases.
     *
     * @return this
     */
    MariaDBDropper execute() {

        final MariaDBDockerManager dockerManager = orchestration.get().getDockerServiceManager(MariaDBDockerManager, containerId)
        final MariaDBManager manager = dockerManager.getServiceManager()

        if (!dockerManager || !manager) {
            throw new IllegalStateException(
                    String.format("Unable to perform database dropping, managers for container %s are null.", containerId))
        }

        modules.each { module ->

            MariaDBModuleConfiguration configuration = (MariaDBModuleConfiguration) module.getDatabaseConfiguration(ServiceType.MARIADB)

            Connection connection
            Statement statement

            try {
                connection = manager.getConnection("") // database is empty, we need just connection to data source
                statement = connection.createStatement()

                statement.executeUpdate("DROP DATABASE " + configuration.scheme)
                logger.info("Database scheme ${configuration.scheme} has been dropped.")

                configuration.schemesToDrop.each { additionalSchemeToDrop ->
                    statement.executeUpdate("DROP DATABASE " + additionalSchemeToDrop)
                    logger.info("Database scheme ${additionalSchemeToDrop} has been dropped.")
                }
            } catch (SQLException ex) {
            } finally {
                if (statement) {
                    manager.closeStatement(statement)
                }

                if (connection) {
                    manager.closeConnection(connection)
                }
            }
        }

        this
    }

}
