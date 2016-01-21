package travel.snapshot.qa.manager.tomcat.command.deployment.lifecycle;

import travel.snapshot.qa.manager.tomcat.api.ContainerManagerCommand;

/**
 * Command by which some deployment is reloaded in a container.
 */
public class TomcatReloadDeploymentCommand extends TomcatDeploymentLifecycleCommand implements ContainerManagerCommand {

    public TomcatReloadDeploymentCommand(String deployment) {
        super(deployment);
    }

    @Override
    public String getCommand() {
        return "/text/reload?path=" + deployment;
    }
}
