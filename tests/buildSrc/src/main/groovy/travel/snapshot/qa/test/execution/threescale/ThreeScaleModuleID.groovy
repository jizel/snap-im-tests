package travel.snapshot.qa.test.execution.threescale

enum ThreeScaleModuleID {

    IDENTITY_APPLICATION_API(53628, 0),
    IDENTITY_CUSTOMER_API(53629, 0),
    IDENTITY_PROPERTY_API(53630, 0),
    IDENTITY_PROPERTY_SET_API(53631, 0),
    IDENTITY_ROLE_API(53632, 0),
    IDENTITY_SUBSCRIPTION_API(53633, 0),
    IDENTITY_COMMERCIAL_SUBSCRIPTION_API(53735, 0),
    IDENTITY_USER_API(53634, 0),
    CONFIGURATION_API(53247, 53258),
    RATE_SHOPPER_API(53250, 53259),
    SOCIAL_MEDIA_API(53245, 53256),
    WEB_PERFORMANCE(53251, 53260),
    REVIEW_API(53246, 53257)

    final int dev

    final int test

    ThreeScaleModuleID(int dev, int test) {
        this.dev = dev
        this.test = test
    }

    @Override
    String toString() {
        "${this.name()} - dev: ${dev}, test: ${test}"
    }

    int getID(ThreeScaleApiEnvironment environment) {
        switch (environment) {
            case ThreeScaleApiEnvironment.DEV:
                return dev
            case ThreeScaleApiEnvironment.TEST:
                return test
            default:
                throw new IllegalArgumentException("Unable to resolve ID of environment ${environment}")
        }
    }
}
