package travel.snapshot.qa.docker.lifecycle;

/**
 * Hook to execute in some moment in the lifecycle of a container.
 */
public interface DockerLifecycleHook<T> {

    /**
     * Executes custom action.
     *
     * @param serviceManager service manager to execute an action with
     */
    void execute(T serviceManager);
}
