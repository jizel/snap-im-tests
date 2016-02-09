package travel.snapshot.qa.manager.mariadb.api;

import travel.snapshot.qa.manager.api.FlywayMigration;
import travel.snapshot.qa.manager.api.ServiceManager;
import travel.snapshot.qa.manager.mariadb.configuration.MariaDBManagerConfiguration;

import java.io.File;
import java.io.Reader;
import java.sql.Connection;
import java.sql.Statement;

/**
 * MariaDB operations interface. Gathers methods which simplifies and enables you to interact with MariaDB.
 */
public interface MariaDBManager extends ServiceManager, FlywayMigration {

    /**
     * Blocks until it is possible to get {@link java.sql.Connection} to MariaDB.
     *
     * @see travel.snapshot.qa.manager.mariadb.check.MariaDBStartedCheckTask
     */
    void waitForConnectivity();

    /**
     * Executes SQL script against MariaDB
     *
     * @param reader reader of a SQL script
     */
    void executeScript(Reader reader);

    /**
     * Executes SQL script against MariaDB
     *
     * @param sqlScript path to SQL script to execute
     */
    void executeScript(String sqlScript);

    /**
     * Executes SQL script against MariaDB
     *
     * @param sqlScript file of SQL script to execute
     */
    void executeScript(File sqlScript);

    /**
     * Executes SQL script against MariaDB
     *
     * @param connection connection to use during execution of the script
     * @param sqlScript  path to SQL script to execute
     */
    void executeScript(Connection connection, String sqlScript);

    /**
     * Executes SQL script against MariaDB
     *
     * @param connection connection to use during execution of the script
     * @param sqlScript  file of SQL script to execute
     */
    void executeScript(Connection connection, File sqlScript);

    /**
     * Executes SQL script against MariaDB via specified connection.
     *
     * @param connection connection to use for the script execution
     * @param reader     reader of a SQL script
     */
    void executeScript(Connection connection, Reader reader);

    /**
     * Gets connection to MariaDB by which you can e.g. execute scripts or perform any query.
     *
     * @return connection to MariaDB
     */
    Connection getConnection();

    /**
     * Gets connection to the specified database.
     *
     * @param database database to get the connection to
     * @return connecion to the given database
     */
    Connection getConnection(String database);

    /**
     * Closes SQL connection to MariaDB.
     *
     * @param connection connection to close
     */
    void closeConnection(Connection connection);

    /**
     * Closes SQL statement.
     *
     * @param statement statement to close
     */
    void closeStatement(Statement statement);

    /**
     * Gets configuration of MariaDB manager.
     *
     * @return client configuration of MariaDB Manager.
     */
    MariaDBManagerConfiguration getConfiguration();
}
