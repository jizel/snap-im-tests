package travel.snapshot.qa.util.interaction

import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.task.os.CommandTool
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DockerInteraction {

    private static Logger logger = LoggerFactory.getLogger(DockerInteraction)

    /**
     * In case we deal with Docker machine and there are containers inside of it, when we use arquillian.xml file
     * where we bind some directory on our localhost to container, it will not work because there is another
     * layer of abstraction. The situation is like this:
     *
     * Docker containers run locally, in this case mounting is without any problems.
     * <br><br>
     * localhost ---- container
     * <br><br>
     * In case we run inside VirtualBox, situation is like this:
     * <br><br>
     * localhost ---- [virtualbox ---- container]
     * <br><br>
     *
     * So we are unable to mount something from localhost to container directly because VirtualBox stands in a way.
     *
     * In order to be able to mount something to container from our localhost, it would have to be mounted to
     * VirtualBox firstly and after that container would evenutally pick it from VirtualBox mount. This double
     * mounting seems to be quite error-prone and problematic accross different platforms so the best solution
     * seems to be the usage of 'docker-machine scp' command which copies files from localhost to VirtualBox
     * without further setup and it works just out of the box.
     *
     * This can be used for copying resources between machines themselves or from machine to localhost as well.
     *
     * In case we deal with some machine, 'from' or 'to' source have to be preceeded by machine name and semicolon.
     *
     * <p>
     * Examples:
     * <br>
     * <br> 1) I want to copy file from machine 'default' to my localhost: default:/some/file .
     * <br> 2) I want to copy file from one machine to another: machine1:/some/file machine2:/some/destination
     * <br> 3) I want to copy file from localhost to machine: /some/file default:/some/destination
     * </p>
     *
     * @param from local file or whole directory to copy to a Docker machine
     * @param to remote destination on Docker machine where file or directory will be saved
     * @param recursively true to turn on '-r' flag on scp command, false otherwise
     */
    static def copy(String from, String to, boolean recursively) {
        CommandTool scp = Spacelift.task("docker-machine").parameter("scp")

        if (recursively) {
            scp.parameter("-r")
        }

        logger.info("copying from '{}' to '{}' {}", from, to, recursively ? "recursively" : "")

        scp.parameter(from).parameter(to).execute().await()
    }

    /**
     * Copies resources without recursivity.
     *
     * @param from local file or whole directory to copy to a Docker machine
     * @param to remote destination on Docker machine where file or directory will be saved
     */
    static def copy(String from, String to) {
        copy(from, to, false)
    }

    /**
     * Executes command on a machine of name 'default'
     *
     * @param command command to execute
     */
    static def execute(String command) {
        execute("default", command, null)
    }

    static def execute(String command, Integer... exitCodes) {
        execute("default", command, exitCodes)
    }

    /**
     * Executes a command on a machine.
     *
     * @param machineName name of machine to execute command agains
     * @param command command to execute
     */
    static def execute(String machineName, String command, Integer... exitCodes) {

        logger.debug("executing '{}' on '{}'", command, machineName)

        def shouldExitWith = []

        if (!exitCodes) {
            shouldExitWith << 0
        } else {
            shouldExitWith.addAll(Arrays.asList(exitCodes))
        }

        Spacelift.task("docker-machine").parameter("ssh")
                .parameter(machineName)
                .splitToParameters(command)
                .shouldExitWith(shouldExitWith.toArray(new Integer[shouldExitWith.size()]))
                .execute().await()
    }
}
