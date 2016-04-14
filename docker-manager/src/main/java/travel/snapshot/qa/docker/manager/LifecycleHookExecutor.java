package travel.snapshot.qa.docker.manager;

import travel.snapshot.qa.docker.lifecycle.AfterStartHook;
import travel.snapshot.qa.docker.lifecycle.AfterStopHook;
import travel.snapshot.qa.docker.lifecycle.BeforeStartHook;
import travel.snapshot.qa.docker.lifecycle.BeforeStopHook;
import travel.snapshot.qa.manager.api.ServiceManager;

import java.util.ArrayList;
import java.util.List;

public class LifecycleHookExecutor<T extends ServiceManager> {

    private final List<BeforeStartHook<T>> beforeStartHooks = new ArrayList<>();

    private final List<AfterStartHook<T>> afterStartHooks = new ArrayList<>();

    private final List<BeforeStopHook<T>> beforeStopHooks = new ArrayList<>();

    private final List<AfterStopHook<T>> afterStopHooks = new ArrayList<>();

    public LifecycleHookExecutor addBeforeStartHook(BeforeStartHook<T> beforeStartHook) {
        beforeStartHooks.add(beforeStartHook);
        return this;
    }

    public LifecycleHookExecutor addAfterStartHook(AfterStartHook<T> afterStartHook) {
        afterStartHooks.add(afterStartHook);
        return this;
    }

    public LifecycleHookExecutor addBeforeStopHook(BeforeStopHook<T> beforeStopHook) {
        beforeStopHooks.add(beforeStopHook);
        return this;
    }

    public LifecycleHookExecutor addAfterStopHook(AfterStopHook<T> afterStopHook) {
        afterStopHooks.add(afterStopHook);
        return this;
    }

    void executeBeforeStartHooks(T serviceManager) {
        beforeStartHooks.stream().forEach(hook -> hook.execute(serviceManager));
    }

    void executeAfterStartHooks(T serviceManager) {
        afterStartHooks.stream().forEach(hook -> hook.execute(serviceManager));
    }

    void executeBeforeStopHooks(T serviceManager) {
        beforeStopHooks.stream().forEach(hook -> hook.execute(serviceManager));
    }

    void executeAfterStopHooks(T serviceManager) {
        afterStopHooks.stream().forEach(hook -> hook.execute(serviceManager));
    }
}
