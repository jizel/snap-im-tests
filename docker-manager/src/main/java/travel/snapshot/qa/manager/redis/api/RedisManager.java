package travel.snapshot.qa.manager.redis.api;

import redis.clients.jedis.Jedis;
import travel.snapshot.qa.manager.api.ServiceManager;
import travel.snapshot.qa.manager.api.container.ContainerManagerException;
import travel.snapshot.qa.manager.redis.configuration.RedisManagerConfiguration;

public interface RedisManager extends ServiceManager {

    RedisManagerConfiguration getConfiguration();

    /**
     * @return Jedis client taken from underlying Jedis pool.
     */
    Jedis getJedis();

    /**
     * Closes underlying Jedis pool. In case pool is already closed, this method does not have any effect.
     *
     * @throws ContainerManagerException in case it was not possible to close underlying pool correctly.
     */
    void close() throws ContainerManagerException;
}
