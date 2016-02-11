package travel.snapshot.qa.test.execution.tomcat

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import travel.snapshot.qa.DataPlatformTestOrchestration
import travel.snapshot.qa.docker.DockerServiceFactory
import travel.snapshot.qa.docker.manager.impl.TomcatDockerManager
import travel.snapshot.qa.manager.tomcat.TomcatManager
import travel.snapshot.qa.manager.tomcat.api.ContainerDeploymentException
import travel.snapshot.qa.test.execution.dataplatform.DataPlatformModule
import travel.snapshot.qa.test.execution.dataplatform.DataPlatformModules
import travel.snapshot.qa.util.PropertyResolver

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

        final DeploymentStrategy deploymentStrategy = PropertyResolver.resolveTomcatDeploymentStrategy()

        modules.each { DataPlatformModule module ->

            boolean isDeployed = manager.isDeployed(module.getDeploymentContext())

            switch (deploymentStrategy) {
                case DeploymentStrategy.DEPLOY_OR_FAIL:
                    if (isDeployed) {
                        logger.info(String.format("Deployment of %s is already deployed and you have set " +
                                "DEPLOY_OR_FAIL deployment strategy.", module.getDeploymentContext()))
                        throw new ContainerDeploymentException(String.format("Unable to deploy %s. Such module is already deployed.", module))
                    }
                    deployModule(module, manager)
                    break
                case DeploymentStrategy.DEPLOY_OR_REDEPLOY:
                    if (isDeployed) {
                        logger.info(String.format("Deployment of %s is already deployed and you have set " +
                                "DEPLOY_OR_REDEPLOY deployment strategy. This deployment is going to be undeployed and " +
                                "deployed again.", module.getDeploymentContext()))
                        undeployModule(module, manager)
                    }
                    deployModule(module, manager)
                    break
                case DeploymentStrategy.DEPLOY_OR_SKIP:
                    if (isDeployed) {
                        logger.info(String.format("Deployment of %s is already deployed and you have set " +
                                "DEPLOY_OR_SKIP deployment strategy. This deployment is going to be skipped",
                                module.getDeploymentContext()))
                        break
                    }
                    deployModule(module, manager)
                default:
                    throw new IllegalStateException(String.format("Unable to resolve deployment strategy %s.", deploymentStrategy.name()))
            }
        }

        this
    }

    private def deployModule(DataPlatformModule module, TomcatManager manager) {
        File deployment = new File(workspace, "data-platform/" + module.war)

        if (!deployment.exists()) {
            throw new IllegalStateException(String.format("Deployment file %s does not exist.", deployment.absolutePath))
        }

        logger.info("Deploying of {} module started.", module.getDeploymentContext())

        manager.deploy(deployment.absolutePath)

        logger.info("Deploying of {} module finished.", module.getDeploymentContext())
    }

    private def undeployModule(DataPlatformModule module, TomcatManager manager) {

        logger.info("Undeploying of {} module started.", module.getDeploymentContext())

        manager.undeploy(module.war)

        logger.info("Undeploying of {} module finished.", module.getDeploymentContext())
    }
}