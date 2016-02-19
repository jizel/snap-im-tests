package travel.snapshot.qa.test.execution.dataplatform

import static travel.snapshot.qa.test.execution.dataplatform.DataPlatformModule.*

enum DataPlatformModules {

    ALL {

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
                    .add(CONFIGURATION)
                    .add(IDENTITY)
                    .add(RATE_SHOPPER)
                    .add(WEB_PERFORMANCE)
                    .add(REVIEW)
                    .build()
        }
    },
    TOMCAT_SERVICE_API_MODULES {

        @Override
        List<DataPlatformModule> modules() {
            new Builder()
                    .add(INSTAGRAM_SERVICE_API)
                    .add(FACEBOOK_SERVICE_API)
                    .add(TWITTER_SERVICE_API)
                    .add(SOCIAL_MEDIA_API)
                    .build()
        }
    },
    TOMCAT_INTEGRATIONS {

        @Override
        List<DataPlatformModule> modules() {
            new Builder()
                    .add(DataPlatformModule.GOOGLE_ANALYTICS_INTEGRATION)
                    .add(OTA_INTEGRATION)
                    .add(TWITTER)
                    .build()
        }
    },
    DATABASE_SCHEMES {

        @Override
        List<DataPlatformModule> modules() {
            new Builder()
                .add(DataPlatformModule.SCHEME_DP)
                .add(DataPlatformModule.SCHEME_IDENTITY)
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