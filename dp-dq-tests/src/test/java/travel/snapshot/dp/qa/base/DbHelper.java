package travel.snapshot.dp.qa.base;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import static travel.snapshot.dp.qa.ConfigProps.getPropValue;

/**
 * Helper for simple database operations using spring's JdbcTemplate.
 */
public class DbHelper {

    private final JdbcTemplate sourceTemplate;
    private final JdbcTemplate targetTemplate;

    public DbHelper() {
        sourceTemplate = new JdbcTemplate(createDataSource(getPropValue("dma.connectionString"),
                getPropValue("dma.username"), getPropValue("dma.password")));
        targetTemplate = new JdbcTemplate(createDataSource(getPropValue("dwh.connectionString"),
                getPropValue("dwh.username"), getPropValue("dwh.password")));
    }

    public JdbcTemplate sourceTemplate() {
        return sourceTemplate;
    }

    public JdbcTemplate targetTemplate() {
        return targetTemplate;
    }

    private DataSource createDataSource(String sourceConnectionString, String username,
                                        String password) {
        try {
            return new SimpleDriverDataSource(
                    DriverManager.getDriver(sourceConnectionString),
                    sourceConnectionString,
                    username,
                    password);
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot get the driver for " + sourceConnectionString);
        }
    }
}
