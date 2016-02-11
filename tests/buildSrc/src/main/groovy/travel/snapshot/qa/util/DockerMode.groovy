package travel.snapshot.qa.util

/**
 * In case MACHINE mode is used, containers will be managed in VirtualBox (in docker-machine).
 */
enum DockerMode {

    HOST("host"),
    MACHINE("machine")

    private final String name

    DockerMode(String name) {
        this.name = name
    }

    @Override
    String toString() {
        name
    }
}