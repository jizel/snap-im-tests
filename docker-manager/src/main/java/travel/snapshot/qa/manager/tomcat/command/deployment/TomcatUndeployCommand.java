package travel.snapshot.qa.manager.tomcat.command.deployment;

import org.jboss.shrinkwrap.api.Archive;

/**
 * Command by which some ShrinkWrap archive representing a deployment is undeployed from a container.
 */
public class TomcatUndeployCommand extends TomcatDeployCommand {

    public TomcatUndeployCommand(Archive<?> undeployment) {
        super(undeployment);
    }

    @Override
    public String getCommand() {
        return "/text/undeploy?path=";
    }
}
