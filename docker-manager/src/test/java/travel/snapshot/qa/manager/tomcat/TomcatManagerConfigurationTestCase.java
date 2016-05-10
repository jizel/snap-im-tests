package travel.snapshot.qa.manager.tomcat;

import static org.junit.Assert.assertEquals;
import static travel.snapshot.qa.manager.tomcat.configuration.HTTPScheme.HTTP;
import static travel.snapshot.qa.manager.tomcat.configuration.HTTPScheme.HTTPS;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import travel.snapshot.qa.category.UnitTest;
import travel.snapshot.qa.manager.api.configuration.ConfigurationException;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;

import java.nio.charset.Charset;
import java.util.UUID;

@Category(UnitTest.class)
public class TomcatManagerConfigurationTestCase {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testDefaultTomcatConfiguration() {

        String hostIp = new TomcatManagerConfiguration.Builder().resolveHostIp();

        final TomcatManagerConfiguration configuration = new TomcatManagerConfiguration.Builder().build();

        assertEquals(hostIp, configuration.getManagerHost());
        assertEquals(8080, configuration.getManagerPort());
        assertEquals(HTTP, configuration.getManagerScheme());
        assertEquals(getManagerURL(hostIp), configuration.getManagerUrl().toString());
    }

    @Test
    public void testCustomTomcatConfiguration() {
        final TomcatManagerConfiguration configuration = new TomcatManagerConfiguration.Builder()
                .setBindAddress("172.17.0.5")
                .setHttpScheme(HTTPS)
                .setBindPort(8433)
                .setUser("admin")
                .setPassword("admin")
                .setUrlCharset(Charset.defaultCharset())
                .setServerConfig("server.xml")
                .setOutputToConsole(false)
                .setStartupTimeoutInSeconds(123)
                .build();

        assertEquals("172.17.0.5", configuration.getManagerHost());
        assertEquals(8433, configuration.getManagerPort());
        assertEquals(HTTPS, configuration.getManagerScheme());
        assertEquals("https://172.17.0.5:8433/manager", configuration.getManagerUrl().toString());
    }

    @Test
    public void testInvalidPort() {
        expectedException.expect(ConfigurationException.class);
        expectedException.expectMessage("Specified port is invalid: -100");

        new TomcatManagerConfiguration.Builder()
                .setHttpScheme(HTTP)
                .setBindAddress("someaddress")
                .setBindPort(-100)
                .build();
    }

    @Test
    public void testPortResolution() {
        TomcatManagerConfiguration httpConfiguration = new TomcatManagerConfiguration.Builder()
                .setHttpScheme(HTTP)
                .build();

        TomcatManagerConfiguration httpsConfiguration = new TomcatManagerConfiguration.Builder()
                .setHttpScheme(HTTPS)
                .build();

        TomcatManagerConfiguration httpsConfigurationWithOverridenPort = new TomcatManagerConfiguration.Builder()
                .setHttpScheme(HTTPS)
                .setBindPort(9443)
                .build();

        Assert.assertEquals(8080, httpConfiguration.getManagerPort());
        Assert.assertEquals(8443, httpsConfiguration.getManagerPort());
        Assert.assertEquals(9443, httpsConfigurationWithOverridenPort.getManagerPort());
    }

    @Test
    public void testNonExistingServerXml() {
        expectedException.expect(IllegalArgumentException.class);

        new TomcatManagerConfiguration.Builder()
                .setServerConfig(UUID.randomUUID().toString())
                .build();
    }

    private String getManagerURL(String hostIp) {
        return String.format("http://%s:8080/manager", hostIp);
    }
}
