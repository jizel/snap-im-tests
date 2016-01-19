package travel.snapshot.qa.manager.tomcat.command;

import travel.snapshot.qa.manager.tomcat.api.ContainerManagerCommand;

/**
 * Command which lists deployments in a container.
 */
public class TomcatListCommand implements ContainerManagerCommand {

    @Override
    public String getCommand() {
        return "/text/list";
    }

}
