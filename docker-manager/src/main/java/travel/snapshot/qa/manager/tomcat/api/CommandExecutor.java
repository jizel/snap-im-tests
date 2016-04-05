package travel.snapshot.qa.manager.tomcat.api;

import travel.snapshot.qa.manager.api.container.ContainerManagerException;
import travel.snapshot.qa.manager.tomcat.api.response.TomcatResponse;

/**
 * Executes commands against a Tomcat container.
 *
 * @see ContainerManagerCommand
 */
public interface CommandExecutor {

    /**
     * Executes a command against a Tomcat container.
     *
     * @param command command to execute
     * @return TomcatResponse response which is returned by container after command is executed
     */
    TomcatResponse execute(ContainerManagerCommand command) throws ContainerManagerException;
}