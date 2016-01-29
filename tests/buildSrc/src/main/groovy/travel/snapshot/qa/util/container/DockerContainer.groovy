package travel.snapshot.qa.util.container

import org.arquillian.spacelift.Spacelift
import travel.snapshot.qa.util.PropertyResolver

class DockerContainer {

    static def removeContainers(List containers) {
        if (PropertyResolver.resolveConnectionMode() == "STARTANDSTOP") {
            removeContainers("Running", containers)
        }
        // when there is other connection mode then STARTANDSTOP (STARTORCONNECT or STARTORCONNECTANDLEAVE)
        // it means we do not want to remove "Running" containers because we can connect to them
        // so we need to get rid only of Exited ones because when we start them again, it would fail
        // that such container already exists
        removeContainers("Exited", containers)
    }

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
            Spacelift.task("docker").parameters("rm", "-f", it).execute().await()
        }
    }
}
