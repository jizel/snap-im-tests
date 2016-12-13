package travel.snapshot.dp.qa.helpers;

import static travel.snapshot.dp.qa.helpers.PropertiesHelper.getProperty;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;


/**
 * Helper for simple database operations using spring's JdbcTemplate.
 */
public class DbHelper {

    private final JdbcTemplate identityDb;

    public DbHelper() {
        identityDb = new JdbcTemplate(createDataSource(getProperty("identity.db.connectionString"),
                getProperty("identity.db.username"), getProperty("identity.db.password")));

    }

    public JdbcTemplate identityDb() {
        return identityDb;
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
