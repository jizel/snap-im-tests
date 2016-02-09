package travel.snapshot.qa.test.execution.tomcat

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import travel.snapshot.qa.DataPlatformTestOrchestration
import travel.snapshot.qa.docker.DockerServiceFactory
import travel.snapshot.qa.docker.manager.impl.TomcatDockerManager
import travel.snapshot.qa.manager.tomcat.TomcatManager
import travel.snapshot.qa.test.execution.dataplatform.DataPlatformModule
import travel.snapshot.qa.test.execution.dataplatform.DataPlatformModules

/**
 * Deploys modules (wars) to Tomcat instance.
 */
class DataPlatformDeployer {

    private static final Logger logger = LoggerFactory.getLogger(DataPlatformDeployer)

    private static final String DEFAULT_TOMCAT_CONTAINER =
            DockerServiceFactory.TomcatService.DEFAULT_TOMCAT_CONTAINER_ID

    private final DataPlatformTestOrchestration orchestration

    private final File workspace

    private List<DataPlatformModule> modules = []

    private String containerId = DEFAULT_TOMCAT_CONTAINER

    DataPlatformDeployer(File workspace, DataPlatformTestOrchestration orchestration) {
        this.workspace = workspace
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
        final TomcatDockerManager dockerManager = orchestration.get().getTomcatDockerManager(containerId)
        final TomcatManager manager = dockerManager.getServiceManager()

        if (!dockerManager || !manager) {
            throw new IllegalStateException(
                    String.format("Unable to perform module deployment, managers for container %s are null.", containerId))
        }

        modules.each { module ->
            if (manager.isDeployed(module.getDeploymentContext())) {
                logger.info("Deployment {} is already deployed.", module.getDeploymentContext())
                //manager.undeploy(new File(dataPlatform, module.war).absolutePath)
            } else {

                File deployment = new File(workspace, "data-platform/" + module.war)

                if (!deployment.exists()) {
                    throw new IllegalStateException(String.format("Deployment file %s does not exist.", deployment.absolutePath))
                }

                manager.deploy(deployment.absolutePath)
            }
        }

        this
    }
}