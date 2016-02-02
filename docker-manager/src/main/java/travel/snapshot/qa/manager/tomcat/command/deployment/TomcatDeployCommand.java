package travel.snapshot.qa.manager.tomcat.command.deployment;

import org.jboss.shrinkwrap.api.Archive;
import travel.snapshot.qa.manager.tomcat.api.ContainerManagerCommand;

/**
 * Command by which some ShrinkWrap archive representing a deployment is deployed to a container.
 */
public class TomcatDeployCommand implements ContainerManagerCommand {

    private final Archive<?> deployment;

    public TomcatDeployCommand(Archive<?> deployment) {
        this.deployment = deployment;
    }

    @Override
    public String getCommand() {
        return "/text/deploy?path=";
    }

    public Archive<?> getDeployment() {
        return deployment;
    }
}
