package travel.snapshot.dp.qa.junit.helpers;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import travel.snapshot.dp.qa.junit.helpers.PropertiesHelper;

import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;


/**
 * Helper for simple database operations using spring's JdbcTemplate.
 */
public class DbHelper {
    private static final String DB_USERNAME = "db.username";
    private static final String DB_PASSWORD = "db.password";
    private static final String TTI_DB_USERNAME = "tti.db.username";
    private static final String TTI_DB_PASSWORD = "tti.db.password";

    private final JdbcTemplate identityDb;
    private final JdbcTemplate ttiDb;
    private static PropertiesHelper propertiesHelper = new PropertiesHelper();
    public String DB_URI;

    public DbHelper() {
        DB_URI = propertiesHelper.getProperty("identity.db.connectionString");
        identityDb = new JdbcTemplate(createDataSource(DB_URI,
                propertiesHelper.getProperty(DB_USERNAME), propertiesHelper.getProperty(DB_PASSWORD)));

        ttiDb = new JdbcTemplate(createDataSource(propertiesHelper.getProperty("tti.db.connectionString"),
                propertiesHelper.getProperty(TTI_DB_USERNAME), propertiesHelper.getProperty(TTI_DB_PASSWORD)));
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
