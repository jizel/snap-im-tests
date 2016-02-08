package travel.snapshot.qa.test.execution.tomcat

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import travel.snapshot.qa.DataPlatformTestOrchestration
import travel.snapshot.qa.docker.DockerServiceFactory
import travel.snapshot.qa.test.execution.dataplatform.DataPlatformModule
import travel.snapshot.qa.test.execution.dataplatform.DataPlatformModules

/**
 * Deploys modules (wars) to Tomcat instance. In case added DataPlatformModule does not have war
 * file built, exception is thrown.
 */
class DataPlatformDeployer {

    private static final Logger logger = LoggerFactory.getLogger(DataPlatformDeployer)

    private DataPlatformTestOrchestration orchestration

    private static final String DEFAULT_TOMCAT_CONTAINER =
            DockerServiceFactory.TomcatService.DEFAULT_TOMCAT_CONTAINER_ID

    private List<DataPlatformModule> modules = []

    private String containerId = DEFAULT_TOMCAT_CONTAINER

    private File workspace

    DataPlatformDeployer(DataPlatformTestOrchestration orchestration) {
        this.orchestration = orchestration
    }

    /**
     * Adds a module to deploy.
     *
     * @param module module to deploy
     * @return this
     */
    DataPlatformDeployer deploy(DataPlatformModule module) {
        if (module) {

            logger.info("Adding {} for deployment.", module.toString())

            this.modules << module
        }
        this
    }

    /**
     * Adds modules for deployment.
     *
     * @param modules modules to deploy
     * @return this
     */
    DataPlatformDeployer deploy(DataPlatformModule... modules) {
        if (modules) {
            for (DataPlatformModule module : modules) {
                deploy(module)
            }
        }
    }

    /**
     * Adds modules for deployment.
     *
     * @param modules modules to deploy
     * @return this
     */
    DataPlatformDeployer deploy(List<DataPlatformModule> modules) {
        for (DataPlatformModule module : modules) {
            deploy(module)
        }
        this
    }

    /**
     * Adds modules for deployment
     *
     * @param modules modules to deploy
     * @return this
     */
    DataPlatformDeployer deploy(DataPlatformModules modules) {
        this.modules.addAll(modules.modules())
        this
    }

    /**
     * Performs actual deployment.
     *
     * @return this
     */
    DataPlatformDeployer execute() {

    }
}
