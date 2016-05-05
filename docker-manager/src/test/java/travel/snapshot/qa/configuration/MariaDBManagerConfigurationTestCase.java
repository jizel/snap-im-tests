package travel.snapshot.qa.configuration;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import travel.snapshot.qa.category.UnitTest;
import travel.snapshot.qa.manager.mariadb.configuration.MariaDBManagerConfiguration;

@Category(UnitTest.class)
public class MariaDBManagerConfigurationTestCase {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void validConfigurationTestCase() {
        MariaDBManagerConfiguration configuration = new MariaDBManagerConfiguration.Builder()
                .bindAddress("127.0.0.1")
                .bindPort(3307)
                .username("admin")
                .password("password")
                .database("test")
                .startupTimeoutInSeconds(123)
                .build();

        assertEquals("127.0.0.1", configuration.getBindAddress());
        assertEquals(3307, configuration.getBindPort());
        assertEquals("admin", configuration.getUsername());
        assertEquals("password", configuration.getPassword());
        assertEquals("test", configuration.getDatabase());
        assertEquals(123, configuration.getStartupTimeoutInSeconds());

        assertEquals("jdbc:mysql://127.0.0.1:3307/test", configuration.getConnectionString());
        assertEquals("jdbc:mysql://127.0.0.1:3307/anotherDB", configuration.getConnectionString("anotherDB"));
    }

    @Test
    public void invalidConfigurationTestCase() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Startup timeout can not be lower than or equal to 0: -1");

        new MariaDBManagerConfiguration.Builder()
                .startupTimeoutInSeconds(-1)
                .build();
    }

}
