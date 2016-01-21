package travel.snapshot.qa.manager.mongodb.configuration;

import travel.snapshot.qa.manager.api.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MongoDBManagerConfiguration implements Configuration {

    private final List<MongoHostPortPair> servers;

    private final String database;

    private final int startupTimeoutInSeconds;

    private MongoDBManagerConfiguration(Builder builder) {
        this.servers = builder.servers;
        this.database = builder.database;
        this.startupTimeoutInSeconds = builder.startupTimeoutInSeconds;
    }

    public int getStartupTimeoutInSeconds() {
        return startupTimeoutInSeconds;
    }

    public String getDatabase() {
        return database;
    }

    public List<MongoHostPortPair> getServers() {
        return servers;
    }

    public String getConnectionString() {
        return this.servers.stream().map(server -> server.toString()).collect(Collectors.joining(","));
    }

    public static class Builder {

        private static final String DEFAULT_HOST = "127.0.0.1";

        private static final int DEFAULT_PORT = 27017;

        private static List<MongoHostPortPair> servers = new ArrayList<MongoHostPortPair>() {{
            add(new MongoHostPortPair(DEFAULT_HOST, DEFAULT_PORT));
        }};

        private String database = "test";

        private int startupTimeoutInSeconds = 60;

        public Builder setServer(final MongoHostPortPair mongoHostPortPair) {
            this.servers.clear();
            this.servers.add(mongoHostPortPair);
            return this;
        }

        public Builder addServer(final MongoHostPortPair mongoHostPortPair) {
            this.servers.add(mongoHostPortPair);
            return this;
        }

        public Builder database(final String database) {
            this.database = database;
            return this;
        }

        public Builder startupTimeoutInSeconds(int startupTimeoutInSeconds) {
            this.startupTimeoutInSeconds = startupTimeoutInSeconds;
            return this;
        }

        public MongoDBManagerConfiguration build() {
            return new MongoDBManagerConfiguration(this);
        }
    }

    public static class MongoHostPortPair {

        public final String host;
        public final int port;

        public MongoHostPortPair(final String host, final int port) {
            this.host = host;
            this.port = port;
        }

        @Override
        public String toString() {
            return host + ":" + port;
        }
    }
}
