package travel.snapshot.qa.test.execution.db.mariadb

import travel.snapshot.qa.docker.ServiceType
import travel.snapshot.qa.test.execution.db.ModuleDatabaseConfiguration

class MariaDBModuleConfiguration implements ModuleDatabaseConfiguration {

    String scheme

    String flywayScripts

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
}
