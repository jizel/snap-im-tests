package travel.snapshot.qa.configuration;

import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import travel.snapshot.qa.category.UnitTest;
import travel.snapshot.qa.manager.api.configuration.AbstractConfigurationBuilder;
import travel.snapshot.qa.manager.api.configuration.ConfigurationException;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;

@Category(UnitTest.class)
@RunWith(MockitoJUnitRunner.class)
public class ConfigurationBuilderTestCase {

    private static final String VALID_HOST_ENV_PROPERTY = "tcp://192.168.99.100:2376";

    private static final String INVALID_HOST_ENV_PROPERTY = "$nonsense#";

    private static final String VALID_RESOLVED_HOST_ENV_PROPERTY = "192.168.99.100";

    private static final String DEFAULT_DOCKER_HOST = "127.0.0.1";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void resolvingHostNameFromSystemPropertyTestCase() {
        TestConfigurationBuilder testConfigurationBuilder = Mockito.spy(new TestConfigurationBuilder());

        when(testConfigurationBuilder.getDockerHostEnvProperty()).thenReturn(null);
        when(testConfigurationBuilder.getDockerHostSystemProperty()).thenReturn("127.0.0.2");

        String resolveHostIp = testConfigurationBuilder.resolveHostIp();

        Assert.assertEquals("127.0.0.2", resolveHostIp);
    }

    @Test
    public void resolvingValidHostNameFromEnvironmentPropertyTest() {

        TestConfigurationBuilder testConfigurationBuilder = Mockito.spy(new TestConfigurationBuilder());

        when(testConfigurationBuilder.getDockerHostEnvProperty()).thenReturn(VALID_HOST_ENV_PROPERTY);
        when(testConfigurationBuilder.getDockerHostSystemProperty()).thenReturn(null);

        Assert.assertEquals(VALID_RESOLVED_HOST_ENV_PROPERTY, testConfigurationBuilder.resolveHostIp());
    }

    @Test
    public void resolvingInvalidHostNameFromEnvironmentPropertyTest() {
        TestConfigurationBuilder testConfigurationBuilder = Mockito.spy(new TestConfigurationBuilder());

        when(testConfigurationBuilder.getDockerHostEnvProperty()).thenReturn(INVALID_HOST_ENV_PROPERTY);
        when(testConfigurationBuilder.getDockerHostSystemProperty()).thenReturn(DEFAULT_DOCKER_HOST);

        Assert.assertEquals(DEFAULT_DOCKER_HOST, testConfigurationBuilder.resolveHostIp());
    }

    @Test
    public void invalidServicePortTest() {
        expectedException.expect(ConfigurationException.class);
        expectedException.expectMessage("Specified port is invalid: -100");

        new TomcatManagerConfiguration.Builder().setBindPort(-100).build();
    }

    public class TestConfigurationBuilder extends AbstractConfigurationBuilder {

    }
}
