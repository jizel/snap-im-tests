package travel.snapshot.qa.docker.manager.lifecycle;

import travel.snapshot.qa.manager.api.ServiceManager;

/**
 * Implementations of this hook will be executed after Docker container has started, with initialized service manager
 * able to talk to such Docker service.
 *
 * This hook is generally handy for custom actions to be performed just after container is started.
 */
public interface AfterStartHook<T extends ServiceManager> extends DockerLifecycleHook<T> {
}
