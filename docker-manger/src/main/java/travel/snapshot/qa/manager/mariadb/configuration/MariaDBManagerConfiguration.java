package travel.snapshot.qa.manager.mariadb.configuration;

import travel.snapshot.qa.manager.api.Configuration;

public class MariaDBManagerConfiguration implements Configuration {

    private final String username;

    private final String password;

    private final String bindAddress;

    private final int bindPort;

    private final String database;

    private final long startupTimeoutInSeconds;

    private MariaDBManagerConfiguration(Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this.bindAddress = builder.bindAddress;
        this.bindPort = builder.bindPort;
        this.database = builder.database;
        this.startupTimeoutInSeconds = builder.startupTimeoutInSeconds;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getBindAddress() {
        return bindAddress;
    }

    public int getBindPort() {
        return bindPort;
    }

    public String getDatabase() {
        return database;
    }

    /**
     * Gets JDBC connection string which uses already set host, port and database.
     *
     * @return connection string with previously set host, port and database
     */
    public String getConnectionString() {
        return getConnectionString(database);
    }

    /**
     * Gets JDBC connection string which uses already set address and port with specified database.
     *
     * @param database database to construct the connection string with
     * @return connection string with default host and port and specified database
     */
    public String getConnectionString(final String database) {
        return String.format("jdbc:mysql://%s:%s/%s", bindAddress, bindPort, database);
    }

    public long getStartupTimeoutInSeconds() {
        return startupTimeoutInSeconds;
    }

    public static class Builder {

        private String username = "root";

        private String password = "root";

        private String bindAddress = "127.0.0.1";

        private int bindPort = 3306;

        private String database = "test";

        private long startupTimeoutInSeconds = 60l;

        public MariaDBManagerConfiguration build() {
            return new MariaDBManagerConfiguration(this);
        }

        /**
         * @param username name of a user to use when connecting to MariaDB. Defaults to {@literal root}
         * @return this
         */
        public Builder username(final String username) {
            this.username = username;
            return this;
        }

        /**
         * @param password password of a user to use when connecting to MariaDB. Defaults to {@literal root}
         * @return this
         */
        public Builder password(final String password) {
            this.password = password;
            return this;
        }

        /**
         * @param bindAddress address of a MariaDB instance to interact with, defaults to {@literal 127.0.0.1}
         * @return this
         */
        public Builder bindAddress(final String bindAddress) {
            this.bindAddress = bindAddress;
            return this;
        }

        /**
         * @param bindPort port of a MariaDB instance to interact with, defaults to {@literal 3306}.
         * @return this
         */
        public Builder bindPort(final int bindPort) {
            this.bindPort = bindPort;
            return this;
        }

        /**
         * Sets the database name to interact with.
         *
         * @param database database name of a MariaDB instance to interact with, defauls to {@literal test}.
         * @return this
         */
        public Builder database(final String database) {
            this.database = database;
            return this;
        }

        /**
         * @param startupTimeoutInSeconds timeout value, defaults to {@literal 60}.
         * @return this
         */
        public Builder startupTimeoutInSeconds(final long startupTimeoutInSeconds) {
            this.startupTimeoutInSeconds = startupTimeoutInSeconds;
            return this;
        }
    }
}
