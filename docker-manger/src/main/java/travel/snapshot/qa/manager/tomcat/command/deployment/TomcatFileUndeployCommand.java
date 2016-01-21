package travel.snapshot.qa.manager.tomcat.command.deployment;

/**
 * Command by which some deployment represented by a file path is undeployed from a container.
 */
public class TomcatFileUndeployCommand extends TomcatFileDeployCommand {

    public TomcatFileUndeployCommand(String fileName) {
        super(fileName);
    }

    @Override
    public String getCommand() {
        return "/text/undeploy?path=";
    }
}
