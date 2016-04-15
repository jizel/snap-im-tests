package travel.snapshot.qa.docker.lifecycle;

public interface DockerLifecycleHook<T> {

    void execute(T serviceManager);
}
