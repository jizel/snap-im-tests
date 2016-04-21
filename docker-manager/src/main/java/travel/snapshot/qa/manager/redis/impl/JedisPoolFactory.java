package travel.snapshot.qa.manager.redis.impl;

import redis.clients.jedis.JedisPool;
import travel.snapshot.qa.manager.redis.configuration.RedisManagerConfiguration;

class JedisPoolFactory {

    private final RedisManagerConfiguration configuration;

    public JedisPoolFactory(RedisManagerConfiguration confguration) {
        this.configuration = confguration;
    }

    public JedisPool build() {

        return new JedisPool(configuration.getJedisPoolConfig(),
                configuration.getBindAddress(),
                configuration.getBindPort(),
                configuration.getConnectionTimeout(),
                configuration.getSoTimeout(),
                configuration.getPassword(),
                configuration.getDatabase(),
                configuration.getClientName());
    }
}
