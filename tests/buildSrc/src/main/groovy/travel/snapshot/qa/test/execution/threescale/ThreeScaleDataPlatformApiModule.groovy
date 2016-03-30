package travel.snapshot.qa.test.execution.threescale

enum ThreeScaleDataPlatformApiModule {

    IDENTITY_APPLICATION_API("Identity Application API", "identity-api/application-api/target/swagger/identity-application-api/swagger.json", ThreeScaleModuleID.IDENTITY_APPLICATION_API),
    IDENTITY_CUSTOMER_API("Identity Customer API", "identity-api/customer-api/target/swagger/identity-customer-api/swagger.json", ThreeScaleModuleID.IDENTITY_CUSTOMER_API),
    IDENTITY_PROPERTY_API("Identity Property API", "identity-api/property-api/target/swagger/identity-property-api/swagger.json", ThreeScaleModuleID.IDENTITY_PROPERTY_API),
    IDENTITY_PROPERTY_SET_API("Identity Property Set API", "identity-api/property-set-api/target/swagger/identity-property-set-api/swagger.json", ThreeScaleModuleID.IDENTITY_PROPERTY_SET_API),
    IDENTITY_ROLE_API("Identity Role API", "identity-api/role-api/target/swagger/identity-role-api/swagger.json", ThreeScaleModuleID.IDENTITY_ROLE_API),
    IDENTITY_SUBSCRIPTION_API("Identity Subscription API", "identity-api/subscription-api/target/swagger/identity-subscription-api/swagger.json", ThreeScaleModuleID.IDENTITY_SUBSCRIPTION_API),
    IDENTITY_USER_API("Identity User API", "identity-api/user-api/target/swagger/identity-user-api/swagger.json", ThreeScaleModuleID.IDENTITY_USER_API),
    CONFIGURATION_API("Configuration API", "configuration-api/target/swagger/configuration-api/swagger.json", ThreeScaleModuleID.CONFIGURATION_API),
    RATE_SHOPPER_API("Rate Shopper API", "rate-shopper-api/target/swagger/rate-shopper-api/swagger.json", ThreeScaleModuleID.RATE_SHOPPER_API),
    SOCIAL_MEDIA_API("Social Media API", "social-media-api/target/swagger/social-media-api/swagger.json", ThreeScaleModuleID.SOCIAL_MEDIA_API),
    WEB_PERFORMANCE("Web Performance API", "web-performance-api/target/swagger/web-performance-api/swagger.json", ThreeScaleModuleID.WEB_PERFORMANCE),
    REVIEW_API("Review API", "review-api/target/swagger/review-api/swagger.json", ThreeScaleModuleID.REVIEW_API)

    final String name

    final String swaggerFilePath

    final ThreeScaleModuleID threeScaleModuleID

    ThreeScaleDataPlatformApiModule(String name, String swaggerFilePath, ThreeScaleModuleID threeScaleModuleID) {
        this.name = name
        this.swaggerFilePath = swaggerFilePath
        this.threeScaleModuleID = threeScaleModuleID
    }

    @Override
    String toString() {
        "${name} - ${swaggerFilePath} - ${threeScaleModuleID}"
    }
}