package travel.snapshot.qa.manager.redis;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;
import travel.snapshot.qa.category.UnitTest;
import travel.snapshot.qa.manager.redis.api.RedisManager;
import travel.snapshot.qa.manager.redis.api.RedisManagerException;
import travel.snapshot.qa.manager.redis.configuration.RedisManagerConfiguration;
import travel.snapshot.qa.manager.redis.impl.JedisPoolFactory;
import travel.snapshot.qa.manager.redis.impl.RedisManagerImpl;

@Category(UnitTest.class)
public class RedisTestCase {

    @Mock
    private Jedis jedis;

    @Mock
    private JedisPool jedisPool;

    @Mock
    private JedisPoolFactory jedisPoolFactory = new JedisPoolFactory(new RedisManagerConfiguration.Builder().build());

    @Spy
    private RedisManager redisManager = new RedisManagerImpl(new RedisManagerConfiguration.Builder().build(), jedisPoolFactory);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void failedClosingOfClientThrowsExceptionTest() {
        expectedException.expect(RedisManagerException.class);
        expectedException.expectMessage("Could not close Jedis pool: exception thrown intentionally");
        expectedException.expectCause(is(instanceOf(JedisException.class)));

        Mockito.doReturn(jedis).when(redisManager).getJedis();
        Mockito.doThrow(new JedisException("exception thrown intentionally")).when(jedis).close();

        redisManager.close();
    }

    @Test
    public void failedGettingOfResourceThrowsExceptionTest() {

        Mockito.doReturn(jedisPool).when(jedisPoolFactory).build();

        Mockito.doThrow(JedisException.class).when(jedisPool).getResource();

        expectedException.expect(RedisManagerException.class);
        expectedException.expectMessage("Unable to get a Jedis resource.");
        expectedException.expectCause(is(instanceOf(JedisException.class)));

        redisManager.getJedis();
    }
}
