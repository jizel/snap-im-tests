package travel.snapshot.qa.manager.tomcat.command.deployment.lifecycle;

import travel.snapshot.qa.manager.tomcat.api.ContainerManagerCommand;

/**
 * Command by which some stopped deployment is started in a container.
 */
public class TomcatStartDeploymentCommand extends TomcatDeploymentLifecycleCommand implements ContainerManagerCommand {

    public TomcatStartDeploymentCommand(String deployment) {
        super(deployment);
    }

    @Override
    public String getCommand() {
        return "/text/start?path=" + deployment;
    }
}
