package travel.snapshot.qa.docker.manager.lifecycle;

import travel.snapshot.qa.manager.api.ServiceManager;

/**
 * Implementations of this hook will be executed before Docker container has started. However, it is questionable if
 * provided service manager is able to talk to yet stopped service.
 *
 * This hook is generally handy for custom actions to be performed just before container is started regardless of their
 * service manager availability.
 */
public interface BeforeStartHook<T extends ServiceManager> extends DockerLifecycleHook<T> {
}
