package travel.snapshot.qa.docker.lifecycle;

import travel.snapshot.qa.manager.api.ServiceManager;

/**
 * Implementations of this hook will be executed after Docker container has stopped. However, it is questionable if
 * provided service manager is able to talk to stopped service anymore.
 *
 * This hook is generally handy for custom actions to be performed just after container is stopped regardless of their
 * service manager availability.
 */
public interface AfterStopHook<T extends ServiceManager> extends DockerLifecycleHook<T> {
}
