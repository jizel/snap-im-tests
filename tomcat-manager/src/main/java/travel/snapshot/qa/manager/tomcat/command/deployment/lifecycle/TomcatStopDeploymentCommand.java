package travel.snapshot.qa.manager.tomcat.command.deployment.lifecycle;

import travel.snapshot.qa.manager.tomcat.api.ContainerManagerCommand;

/**
 * Command by which some started deployment is stopped in a container.
 */
public class TomcatStopDeploymentCommand extends TomcatDeploymentLifecycleCommand implements ContainerManagerCommand {

    public TomcatStopDeploymentCommand(String deployment) {
        super(deployment);
    }

    @Override
    public String getCommand() {
        return "/text/stop?path=" + deployment;
    }
}
