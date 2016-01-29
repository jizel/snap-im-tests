package travel.snapshot.qa.util.machine

import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.process.ProcessResult

class DockerMachineHelper {

    static boolean isRunning(String machine) {
        hasState(machine, "Running")
    }

    static def start(String machine) {
        Spacelift.task("docker-machine").parameters("start", machine).execute().await()
    }

    static def stop(String machine) {
        Spacelift.task("docker-machine").parameters("stop", machine).execute().await()
    }

    static def restart(String machine) {
        Spacelift.task("docker-machine").parameters("restart", machine).execute().await()
    }

    static def create(String machine) {

        // do we want this to be configurable?

        Spacelift.task("docker-machine")
                .parameter("create")
                .parameter("--driver=virtualbox")
                .parameter("--virtualbox-memory=3072")
                .parameter("--virtualbox-cpu-count=2")
                .parameter(machine)
                .execute()
                .await()
    }

    static List<DockerMachineListRecord> parseMachines() {
        ProcessResult result = Spacelift.task("docker-machine").parameter("ls").execute().await()
        DockerMachineListRecord.parse(result.output())
    }

    static boolean isPresent(String machine) {
        boolean contains = false

        for (DockerMachineListRecord record : parseMachines()) {
            if (record.name == machine) {
                contains = true
                break
            }
        }

        contains
    }

    static boolean hasState(String machine, String state) {

        boolean hasState = false

        for (DockerMachineListRecord record : parseMachines()) {
            if (record.name == machine && record.state == state) {
                hasState = true
                break
            }
        }

        hasState
    }

    static def getIp(String machine) {
        Spacelift.task("docker-machine").parameters("ip", machine).execute().await().output().get(0)
    }

    static Map getEnvironmentProperties(String machine) {
        List<String> rawPropertyLines = Spacelift.task("docker-machine").parameters("env", machine)
                .execute()
                .await()
                .output()
                .findAll { it -> it.startsWith("export") }

        Map environmentProperties = [:]

        rawPropertyLines.each { rawPropertyLine ->
            String[] property = rawPropertyLine.substring("export ".length()).split("\\=")
            String key = property[0]
            String value = property[1].substring(1, property[1].length() - 1)
            environmentProperties.put(key, value)
        }

        environmentProperties
    }
}
