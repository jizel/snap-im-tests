package travel.snapshot.qa.util

import org.arquillian.spacelift.gradle.GradleSpaceliftDelegate
import travel.snapshot.qa.docker.manager.ConnectionMode
import travel.snapshot.qa.test.execution.tomcat.DeploymentStrategy

class PropertyResolver {

    private static final int DEFAULT_VM_MEMORY_SIZE = 3072 // in MB

    private static final DeploymentStrategy DEFAULT_DEPLOYMENT_STRATEGY = DeploymentStrategy.DEPLOYORREDEPLOY

    private static final ConnectionMode DEFAULT_CONNECTION_MODE = ConnectionMode.STARTORCONNECTANDLEAVE

    private static final DockerMode DEFAULT_DOCKER_MODE = DockerMode.MACHINE

    static def resolveDockerMode() {

        DockerMode resolvedDockerMode

        try {
            resolvedDockerMode = DockerMode.valueOf(System.getProperty("dockerMode", DEFAULT_DOCKER_MODE.name()))
        } catch (Exception ex) {
            resolvedDockerMode = DEFAULT_DOCKER_MODE
        }

        resolvedDockerMode.name()
    }

    static def resolveDockerMachine() {
        System.getProperty("dockerMachine", "default")
    }

    static def resolveConnectionMode() {

        ConnectionMode resolvedConnectionMode

        try {
            resolvedConnectionMode = ConnectionMode.valueOf(System.getProperty("connectionMode", DEFAULT_CONNECTION_MODE.name()))
        } catch (Exception ex) {
            resolvedConnectionMode = DEFAULT_CONNECTION_MODE
        }

        resolvedConnectionMode.name()
    }

    static def resolveDockerRegistryPassword() {
        String password = System.getProperty("dockerRegistryPassword")

        if (!password) {
            throw new IllegalStateException("You have not set system property 'dockerRegistryPassword' with " +
                    "password to Docker registry. The best way to set this property is to add line " +
                    "'systemProp.dockerRegistryPassword=<password>' into your gradle.properties file.")
        }

        password
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
        File root = new GradleSpaceliftDelegate().project().rootDir
        System.getProperty("tomcatSpringConfigDirectory", new File(root, "configuration").absolutePath)
    }

    static def resolveRepositoryFetchSkip() {
        Boolean.parseBoolean(System.getProperty("repositorySkipFetch"))
    }

    /**
     *
     * @return size of memory for VM in MB, when not set on command line, defaults to 3072
     */
    static def resolveDockerMachineMemorySize() {

        int memory

        try {
            memory = Integer.parseInt(System.getProperty("dockerMachineMemorySize", "3072"))
        } catch (NumberFormatException ex) {
            memory = DEFAULT_VM_MEMORY_SIZE
        }

        memory.toString()
    }

    /**
     *
     * @return resolved deployment strategy, when not set on command line, default to DEPLOYORREDEPLOY
     */
    static def resolveTomcatDeploymentStrategy() {

        def deploymentStrategy

        try {
            deploymentStrategy = DeploymentStrategy.valueOf(System.getProperty("tomcatDeploymentStrategy", DEFAULT_DEPLOYMENT_STRATEGY.toString()))
        } catch (Exception ex) {
            deploymentStrategy = DEFAULT_DEPLOYMENT_STRATEGY
        }

        deploymentStrategy.name()
    }
}
