package travel.snapshot.dp.qa;

import org.junit.Test;

import static java.lang.System.setProperty;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static travel.snapshot.dp.qa.ConfigProps.getPropValue;

public class ConfigPropsTest {

    @Test
    public void existingPropertyValue() {
        assertThat(getPropValue("dma.driverClass"), is("com.microsoft.sqlserver.jdbc.SQLServerDriver"));
        assertThat(getPropValue("dwh.connectionString"), is("jdbc:mysql://localhost/dp"));
    }

    @Test
    public void existingPropertyValueOverridenInSystemProperties() {
        final String propertyKey = "dwh.username";
        final String propertyValue = "user.from.system.property";
        final String previousPropertyValue = System.getProperty(propertyKey);

        System.setProperty(propertyKey, propertyValue);
        try {
            assertThat(getPropValue(propertyKey), is(propertyValue));
        } finally {
            resetSystemProperty(propertyKey, previousPropertyValue);
        }
    }

    @Test
    public void newPropertySetInSystemProperties() {
        final String propertyKey = "my.super.custom.property";
        final String propertyValue = "secretValue";
        setProperty(propertyKey, propertyValue);
        assertThat(getPropValue(propertyKey), is(propertyValue));
    }

    @Test
    public void emptyPropertySetInSystemProperties() {
        final String propertyKey = "my.empty.property";
        final String propertyValue = "";
        setProperty(propertyKey, propertyValue);
        assertThat(getPropValue(propertyKey), is(propertyValue));
    }

    @Test
    public void unknownPropertyValueShouldBeNull() {
        assertNull(getPropValue("dma.driverClass.notexist"));
        assertNull(getPropValue("XXX"));
    }


    private void resetSystemProperty(String propertyKey, String previousPropertyValue) {
        if (previousPropertyValue == null) {
            // just remove property completely - Note, that System#clearProperty doesn't work
            // as expected -> the old value is still accessible via System#getProperty
            System.getProperties().remove(propertyKey);
        } else {
            System.setProperty(propertyKey, previousPropertyValue);
        }
    }
}