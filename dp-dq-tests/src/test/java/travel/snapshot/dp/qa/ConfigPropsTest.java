package travel.snapshot.dp.qa;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static travel.snapshot.dp.qa.ConfigProps.getPropValue;

import org.junit.Test;

public class ConfigPropsTest {

    @Test
    public void existingPropertyValue() {
        assertThat(getPropValue("dma.driverClass"), is("com.microsoft.sqlserver.jdbc.SQLServerDriver"));
        assertThat(getPropValue("dwh.connectionString"), is("jdbc:mysql://localhost/dp"));
    }

    @Test
    public void existingPropertyValueOverridenInTestConfig() {
        assertThat(getPropValue("dwh.username"), is("DUMMY"));
    }

    @Test
    public void unknownPropertyValueShouldBeNull() {
        assertNull(getPropValue("dma.driverClass.notexist"));
        assertNull(getPropValue("XXX"));
    }
}