package travel.snapshot.qa.manager.redis.api;

import redis.clients.jedis.Jedis;
import travel.snapshot.qa.manager.api.ServiceManager;
import travel.snapshot.qa.manager.redis.configuration.RedisManagerConfiguration;

/**
 * Manager for Redis service.
 */
public interface RedisManager extends ServiceManager {

    /**
     * @return configuration for Redis manager.
     */
    RedisManagerConfiguration getConfiguration();

    /**
     * @return Jedis client taken from underlying Jedis pool.
     * @throws RedisManagerException in case it is not possible to get a Jedis client.
     */
    Jedis getJedis() throws RedisManagerException;

    /**
     * Closes underlying Jedis pool. In case pool is already closed, this method does not have any effect.
     *
     * @throws RedisManagerException in case it was not possible to close underlying pool correctly.
     */
    void close() throws RedisManagerException;
}
