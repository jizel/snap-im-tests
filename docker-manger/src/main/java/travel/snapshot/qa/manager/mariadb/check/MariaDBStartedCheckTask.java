package travel.snapshot.qa.manager.mariadb.check;

import org.arquillian.spacelift.task.Task;
import travel.snapshot.qa.manager.mariadb.configuration.MariaDBManagerConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Checks that it is possible to make a {@link java.sql.Connection} to MariaDB.
 *
 * There is a check if MySQL JDBC driver class of {@literal com.mysql.jdbc.Driver} is on the classpath.
 */
public class MariaDBStartedCheckTask extends Task<MariaDBManagerConfiguration, Boolean> {

    private static final String JDBC_MYSQL_DRIVER_CLASS = "com.mysql.jdbc.Driver";

    {
        try {
            Class.forName(JDBC_MYSQL_DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(String.format("Unable to load %s class.", JDBC_MYSQL_DRIVER_CLASS));
        }
    }

    @Override
    protected Boolean process(MariaDBManagerConfiguration configuration) throws Exception {

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(configuration.getConnectionString(), configuration.getUsername(), configuration.getPassword());
        } catch (SQLException ex) {
            // intentionally empty
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    // intentionally empty
                }
            }
        }

        return connection != null;
    }
}
