package travel.snapshot.qa.test.execution.tomcat

enum DeploymentStrategy {

    /**
     * If module is already deployed, whole deployment fails.
     */
    DEPLOY_OR_FAIL,

    /**
     * If such module is already deployed, that deployment is skipped.
     */
    DEPLOY_OR_SKIP,

    /**
     * If such module is already deployed - it is undeployed and deployed again - otherwise it is just deployed.
     */
    DEPLOY_OR_REDEPLOY;
}