package travel.snapshot.qa.util

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
}
