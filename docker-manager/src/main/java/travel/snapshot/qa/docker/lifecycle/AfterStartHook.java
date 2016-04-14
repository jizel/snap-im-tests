package travel.snapshot.qa.docker.lifecycle;

import travel.snapshot.qa.manager.api.ServiceManager;

public interface AfterStartHook<T extends ServiceManager> extends DockerLifecycleHook<T> {
}
