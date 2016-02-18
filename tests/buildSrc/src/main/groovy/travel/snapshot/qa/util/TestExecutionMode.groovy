package travel.snapshot.qa.util

/**
 * Whole platform can be executed in various modes. After starting platform in DEVELOPMENT mode,
 * you will be able to deploy wars directly from IDE to containerized Tomcat and there will not be /manager
 * deployment in Tomcat.
 *
 * Once running in TEST mode, you will not be able to deploy wars from IDE but you will get /manager.
 */
enum TestExecutionMode {

    /**
     * Development mode just does not gives you manager deployed in Tomcat container
     * but you can deploy wars directly from your IDE like IDEA via Tomcat Remote run configration
     *
     * This is default.
     */
    DEVELOPMENT,

    /**
     * With this mode, you will not be able to deploy wars from your IDE.
     */
    TEST
}