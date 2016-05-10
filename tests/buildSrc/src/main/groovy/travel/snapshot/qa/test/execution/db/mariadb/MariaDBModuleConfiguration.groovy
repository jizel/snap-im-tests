package travel.snapshot.qa.test.execution.db.mariadb

import travel.snapshot.qa.ServiceType
import travel.snapshot.qa.test.execution.db.ModuleDatabaseConfiguration

/**
 * Database configuration of MariaDB database for some Data platform module.
 */
class MariaDBModuleConfiguration implements ModuleDatabaseConfiguration {

    String scheme

    String flywayScripts

    def schemesToDrop = []

    MariaDBModuleConfiguration(String scheme) {
        this(scheme, null)
    }

    MariaDBModuleConfiguration(String scheme, String flywayScripts) {
        this.scheme = scheme
        this.flywayScripts = flywayScripts
    }

    @Override
    ServiceType getServiceType() {
        ServiceType.MARIADB
    }

    def addDrop(String scheme) {
        schemesToDrop << scheme
        this
    }
}
