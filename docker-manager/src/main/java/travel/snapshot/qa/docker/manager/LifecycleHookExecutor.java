package travel.snapshot.qa.docker.manager;

import travel.snapshot.qa.docker.lifecycle.AfterStartHook;
import travel.snapshot.qa.docker.lifecycle.AfterStopHook;
import travel.snapshot.qa.docker.lifecycle.BeforeStartHook;
import travel.snapshot.qa.docker.lifecycle.BeforeStopHook;
import travel.snapshot.qa.manager.api.ServiceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Executes hooks registered for some Docker service. Hooks are executed in order they have been added.
 */
public class LifecycleHookExecutor<T extends ServiceManager> {

    private final List<BeforeStartHook<T>> beforeStartHooks = new ArrayList<>();

    private final List<AfterStartHook<T>> afterStartHooks = new ArrayList<>();

    private final List<BeforeStopHook<T>> beforeStopHooks = new ArrayList<>();

    private final List<AfterStopHook<T>> afterStopHooks = new ArrayList<>();

    /**
     * Adds a hook which will be executed before a container starts.
     *
     * @param beforeStartHook hook to add
     * @return this
     */
    public LifecycleHookExecutor addBeforeStartHook(final BeforeStartHook<T> beforeStartHook) {
        beforeStartHooks.add(beforeStartHook);
        return this;
    }

    /**
     * Adds a hook which will be executed after a container starts.
     *
     * @param afterStartHook hook to add
     * @return this
     */
    public LifecycleHookExecutor addAfterStartHook(final AfterStartHook<T> afterStartHook) {
        afterStartHooks.add(afterStartHook);
        return this;
    }

    /**
     * Adds a hook which will be executed before a container is stopped.
     *
     * @param beforeStopHook hook to add
     * @return this
     */
    public LifecycleHookExecutor addBeforeStopHook(final BeforeStopHook<T> beforeStopHook) {
        beforeStopHooks.add(beforeStopHook);
        return this;
    }

    /**
     * Adds a hook which will be executed after a container has stopped.
     *
     * @param afterStopHook hook to add
     * @return this
     */
    public LifecycleHookExecutor addAfterStopHook(final AfterStopHook<T> afterStopHook) {
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
