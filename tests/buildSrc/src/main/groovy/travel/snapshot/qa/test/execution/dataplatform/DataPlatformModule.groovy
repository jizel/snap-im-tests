package travel.snapshot.qa.test.execution.dataplatform

import travel.snapshot.qa.docker.ServiceType
import travel.snapshot.qa.test.execution.db.ModuleDatabaseConfiguration
import travel.snapshot.qa.test.execution.db.mariadb.MariaDBModuleConfiguration

/**
 * Abstraction of module in Data platform repository
 */
enum DataPlatformModule {

    // Tomcat modules

    CONFIGURATION("ConfigurationModule", "ConfigurationModule/build/libs/ConfigurationModule-1.0.war"),
    IDENTITY("IdentityModule", "IdentityModule/build/libs/IdentityModule-1.0.war", new MariaDBModuleConfiguration("identity", "DB/identity/sql")),
    OTA_INTEGRATION("OTAIntegration", "OTAIntegration/build/libs/OTAIntegration-1.0.war"),
    TWITTER("TwitterIntegration", "TwitterIntegration/build/libs/TwitterIntegration-1.0.war"),
    RATE_SHOPPER("RateShopper", "RateShopper/build/libs/RateShopper-1.0.war"),
    WEB_PERFORMANCE("WebPerformance", "WebPerformance/build/libs/WebPerformance-1.0.war"),
    REVIEW("Review", "Review/build/libs/Review-1.0.war"),

    // Tomcat service API modules

    INSTAGRAM_SERVICE_API("Integration/Instagram/ServiceApi", "Integration/Instagram/ServiceApi/build/libs/InstagramAnalyticsApi-1.0-SNAPSHOT.war"),
    FACEBOOK_SERVICE_API("Integration/Facebook/ServiceApi", "Integration/Facebook/ServiceApi/build/libs/FacebookAnalyticsApi-1.0-SNAPSHOT.war"),
    TWITTER_SERVICE_API("Integration/Twitter/ServiceApi", "Integration/Twitter/ServiceApi/build/libs/TwitterAnalyticsApi-1.0-SNAPSHOT.war"),
    SOCIAL_MEDIA_API("Integration/SocialMediaApi", "Integration/SocialMediaApi/build/libs/SocialMediaAnalyticsApi-1.0-SNAPSHOT.war"),

    // Tomcat integrations

    GOOGLE_ANALYTICS_INTEGRATION("GoogleAnalyticsIntegration", "GoogleAnalyticsIntegration/build/libs/GoogleAnalyticsIntegration-1.0.war")

    String path
    String war
    List<ModuleDatabaseConfiguration> databaseConfigurations = []

    /**
     * Path to that module from the root of the repository is equal to its name.
     *
     * @param name name of module, e.g. ConfigurationModule
     * @param war path of war of that module, e.g. ConfigurationModule/build/libs/ConfigurationModule-1.0.war
     */
    DataPlatformModule(String path, String war) {
        this(path, war, null)
    }

    /**
     *
     * @param path path to that module from the root of the repository
     * @param war path of war of that module, e.g. ConfigurationModule/build/libs/ConfigurationModule-1.0.war
     * @param databaseConfigurations database-specific configuration for any module
     */
    DataPlatformModule(String path, String war, ModuleDatabaseConfiguration... databaseConfigurations) {
        this.path = path
        this.war = war

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
