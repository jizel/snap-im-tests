package travel.snapshot.qa.util

import org.arquillian.spacelift.gradle.GradleSpaceliftDelegate
import travel.snapshot.qa.docker.manager.ConnectionMode
import travel.snapshot.qa.test.execution.tomcat.DeploymentStrategy

class PropertyResolver {

    /**
     * This should be externalized to some file we do not know the location of yet
     */
    private static final String DOCKER_REGISTRY_PASSWORD = "aqGG86d1Yf3Y"

    private static final int DEFAULT_VM_MEMORY_SIZE = 3072

    private static final DeploymentStrategy DEFAULT_DEPLOYMENT_STRATEGY = DeploymentStrategy.DEPLOY_OR_REDEPLOY

    static def resolveDockerMode() {
        def mode = System.getProperty("dockerMode", DockerMode.HOST.toString())

        if (mode != DockerMode.HOST.toString() && mode != DockerMode.MACHINE.toString()) {
            mode = DockerMode.HOST.toString()
        }

        mode
    }

    static def resolveDockerMachine() {
        System.getProperty("dockerMachine", "default")
    }

    static def resolveConnectionMode() {

        def defaultConnectionMode = ConnectionMode.STARTANDSTOP

        def resolvedConnectionMode

        try {
            resolvedConnectionMode = ConnectionMode.valueOf(System.getProperty("connectionMode"))
        } catch (Exception ex) {
            resolvedConnectionMode = defaultConnectionMode
        }

        resolvedConnectionMode.name()
    }

    static def resolveDockerRegistryPassword() {
        System.getProperty("dockerRegistryPassword", DOCKER_REGISTRY_PASSWORD)
    }

    static def resolveDataPlatformRespositoryCommit(String defaultCommit) {
        System.getProperty("dataPlatformRepositoryCommit", defaultCommit)
    }

    static def resolveDataPlatformQARespositoryCommit(String defaultCommit) {
        System.getProperty("dataPlatformQARepositoryCommit", defaultCommit)
    }

    static def resolveForceDataPlatformBuild() {
        Boolean.parseBoolean(System.getProperty("forceDataPlatformBuild"))
    }

    static def resolveTomcatSpringConfigDirectory() {

        File rootDir = (File) new GradleSpaceliftDelegate().project().rootDir
        File tomcatConfigurationDirectory = new File(rootDir, "configuration/tomcat")

        System.getProperty("tomcatSpringConfigDirectory", tomcatConfigurationDirectory.getAbsolutePath())
    }

    static def resolveRepositoryFetchSkip() {
        Boolean.parseBoolean(System.getProperty("repositorySkipFetch"))
    }

    /**
     *
     * @return size of memory for VM in MB, when not set on command line, defaults to 3072
     */
    static def resolveDockerMachineMemorySize() {

        int memory = DEFAULT_VM_MEMORY_SIZE

        try {
            memory = Integer.parseInt(System.getProperty("dockerMachineMemorySize", "3072"))
        } catch (NumberFormatException ex) {

        }

        memory.toString()
    }

    /**
     *
     * @return resolved deployment strategy, when not set on command line, default to DEPLOY_OR_REDEPLOY
     */
    static DeploymentStrategy resolveTomcatDeploymentStrategy() {

        DeploymentStrategy deploymentStrategy = DEFAULT_DEPLOYMENT_STRATEGY

        try {
            deploymentStrategy = DeploymentStrategy.valueOf(System.getProperty("tomcatDeploymentStrategy", DEFAULT_DEPLOYMENT_STRATEGY.toString()))
        } catch (Exception ex) {
            // intentionally empty
        }

        deploymentStrategy
    }
}
