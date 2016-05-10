package travel.snapshot.qa.manager.redis.api;

import travel.snapshot.qa.manager.api.container.ContainerManagerException;

public class RedisManagerException extends ContainerManagerException {

    public RedisManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
