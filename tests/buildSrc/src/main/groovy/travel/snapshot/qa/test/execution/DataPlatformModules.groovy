package travel.snapshot.qa.test.execution

enum DataPlatformModules {

    ALL{
        @Override
        List<DataPlatformModule> modules() {
            new Builder()
                    .addAll(TOMCAT_MODULES.modules())
                    .addAll(TOMCAT_SERVICE_API_MODULES.modules())
                    .addAll(TOMCAT_INTEGRATIONS.modules())
                    .build()
        }
    },
    TOMCAT_MODULES {
        @Override
        List<DataPlatformModule> modules() {
            new Builder()
                    .add(new DataPlatformModule("ConfigurationModule", "ConfigurationModule/build/libs/ConfigurationModule-1.0.war"))
                    .add(new DataPlatformModule("IdentityModule", "IdentityModule/build/libs/IdentityModule-1.0.war"))
                    //.add(new DataPlatformModule("OTAIntegration", "OTAIntegration/build/libs/OTAIntegration-1.0.war"))
                    //.add(new DataPlatformModule("TwitterIntegration", "TwitterIntegration/build/libs/TwitterIntegration-1.0.war"))
                    //.add(new DataPlatformModule("RateShopper", "RateShopper/build/libs/RateShopper-1.0.war"))
                    //.add(new DataPlatformModule("WebPerformance", "WebPerformance/build/libs/WebPerformance-1.0.war"))
                    //.add(new DataPlatformModule("Review", "Review/build/libs/Review-1.0.war"))
                    .build()
        }
    },
    TOMCAT_SERVICE_API_MODULES {
        @Override
        List<DataPlatformModule> modules() {
            new Builder()
                    .add(new DataPlatformModule("Integration/Instagram/ServiceApi", "Integration/Instagram/ServiceApi/build/libs/InstagramAnalyticsApi-1.0-SNAPSHOT.war"))
                    .add(new DataPlatformModule("Integration/Facebook/ServiceApi", "Integration/Facebook/ServiceApi/build/libs/FacebookAnalyticsApi-1.0-SNAPSHOT.war"))
                    .add(new DataPlatformModule("Integration/Twitter/ServiceApi", "Integration/Twitter/ServiceApi/build/libs/TwitterAnalyticsApi-1.0-SNAPSHOT.war"))
                    .add(new DataPlatformModule("Integration/SocialMediaApi", "Integration/SocialMediaApi/build/libs/SocialMediaAnalyticsApi-1.0-SNAPSHOT.war"))
                    .build()
        }
    },
    TOMCAT_INTEGRATIONS {
        @Override
        List<DataPlatformModule> modules() {
            new Builder()
                    .add(new DataPlatformModule("GoogleAnalyticsIntegration", "GoogleAnalyticsIntegration/build/libs/GoogleAnalyticsIntegration-1.0.war"))
                    .build()
        }
    }

    abstract List<DataPlatformModule> modules()

    static class Builder {

        private List<DataPlatformModule> modules = []

        Builder addAll(List<DataPlatformModule> modules) {
            this.modules.addAll(modules)
            this
        }

        Builder add(DataPlatformModule module) {
            modules << module
            this
        }

        List<DataPlatformModule> build() {
            modules
        }
    }
}