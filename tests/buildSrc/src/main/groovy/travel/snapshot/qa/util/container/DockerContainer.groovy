package travel.snapshot.qa.util.container

import org.arquillian.spacelift.Spacelift
import travel.snapshot.qa.docker.manager.ConnectionMode
import travel.snapshot.qa.util.PropertyResolver

class DockerContainer {

    /**
     * Deletes containers when necessary.
     *
     * If connection mode is "STARTANDSTOP", containers with status "Running" will be removed. After that, all
     * containers with status "Exited" are removed as well.
     *
     * @param containers containers to iterate over for deletion
     * @return
     */
    static def removeContainers(List containers) {
        if (PropertyResolver.resolveConnectionMode() == ConnectionMode.STARTANDSTOP.name()) {
            removeContainers("Running", containers)
        }
        // when there is other connection mode then STARTANDSTOP (STARTORCONNECT or STARTORCONNECTANDLEAVE)
        // it means we do not want to remove "Running" containers because we can connect to them
        // so we need to get rid only of Exited ones because when we start them again, it would fail
        // that such container already exists
        removeContainers("Exited", containers)
    }

    /**
     * Removes containers of given status.
     *
     * @param status status of container to remove
     * @param containers containers to iterate over
     */
    static def removeContainers(String status, List containers) {
        Spacelift.task("docker")
                .parameter("ps")
                .parameter("--filter=status=${status.toLowerCase()}")
                .parameter("--format={{.Names}}")
                .execute()
                .await()
                .output()
                .findAll {
            containers.contains(it)
        }
        .each {
            removeContainer(it)
        }
    }

    static def removeContainer(String container) {
        Spacelift.task("docker").parameters("rm", "-f", container).execute().await()
    }

    static List<String> list() {
        Spacelift.task("docker").parameters("ps", "--format", "{{.Names}}").execute().await().output()
    }

    static List<String> listWithImages() {
        Spacelift.task("docker").parameters("ps", "--format", "{{.Names}} {{.Image}}").execute().await().output()
    }
}
