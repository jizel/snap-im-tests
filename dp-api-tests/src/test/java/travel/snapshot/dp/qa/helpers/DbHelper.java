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
    private static final String DB_USERNAME = "db.username";
    private static final String DB_PASSWORD = "db.password";

    private final JdbcTemplate identityDb;
    private final JdbcTemplate ttiDb;

    public DbHelper() {
        identityDb = new JdbcTemplate(createDataSource(getProperty("identity.db.connectionString"),
                getProperty(DB_USERNAME), getProperty(DB_PASSWORD)));

        ttiDb = new JdbcTemplate(createDataSource(getProperty("tti.db.connectionString"),
                getProperty(DB_USERNAME), getProperty(DB_PASSWORD)));
    }

    public JdbcTemplate identityDb() {
        return identityDb;
    }

    public JdbcTemplate ttiDb() {
        return ttiDb;
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
