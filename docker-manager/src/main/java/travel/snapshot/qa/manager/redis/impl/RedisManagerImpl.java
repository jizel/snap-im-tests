package travel.snapshot.qa.manager.redis.impl;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;
import travel.snapshot.qa.manager.api.configuration.Validate;
import travel.snapshot.qa.manager.redis.api.RedisManager;
import travel.snapshot.qa.manager.redis.api.RedisManagerException;
import travel.snapshot.qa.manager.redis.configuration.RedisManagerConfiguration;

/**
 * Manages an instance of Redis.
 */
public class RedisManagerImpl implements RedisManager {

    private final RedisManagerConfiguration configuration;

    private final JedisPoolFactory jedisPoolFactory;

    private JedisPool jedisPool;

    public RedisManagerImpl() {
        this(new RedisManagerConfiguration.Builder().build());
    }

    public RedisManagerImpl(RedisManagerConfiguration configuration) {
        Validate.notNull(configuration, "Redis manager configuration is a null object.");
        this.configuration = configuration;
        this.jedisPoolFactory = new JedisPoolFactory(configuration);
    }

    public RedisManagerImpl(RedisManagerConfiguration configuration, JedisPoolFactory jedisPoolFactory) {
        this.configuration = configuration;
        this.jedisPoolFactory = jedisPoolFactory;
    }

    @Override
    public RedisManagerConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public Jedis getJedis() throws RedisManagerException {

        if (jedisPool == null) {
            jedisPool = jedisPoolFactory.build();
        }

        try {
            return jedisPool.getResource();
        } catch (JedisException ex) {
            throw new RedisManagerException("Unable to get a Jedis resource.", ex);
        }

    }

    @Override
    public void close() throws RedisManagerException {
        try {
            getJedis().close();
        } catch (JedisException ex) {
            throw new RedisManagerException(String.format("Could not close Jedis pool: %s", ex.getMessage()), ex);
        }
    }
}
