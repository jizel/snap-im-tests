package travel.snapshot.qa.test.execution.load

import travel.snapshot.qa.test.execution.dataplatform.DataPlatformModule

import static travel.snapshot.qa.test.execution.dataplatform.DataPlatformModule.FACEBOOK_SERVICE_API
import static travel.snapshot.qa.test.execution.dataplatform.DataPlatformModule.IDENTITY
import static travel.snapshot.qa.test.execution.dataplatform.DataPlatformModule.INSTAGRAM_SERVICE_API
import static travel.snapshot.qa.test.execution.dataplatform.DataPlatformModule.REVIEW
import static travel.snapshot.qa.test.execution.dataplatform.DataPlatformModule.SOCIAL_MEDIA_API
import static travel.snapshot.qa.test.execution.dataplatform.DataPlatformModule.TWITTER_SERVICE_API

enum LoadTestsSimulation {

    // Configuration

    CONFIGURATION("travel.snapshot.dp.qa.configuration.ConfigurationSimulation", [ DataPlatformModule.CONFIGURATION ]),

    // Identity

    IDENTITY_PROPERTY_SET("travel.snapshot.dp.qa.identity.IdentityPropertySetSimulation", [ IDENTITY ]),
    IDENTITY_PROPERTY_SET_WITH_USERS("travel.snapshot.dp.qa.identity.IdentityPropertySetWithUsersSimulation", [ IDENTITY ]),
    IDENTITY_PROPERTY("travel.snapshot.dp.qa.identity.IdentityPropertySimulation", [ IDENTITY ]),
    IDENTITY_USER("travel.snapshot.dp.qa.identity.IdentityUserSimulation", [ IDENTITY ]),

    // Performance

    WEB_PERFORMANCE("travel.snapshot.dp.qa.performance.WebPerformanceSimulation", [ DataPlatformModule.WEB_PERFORMANCE ]),

    // RateShopper

    RATE_SHOPPER("travel.snapshot.dp.qa.rateshopper.RateShopperSimulation", [ DataPlatformModule.RATE_SHOPPER ]),

    // Social

    SOCIAL_COMMON("travel.snapshot.dp.qa.social.common.AllAnalyticsSimulation", [ SOCIAL_MEDIA_API ]),
    FACEBOOK("travel.snapshot.dp.qa.social.facebook.FacebookSimulation", [ FACEBOOK_SERVICE_API ]),
    INSTAGRAM("travel.snapshot.dp.qa.social.instagram.InstagramSimulation", [ INSTAGRAM_SERVICE_API ]),
    TWITTER("travel.snapshot.dp.qa.social.twitter.TwitterSimulation", [ TWITTER_SERVICE_API ]),

    // TripAdvisor

    TRIPADVISOR("travel.snapshot.dp.qa.tripadvisor.TripAdvisorSimulation", [ IDENTITY, REVIEW ])

    // class in load tests project
    private final String name

    // module which has to be depoyed when we want to execute some load tests of 'name'
    private final List<DataPlatformModule> modules = []

    LoadTestsSimulation(String name, List<DataPlatformModule> modules) {
        this.name = name
        this.modules = modules
    }

    /**
     *
     * @return list of modules which are needed for some load test simulation to run
     */
    List<DataPlatformModule> requiredModules() {
        modules
    }

    @Override
    String toString() {
        name
    }
}