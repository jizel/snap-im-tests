package travel.snapshot.qa.docker.lifecycle;

import travel.snapshot.qa.manager.api.ServiceManager;

/**
 * Implementations of this hook will be executed before Docker container has stopped.
 *
 * This hook is generally handy for custom actions to be performed just before container is stopped regardless of their
 * service manager usage.
 */
public interface BeforeStopHook<T extends ServiceManager> extends DockerLifecycleHook<T> {
}
