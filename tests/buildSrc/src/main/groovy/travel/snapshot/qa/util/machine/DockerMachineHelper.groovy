package travel.snapshot.qa.util.machine

import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.process.ProcessResult

class DockerMachineHelper {

    /**
     * Checks if given machine is running or not
     *
     * @param machine machine to check
     * @return true if machine is in state "Running", false otherwise
     */
    static boolean isRunning(String machine) {
        hasState(machine, "Running")
    }

    /**
     * Starts given machine
     *
     * @param machine machine to start
     * @return result of the execution
     */
    static ProcessResult start(String machine) {
        Spacelift.task("docker-machine").parameters("start", machine).shouldExitWith(0, 1).execute().await()
    }

    /**
     * Stops given machine
     *
     * @param machine machine to stop
     * @return result of the execution
     */
    static ProcessResult stop(String machine) {
        Spacelift.task("docker-machine").parameters("stop", machine).execute().await()
    }

    /**
     * Restarts given machine
     *
     * @param machine machine to restart
     * @return result of the execution
     */
    static ProcessResult restart(String machine) {
        Spacelift.task("docker-machine").parameters("restart", machine).execute().await()
    }

    /**
     * Creates machine of given name.
     *
     * @param machine machine to create
     * @return result of the execution
     */
    static ProcessResult create(String machineName) {

        // do we want this to be configurable?

        Spacelift.task("docker-machine")
                .parameter("create")
                .parameter("--driver=virtualbox")
                .parameter("--virtualbox-memory=3072")
                .parameter("--virtualbox-cpu-count=2")
                .parameter(machineName)
                .execute()
                .await()
    }

    /**
     * Parses output of 'docker-machine ls' into its model classes.
     *
     * @return list of parsed machines
     */
    static List<DockerMachineListRecord> parseMachines() {
        ProcessResult result = Spacelift.task("docker-machine").parameter("ls").execute().await()
        DockerMachineListRecord.parse(result.output())
    }

    /**
     * Check if machine is present in 'docker-machine ls' output.
     *
     * @param machine machine to check
     * @return true if machine is present, false otherwise
     */
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

    /**
     * Checks if given machine is in a given state
     *
     * @param machine machine to check the state of
     * @param state state to check
     * @return true if machine is in given state, false otherwise
     */
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

    /**
     * Resolves IP address of the specified machine
     *
     * @param machine machine to get the IP of
     * @return IP of the machine
     */
    static String getIp(String machine) {
        Spacelift.task("docker-machine").parameters("ip", machine).execute().await().output().get(0)
    }

    /**
     * Resolves environment properties of some machine. Internally calls 'docker-machine env machine' and
     * parses environment propertis found there into the map to be returned.
     *
     * @param machine machine to get environment properties for
     * @return environment properties of specified machine
     */
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
