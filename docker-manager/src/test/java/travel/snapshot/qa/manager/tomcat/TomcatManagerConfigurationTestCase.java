package travel.snapshot.qa.manager.tomcat;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import travel.snapshot.qa.category.UnitTest;
import travel.snapshot.qa.manager.tomcat.configuration.HTTPScheme;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;

@Category(UnitTest.class)
public class TomcatManagerConfigurationTestCase {

    @Test
    public void testDefaultTomcatConfiguration() {

        String hostIp = new TomcatManagerConfiguration.Builder().resolveHostIp();

        final TomcatManagerConfiguration configuration = new TomcatManagerConfiguration.Builder().build();

        assertEquals(hostIp, configuration.getManagerHost());
        assertEquals(8080, configuration.getManagerPort());
        assertEquals(HTTPScheme.HTTP, configuration.getManagerScheme());
        assertEquals(getManagerURL(hostIp), configuration.getManagerUrl().toString());
    }

    @Test
    public void testCustomTomcatConfiguration() {
        final TomcatManagerConfiguration configuration = new TomcatManagerConfiguration.Builder()
                .setBindAddress("172.17.0.5")
                .setBindPort(8433)
                .setHttpScheme(HTTPScheme.HTTPS)
                .build();

        assertEquals("172.17.0.5", configuration.getManagerHost());
        assertEquals(8433, configuration.getManagerPort());
        assertEquals(HTTPScheme.HTTPS, configuration.getManagerScheme());
        assertEquals("https://172.17.0.5:8433/manager", configuration.getManagerUrl().toString());
    }

    private String getManagerURL(String hostIp) {
        return String.format("http://%s:8080/manager", hostIp);
    }
}
