package travel.snapshot.qa.manager.mariadb.impl;

import static java.util.concurrent.TimeUnit.SECONDS;

import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.execution.CountDownWatch;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.manager.api.BasicWaitingCondition;
import travel.snapshot.qa.manager.api.configuration.Validate;
import travel.snapshot.qa.manager.mariadb.api.MariaDBManager;
import travel.snapshot.qa.manager.mariadb.api.MariaDBManagerException;
import travel.snapshot.qa.manager.mariadb.check.MariaDBStartedCheckTask;
import travel.snapshot.qa.manager.mariadb.configuration.MariaDBManagerConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MariaDBManagerImpl implements MariaDBManager {

    private static final Logger logger = LoggerFactory.getLogger(MariaDBManagerImpl.class);

    private final MariaDBManagerConfiguration configuration;

    public MariaDBManagerImpl() {
        this.configuration = new MariaDBManagerConfiguration.Builder().build();
    }

    public MariaDBManagerImpl(final MariaDBManagerConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void waitForConnectivity() {
        Spacelift.task(configuration, MariaDBStartedCheckTask.class).execute().until(
                new CountDownWatch(configuration.getStartupTimeoutInSeconds(), SECONDS),
                new BasicWaitingCondition());
    }

    @Override
    public void executeScript(final Reader reader) throws MariaDBManagerException {
        Connection connection = getConnection();
        executeScript(connection, reader);
        closeConnection(connection);
    }

    @Override
    public void executeScript(String sqlScript) throws MariaDBManagerException {
        executeScript(new File(sqlScript));
    }

    @Override
    public void executeScript(File sqlScript) throws MariaDBManagerException {
        try (final Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sqlScript)))) {
            executeScript(reader);
        } catch (IOException ex) {
            throw new MariaDBManagerException(String.format("Unable to find SQL script file to execute: %s. ",
                    sqlScript.getAbsolutePath()), ex);
        }
    }

    @Override
    public void executeScript(Connection connection, String sqlScript) throws MariaDBManagerException {
        executeScript(connection, new File(sqlScript));
    }

    @Override
    public void executeScript(Connection connection, File sqlScript) throws MariaDBManagerException {
        try (final Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sqlScript)))) {
            executeScript(connection, reader);
        } catch (IOException ex) {
            throw new MariaDBManagerException(String.format("Unable to find SQL script file to execute: %s",
                    sqlScript.getAbsolutePath()), ex);
        }
    }

    @Override
    public void executeScript(final Connection connection, final Reader reader) throws MariaDBManagerException {
        Validate.notNull(connection, "Connection to execute script on must not be a null object!");
        Validate.notNull(reader, "Reader to read SQL script must not be a null object!");

        final ScriptRunner scriptRunner = new ScriptRunner(connection, false, false);

        try {
            scriptRunner.runScript(reader);
        } catch (IOException | SQLException ex) {
            throw new MariaDBManagerException(String.format("Unable to execute SQL script: %s", ex.getMessage()), ex);
        }
    }

    @Override
    public Connection getConnection() {
        return getConnection(configuration.getDatabase());
    }

    @Override
    public Flyway flyway(String scheme) {
        return flyway(configuration.getDataSource(), scheme, null);
    }

    @Override
    public Flyway flyway(String dataSource, String scheme) {
        return flyway(dataSource, scheme, null);
    }

    @Override
    public Flyway flyway(String dataSource, String scheme, String migrationLocation, String... migrationLocations) {
        final Flyway flyway = new Flyway();

        final String datasource = configuration.getDataSource();
        final String username = configuration.getUsername();
        final String password = configuration.getPassword();

        flyway.setDataSource(datasource, username, password);
        flyway.setSchemas(scheme);

        final List<String> migrations = new ArrayList<>();

        if (migrationLocation != null) {
            migrations.add(migrationLocation);
        }

        for (String migration : migrationLocations) {
            if (migration != null) {
                migrations.add(migration);
            }
        }

        if (!migrations.isEmpty()) {
            flyway.setLocations(migrations.toArray(new String[migrations.size()]));
        }

        return flyway;
    }

    @Override
    public Connection getConnection(String database) throws MariaDBManagerException {
        Connection connection;

        final String connectionString = configuration.getConnectionString(database);

        try {
            connection = DriverManager.getConnection(connectionString, configuration.getUsername(), configuration.getPassword());
        } catch (SQLException ex) {
            throw new MariaDBManagerException(String.format("Unable to get connection to %s.", connectionString), ex);
        }

        return connection;
    }

    @Override
    public void closeConnection(Connection connection) {
        Validate.notNull(connection, "Connection to close must not be a null object!");

        try {
            connection.close();
        } catch (SQLException ex) {
            logger.warn("Unable to close SQL connection. Error code: {}. Message: {}.", ex.getErrorCode(), ex.getMessage());
        }
    }

    @Override
    public void closeStatement(Statement statement) {
        Validate.notNull(statement, "Statement to close must not be a null object!");

        try {
            statement.close();
        } catch (SQLException ex) {
            logger.warn("Unable to close SQL statement. Error code: {}. Message: {}.", ex.getErrorCode(), ex.getMessage());
        }
    }

    @Override
    public MariaDBManagerConfiguration getConfiguration() {
        return configuration;
    }
}
