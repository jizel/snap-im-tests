package travel.snapshot.qa.manager.redis.impl;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;
import travel.snapshot.qa.manager.api.configuration.Validate;
import travel.snapshot.qa.manager.api.container.ContainerManagerException;
import travel.snapshot.qa.manager.redis.api.RedisManager;
import travel.snapshot.qa.manager.redis.configuration.RedisManagerConfiguration;

public class RedisManagerImpl implements RedisManager {

    private final RedisManagerConfiguration configuration;

    private final JedisPool jedisPool;

    public RedisManagerImpl(RedisManagerConfiguration configuration) {
        Validate.notNull(configuration, "Redis manager configuration is a null object.");
        this.configuration = configuration;
        jedisPool = new JedisPoolFactory(configuration).build();
    }

    @Override
    public RedisManagerConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    @Override
    public void close() throws ContainerManagerException {
        try {
            jedisPool.close();
        } catch (JedisException ex) {
            throw new ContainerManagerException("Could not close Jedis pool.", ex);
        }
    }
}
