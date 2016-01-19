package travel.snapshot.qa.manager.tomcat.api;

/**
 * Represents a command which is executed against a container.
 *
 * @see CommandExecutor
 */
public interface ContainerManagerCommand {

    /**
     * @return string representation of a command to execute
     */
    String getCommand();
}
