package travel.snapshot.qa.test.execution.db

import travel.snapshot.qa.docker.ServiceType

interface ModuleDatabaseConfiguration {

    ServiceType getServiceType()
}