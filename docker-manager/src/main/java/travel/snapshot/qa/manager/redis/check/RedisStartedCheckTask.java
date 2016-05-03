package travel.snapshot.qa.manager.redis.check;

import org.arquillian.spacelift.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;
import travel.snapshot.qa.manager.redis.api.RedisManager;
import travel.snapshot.qa.manager.redis.api.RedisManagerException;
import travel.snapshot.qa.manager.redis.configuration.RedisManagerConfiguration;
import travel.snapshot.qa.manager.redis.impl.RedisManagerImpl;

import java.util.UUID;

/**
 * Checks if Redis is started or not. Redis service is considered to be started in case it is possible to get a Jedis
 * resource from a {@link JedisPool} without exception being thrown.
 *
 * @see JedisPool#getResource()
 */
public class RedisStartedCheckTask extends Task<RedisManagerConfiguration, Boolean> {

    private static final Logger logger = LoggerFactory.getLogger(RedisStartedCheckTask.class);

    @Override
    protected Boolean process(RedisManagerConfiguration configuration) throws Exception {

        final RedisManager redisManager = new RedisManagerImpl(configuration);

        Jedis jedis = null;

        try {
            jedis = redisManager.getJedis();

            String randomKey = UUID.randomUUID().toString();

            jedis.set(randomKey, "123");
            jedis.del(randomKey);

            return true;
        } catch (JedisException ex) {
            if (logger.isDebugEnabled()) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Unable to close Jedis client");
                    ex.printStackTrace();
                }
            }

            try {
                redisManager.close();
            } catch (RedisManagerException ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Unable to close Redis manager.");
                    ex.printStackTrace();
                }
            }
        }

        return false;
    }
}
