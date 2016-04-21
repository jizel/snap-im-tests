package travel.snapshot.qa.manager.redis.check;

import org.arquillian.spacelift.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import travel.snapshot.qa.manager.api.container.ContainerManagerException;
import travel.snapshot.qa.manager.redis.api.RedisManager;
import travel.snapshot.qa.manager.redis.configuration.RedisManagerConfiguration;
import travel.snapshot.qa.manager.redis.impl.RedisManagerImpl;

public class RedisStartedCheckTask extends Task<RedisManagerConfiguration, Boolean> {

    private static final Logger logger = LoggerFactory.getLogger(RedisStartedCheckTask.class);

    @Override
    protected Boolean process(RedisManagerConfiguration configuration) throws Exception {

        RedisManager redisManager = new RedisManagerImpl(configuration);

        Jedis jedis = null;

        try {
            jedis = redisManager.getJedis();
            return true;
        } catch (Exception ex) {
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
            } catch (ContainerManagerException ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Unable to close Redis manager.");
                    ex.printStackTrace();
                }
            }
        }

        return false;
    }
}
