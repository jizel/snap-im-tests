package travel.snapshot.qa.util.machine

import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.process.ProcessResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import travel.snapshot.qa.util.PropertyResolver

class DockerMachineHelper {

    private static final Logger logger = LoggerFactory.getLogger(DockerMachineHelper)

    static final String DEFAULT_VIRTUALBOX_INTERFACE_IP = "192.168.99.1"

    /**
     * Checks if given machine is running or not
     *
     * @param machine machine to check
     * @return true if machine is in state "Running", false otherwise
     */
    static boolean isRunning(String machine) {
        hasState(machine, "Running")
    }

    static boolean isTimeouted(String machine) {
        hasState(machine, "Timeout")
    }

    /**
     * Starts given machine
     *
     * @param machine machine to start
     * @return result of the execution
     */
    static ProcessResult start(String machine) {

        logger.info("Starting Docker machine {}.", machine)

        Spacelift.task("docker-machine").parameters("start", machine).execute().await()
    }

    /**
     * Stops given machine
     *
     * @param machine machine to stop
     * @return result of the execution
     */
    static ProcessResult stop(String machine) {

        logger.info("Stopping Docker machine {}.", machine)

        Spacelift.task("docker-machine").parameters("stop", machine).execute().await()
    }

    /**
     * Restarts given machine
     *
     * @param machine machine to restart
     * @return result of the execution
     */
    static ProcessResult restart(String machine) {

        logger.info("Restarting Docker machine {}.", machine)

        Spacelift.task("docker-machine").parameters("restart", machine).execute().await()
    }

    /**
     * Creates machine of given name. Amount of memory in MB to dedicate for VM can be set by
     * 'dockerMachineMemorySize' system property and by default it is set to '3072'
     *
     * @param machine machine to create
     * @return result of the execution
     */
    static ProcessResult create(String machineName) {

        logger.info("Creating Docker machine {}.", machineName)

        Spacelift.task("docker-machine")
                .parameter("create")
                .parameter("--driver=virtualbox")
                .parameter("--virtualbox-memory=${PropertyResolver.resolveDockerMachineMemorySize()}")
                .parameter("--virtualbox-cpu-count=2")
                .parameter(machineName)
                .execute()
                .await()
    }

    /**
     * Removes host only interfaces of VirtualBox. Interfaces to remove are parsed firstly from 'VboxManage list -l hostonlyifs'
     */
    static def removeHostOnlyInterfaces() {
        List<String> result = Spacelift.task("VBoxManage").parameters("list", "-l", "hostonlyifs").execute().await().output()

        Map<String, String> hostOnlyIfsToRemove = parseHostOnlyInterfacesToRemove(result)

        hostOnlyIfsToRemove.each { name, ip ->
            logger.info("Removing host only interface {} of IP {}.", name, ip)
            removeHostOnlyInterface(name)
        }
    }

    /**
     * Parses output of 'docker-machine ls' into its model classes.
     *
     * @return list of parsed machines
     */
    static List<DockerMachineListRecord> parseMachines() {
        ProcessResult result = Spacelift.task("docker-machine")
                .parameter("ls")
                .parameter("--format")
                .parameter("{{.Name}}:{{.DriverName}}:{{.State}}:{{.URL}}:")
                .execute().await()
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
        List<String> rawPropertyLines = Spacelift.task(machine, DockerMachineEnvTask)
                .execute()
                .await()
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

    /**
     * Removes host only interface by VBoxManage command
     *
     * @param hostOnlyInterface host interface to remove, e.g "vboxnet0".
     * @return
     */
    static ProcessResult removeHostOnlyInterface(String hostOnlyInterface) {
        Spacelift.task("VBoxManage").parameters("hostonlyif", "remove", hostOnlyInterface).execute().await()
    }

    private static Map<String, String> parseHostOnlyInterfacesToRemove(List<String> output) {

        // Example output which gets parsed

        //    Name:            vboxnet0
        //    GUID:            786f6276-656e-4074-8000-0a0027000000
        //    DHCP:            Disabled
        //    IPAddress:       192.168.99.1
        //    NetworkMask:     255.255.255.0
        //    IPV6Address:
        //    IPV6NetworkMaskPrefixLength: 0
        //    HardwareAddress: 0a:00:27:00:00:00
        //    MediumType:      Ethernet
        //    Status:          Down
        //    VBoxNetworkName: HostInterfaceNetworking-vboxnet0

        Map<String, String> hostOnlyInterfaces = [:]

        String name = null
        String ipAddress = null

        for (String line : output) {
            if (line.startsWith("Name:")) {
                name = line.substring(line.lastIndexOf(" ") + 1)
            }

            if (line.startsWith("IPAddress:")) {
                ipAddress = line.substring(line.lastIndexOf(" ") + 1)
            }

            if (name && ipAddress) {
                hostOnlyInterfaces.put(name, ipAddress)
                name = null
                ipAddress = null
            }
        }

        Map<String, String> hostOnlyInterfacesToDelete = [:]

        for (Map.Entry<String, String> entry : hostOnlyInterfaces) {
            if (entry.value.equals(DEFAULT_VIRTUALBOX_INTERFACE_IP)) {
                hostOnlyInterfacesToDelete.put(entry.key, entry.value)
            }
        }

        hostOnlyInterfacesToDelete
    }
}
