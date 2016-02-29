package travel.snapshot.qa.test.execution.load

import static travel.snapshot.qa.test.execution.load.LoadTestsSimulation.FACEBOOK
import static travel.snapshot.qa.test.execution.load.LoadTestsSimulation.IDENTITY_PROPERTY
import static travel.snapshot.qa.test.execution.load.LoadTestsSimulation.IDENTITY_PROPERTY_SET
import static travel.snapshot.qa.test.execution.load.LoadTestsSimulation.IDENTITY_PROPERTY_SET_WITH_USERS
import static travel.snapshot.qa.test.execution.load.LoadTestsSimulation.IDENTITY_USER
import static travel.snapshot.qa.test.execution.load.LoadTestsSimulation.INSTAGRAM
import static travel.snapshot.qa.test.execution.load.LoadTestsSimulation.SOCIAL_COMMON
import static travel.snapshot.qa.test.execution.load.LoadTestsSimulation.TWITTER

enum LoadTestsSimulations {

    ALL{
        @Override
        List<LoadTestsSimulation> simulations() {
            new Builder()
                    .addAll(CONFIGURATION.simulations())
                    .addAll(IDENTITY.simulations())
                    .addAll(WEB_PERFORMANCE.simulations())
                    .addAll(RATE_SHOPPER.simulations())
                    .addAll(SOCIAL.simulations())
                    .addAll(TRIPADVISOR.simulations())
                    .build()
        }
    },
    CONFIGURATION{
        @Override
        List<LoadTestsSimulation> simulations() {
            new Builder().add(LoadTestsSimulation.CONFIGURATION).build()
        }
    },
    IDENTITY{
        @Override
        List<LoadTestsSimulation> simulations() {
            new Builder()
                    .add(IDENTITY_PROPERTY)
                    .add(IDENTITY_PROPERTY_SET)
                    .add(IDENTITY_PROPERTY_SET_WITH_USERS)
                    .add(IDENTITY_USER)
                    .build()
        }
    },
    WEB_PERFORMANCE{
        @Override
        List<LoadTestsSimulation> simulations() {
            new Builder()
                    .add(LoadTestsSimulation.WEB_PERFORMANCE)
                    .build()
        }
    },
    RATE_SHOPPER{
        @Override
        List<LoadTestsSimulation> simulations() {
            new Builder()
                    .add(LoadTestsSimulation.RATE_SHOPPER)
                    .build()
        }
    },
    SOCIAL{
        @Override
        List<LoadTestsSimulation> simulations() {
            new Builder()
                    .add(SOCIAL_COMMON)
                    .add(FACEBOOK)
                    .add(TWITTER)
                    .add(INSTAGRAM)
                    .build()
        }
    },
    TRIPADVISOR{
        @Override
        List<LoadTestsSimulation> simulations() {
            new Builder()
                    .add(LoadTestsSimulation.TRIPADVISOR)
                    .build()
        }
    }

    abstract List<LoadTestsSimulation> simulations()

    static class Builder {

        private List<LoadTestsSimulation> modules = []

        Builder addAll(List<LoadTestsSimulation> modules) {
            this.modules.addAll(modules)
            this
        }

        Builder add(LoadTestsSimulation module) {
            modules << module
            this
        }

        List<LoadTestsSimulation> build() {
            modules
        }
    }
}