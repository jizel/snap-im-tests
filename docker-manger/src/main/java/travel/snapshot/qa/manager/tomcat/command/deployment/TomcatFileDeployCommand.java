package travel.snapshot.qa.manager.tomcat.command.deployment;

import travel.snapshot.qa.manager.tomcat.api.ContainerManagerCommand;

/**
 * Command by which some deployment represented by a file path is deployed to a container.
 */
public class TomcatFileDeployCommand implements ContainerManagerCommand {

    private final String fileName;

    public TomcatFileDeployCommand(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getCommand() {
        return "/text/deploy?path=";
    }

    public String getFileName() {
        return fileName;
    }
}
