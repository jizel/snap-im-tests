package travel.snapshot.qa.manager.tomcat.command.deployment;

/**
 * Command by which some deployment represented by a file path is deployed to a container.
 */
public class TomcatFileDeployCommand extends TomcatDeployCommand {

    private final String fileName;

    public TomcatFileDeployCommand(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
