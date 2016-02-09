package travel.snapshot.qa.util

import org.arquillian.spacelift.gradle.GradleSpaceliftDelegate
import travel.snapshot.qa.docker.manager.ConnectionMode

class PropertyResolver {

    /**
     * This should be externalized to some file we do not know the location of yet
     */
    private static final String DOCKER_REGISTRY_PASSWORD = "aqGG86d1Yf3Y"

    static def resolveDockerMode() {
        def mode = System.getProperty("dockerMode", "host")

        if (mode != "host" && mode != "machine") {
            mode = "host"
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
}
