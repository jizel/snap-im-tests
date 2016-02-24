package travel.snapshot.qa.test.execution.tomcat

import org.apache.commons.io.IOUtils
import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.execution.Execution
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
import travel.snapshot.qa.util.Timer

/**
 * Deploys modules (wars) to Tomcat instance.
 */
class TomcatModuleDeployer {

    private static final Logger logger = LoggerFactory.getLogger(TomcatModuleDeployer)

    private static final String DEFAULT_TOMCAT_CONTAINER =
            DockerServiceFactory.TomcatService.DEFAULT_TOMCAT_CONTAINER_ID

    private final DataPlatformTestOrchestration orchestration

    private final File dataPlatformDir

    private List<DataPlatformModule> modules = []

    private String containerId = DEFAULT_TOMCAT_CONTAINER

    private String deploymentStrategy = parseDeploymentStrategy()

    TomcatModuleDeployer(DataPlatformTestOrchestration orchestration) {
        this.dataPlatformDir = PropertyResolver.resolveDataPlatformRepositoryLocation()
        this.orchestration = orchestration
    }

    TomcatModuleDeployer strategy(DeploymentStrategy deploymentStrategy) {
        this.deploymentStrategy = deploymentStrategy
        this
    }

    /**
     * Adds a module to deploy.
     *
     * @param module module to deploy
     * @return this
     */
    TomcatModuleDeployer deploy(DataPlatformModule module) {
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
    TomcatModuleDeployer deploy(DataPlatformModule... modules) {
        if (modules) {
            for (DataPlatformModule module : modules) {
                deploy(module)
            }
        }
        this
    }

    /**
     * Adds modules for deployment.
     *
     * @param modules modules to deploy
     * @return this
     */
    TomcatModuleDeployer deploy(List<DataPlatformModule> modules) {
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
    TomcatModuleDeployer deploy(DataPlatformModules modules) {
        this.modules.addAll(modules.modules())
        this
    }

    /**
     * Performs actual deployment.
     *
     * @return this
     */
    TomcatModuleDeployer execute() {
        final TomcatDockerManager dockerManager = orchestration.get().getTomcatDockerManager(containerId)
        final TomcatManager manager = dockerManager.getServiceManager()

        if (!dockerManager || !manager) {
            throw new IllegalStateException(
                    String.format("Unable to perform module deployment, managers for container %s are null.", containerId))
        }

        final DeploymentStrategy deploymentStrategy = parseDeploymentStrategy()

        modules.each { DataPlatformModule module ->

            boolean isDeployed = manager.isDeployed(module.getDeploymentContext())

            switch (deploymentStrategy) {
                case DeploymentStrategy.DEPLOYORFAIL:
                    if (isDeployed) {
                        logger.info(String.format("Deployment of %s is already deployed and you have set " +
                                "'deploy or fail' deployment strategy.", module.getDeploymentContext()))
                        throw new ContainerDeploymentException(String.format("Unable to deploy %s. Such module is already deployed.", module))
                    }
                    deployModule(module, manager)
                    break
                case DeploymentStrategy.DEPLOYORREDEPLOY:
                    if (isDeployed) {
                        logger.info(String.format("Deployment of %s is already deployed and you have set " +
                                "'deploy or redeploy' deployment strategy. This deployment is going to be undeployed and " +
                                "deployed again.", module.getDeploymentContext()))
                        undeployModule(module, manager)
                    }
                    deployModule(module, manager)
                    break
                case DeploymentStrategy.DEPLOYORSKIP:
                    if (isDeployed) {
                        logger.info(String.format("Deployment of %s is already deployed and you have set " +
                                "'deploy or skip' deployment strategy. This deployment is going to be skipped.",
                                module.getDeploymentContext()))
                        break
                    }
                    deployModule(module, manager)
                    break
                default:
                    throw new IllegalStateException(String.format("Unable to resolve deployment strategy %s.", deploymentStrategy.name()))
            }
        }

        this
    }

    private def deployModule(DataPlatformModule module, TomcatManager manager) {
        File deployment = new File(dataPlatformDir, module.war)

        if (!deployment.exists()) {
            throw new IllegalStateException(String.format("Deployment file %s does not exist.", deployment.absolutePath))
        }

        Timer timer = new Timer()

        logger.info("Deploying of {} module started.", module.getDeploymentContext())

        TomcatContainerLogger loggerTask = Spacelift.task(orchestration.get(), TomcatContainerLogger).container(containerId)
        // we do not await() here, this runs in background so we see Tomcat logs
        Execution<Void> loggerExecution = loggerTask.execute()

        timer.start()

        manager.deploy(deployment.absolutePath)

        timer.stop()

        // here we terminate logging task and close a logging stream
        IOUtils.closeQuietly(loggerTask.getLogOutputStream())
        loggerExecution.terminate()

        logger.info("Deploying of {} module finished in {} seconds.", module.getDeploymentContext(), timer.delta())
    }

    private def undeployModule(DataPlatformModule module, TomcatManager manager) {

        Timer timer = new Timer()

        logger.info("Undeploying of {} module started.", module.getDeploymentContext())

        TomcatContainerLogger loggerTask = Spacelift.task(orchestration.get(), TomcatContainerLogger).container(containerId)
        // we do not await() here, this runs in background so we see Tomcat logs
        Execution<Void> loggerExecution = loggerTask.execute()

        timer.start()

        manager.undeploy(module.war)

        timer.stop()

        // here we terminate logging task and close a logging stream
        IOUtils.closeQuietly(loggerTask.getLogOutputStream())
        loggerExecution.terminate()

        logger.info("Undeploying of {} module finished in {} seconds.", module.getDeploymentContext(), timer.delta())
    }

    private DeploymentStrategy parseDeploymentStrategy() {
        DeploymentStrategy.valueOf(PropertyResolver.resolveTomcatDeploymentStrategy())
    }
}