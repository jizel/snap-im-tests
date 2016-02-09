package travel.snapshot.qa.test.execution.db

import travel.snapshot.qa.docker.ServiceType

/**
 * Every specific database configuration should implement this interface
 */
interface ModuleDatabaseConfiguration {

    /**
     *
     * @return service type of Docker service this database configuration is for
     */
    ServiceType getServiceType()
}