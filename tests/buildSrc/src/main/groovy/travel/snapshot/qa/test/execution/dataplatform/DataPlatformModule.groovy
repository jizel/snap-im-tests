package travel.snapshot.qa.test.execution.dataplatform

import travel.snapshot.qa.docker.ServiceType
import travel.snapshot.qa.test.execution.db.ModuleDatabaseConfiguration
import travel.snapshot.qa.test.execution.db.mariadb.MariaDBModuleConfiguration

/**
 * Abstraction of module in Data platform repository
 */
enum DataPlatformModule {

    PROJECT,

    // Tomcat modules

    CONFIGURATION("ConfigurationModule", "ConfigurationModule/build/libs/ConfigurationModule-1.0.war"),
    IDENTITY("IdentityModule", "IdentityModule/build/libs/IdentityModule-1.0.war", new MariaDBModuleConfiguration("identity", "DB/identity/sql")),

    RATE_SHOPPER("RateShopper", "RateShopper/build/libs/RateShopper-1.0.war"),
    WEB_PERFORMANCE("WebPerformance", "WebPerformance/build/libs/WebPerformance-1.0.war"), // Google Analytics
    REVIEW("Review", "Review/build/libs/Review-1.0.war", [ IDENTITY ]), // TripAdvisor

    // Tomcat service API modules

    SOCIAL_MEDIA_API("Integration:SocialMediaApi", "Integration/SocialMediaApi/build/libs/SocialMediaAnalyticsApi-1.0-SNAPSHOT.war"),
    INSTAGRAM_SERVICE_API("Integration:Instagram:ServiceApi", "Integration/Instagram/ServiceApi/build/libs/InstagramAnalyticsApi-1.0-SNAPSHOT.war"),
    FACEBOOK_SERVICE_API("Integration:Facebook:ServiceApi", "Integration/Facebook/ServiceApi/build/libs/FacebookAnalyticsApi-1.0-SNAPSHOT.war"),
    TWITTER_SERVICE_API("Integration:Twitter:ServiceApi", "Integration/Twitter/ServiceApi/build/libs/TwitterAnalyticsApi-1.0-SNAPSHOT.war"),

    // Tomcat integrations

    OTA_INTEGRATION("OTAIntegration", "OTAIntegration/build/libs/OTAIntegration-1.0.war"), // downloading of data for Rate Shopper

    // meta Data Platform modules for initializing database schemes

    SCHEME_DP("SCHEME_DP", "SCHEME_DP", new MariaDBModuleConfiguration("dp", "DB/dp/sql").addDrop("dp_metadata")),
    SCHEME_IDENTITY("SCHEME_IDENTITY", "SCHEME_IDENTITY", new MariaDBModuleConfiguration("identity", "DB/identity/sql")),
    SCHEME_OTA("SCHEME_OTA", "SCHEME_OTA", new MariaDBModuleConfiguration("OTA_STG", "DB/ota_tti/sql"))

    final String path

    final String war

    final List<ModuleDatabaseConfiguration> databaseConfigurations = []

    final List<DataPlatformModule> dependencies = []

    DataPlatformModule() {}

    /**
     * Creates DataPlatformModule abstraction with given path and war. This module will not depend on anything.
     *
     * @param name name of module, e.g. Integration:Twitter:ServiceApi
     * @param war path of war of that module, e.g. ConfigurationModule/build/libs/ConfigurationModule-1.0.war
     */
    DataPlatformModule(String path, String war) {
        this(path, war, [], null)
    }

    /**
     * Creates DataPlatformModule abstraction with given path and war and database configurations. This module will
     * not depend on anything.
     *
     * @param path path to that module from the root of the repository
     * @param war path of war of that module, e.g. ConfigurationModule/build/libs/ConfigurationModule-1.0.war
     * @param databaseConfigurations database-specific configuration for any module
     */
    DataPlatformModule(String path, String war, ModuleDatabaseConfiguration... databaseConfigurations) {
        this(path, war, [], databaseConfigurations)
    }

    /**
     * Creates DataPlatformModule abstraction with given path and war and database configurations with modules
     * it depends on.
     *
     * @param path path to that module from the root of the repository
     * @param war path of war of that module, e.g. ConfigurationModule/build/libs/ConfigurationModule-1.0.war
     * @param dependencies list of modules this module depends on
     * @param databaseConfigurations database-specific configuration for any module
     */
    DataPlatformModule(String path, String war, List<DataPlatformModule> dependencies, ModuleDatabaseConfiguration... databaseConfigurations) {
        this.path = path
        this.war = war
        this.dependencies.addAll(dependencies)

        if (databaseConfigurations) {
            for (ModuleDatabaseConfiguration configuration : databaseConfigurations) {
                if (configuration) {
                    this.databaseConfigurations << configuration
                }
            }
        }
    }

    String getDeploymentContext() {
        return war.substring(war.lastIndexOf("/"), war.lastIndexOf("."))
    }

    /**
     * Gets database configuration for given service type.
     *
     * @param serviceType service type to get a database configuration for
     * @return respective database configuration or null when not found
     */
    ModuleDatabaseConfiguration getDatabaseConfiguration(ServiceType serviceType) {
        for (ModuleDatabaseConfiguration configuration : databaseConfigurations) {
            if (configuration.serviceType == serviceType) {
                return configuration
            }
        }

        null
    }

    /**
     * Checks if data platform module has some database configuration of given service type.
     *
     * In case service type is different from {@code ServiceType.MARIADB} or {@code ServiceType.MONGODB}, false is returned.
     *
     * @param serviceType service type to check the database module configuration of
     * @return true if such module has given database configuration
     */
    boolean hasDatabase(ServiceType serviceType) {

        if (serviceType != ServiceType.MARIADB && serviceType != ServiceType.MONGODB) {
            return false
        }

        for (ModuleDatabaseConfiguration configuration : databaseConfigurations) {
            if (configuration.serviceType == serviceType) {
                return true
            }
        }

        false
    }

    /**
     *
     * @return list of modules this module depends on
     */
    List<DataPlatformModule> getDependencies() {
        dependencies
    }

    @Override
    String toString() {
        StringBuilder stringBuilder = new StringBuilder()

        stringBuilder.append(String.format("Path: %s, War: %s", path, war))

        if (databaseConfigurations) {
            for (ModuleDatabaseConfiguration moduleDatabaseConfiguration : this.databaseConfigurations) {
                stringBuilder.append(", Database: " + moduleDatabaseConfiguration.serviceType)
            }
        }

        stringBuilder.toString()
    }

}
