package travel.snapshot.qa.test.execution.tomcat

enum DeploymentStrategy {

    /**
     * If module is already deployed, whole deployment fails.
     */
    DEPLOYORFAIL,

    /**
     * If such module is already deployed, that deployment is skipped.
     */
    DEPLOYORSKIP,

    /**
     * If such module is already deployed - it is undeployed and deployed again - otherwise it is just deployed.
     */
    DEPLOYORREDEPLOY;
}