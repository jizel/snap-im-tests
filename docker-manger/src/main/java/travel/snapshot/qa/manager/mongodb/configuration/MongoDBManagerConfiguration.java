package travel.snapshot.qa.manager.mongodb.configuration;

import travel.snapshot.qa.manager.api.AbstractConfigurationBuilder;
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

    public static class Builder extends AbstractConfigurationBuilder {

        private static final int DEFAULT_PORT = 27017;

        private List<MongoHostPortPair> servers;

        public Builder() {
            servers = new ArrayList<MongoHostPortPair>() {{
                add(new MongoHostPortPair(resolveHostIp(), DEFAULT_PORT));
            }};
        }

        private String database = "test";

        private int startupTimeoutInSeconds = 60;

        public Builder setServer(final MongoHostPortPair mongoHostPortPair) {
            validatePort(mongoHostPortPair.getPort());
            this.servers.clear();
            this.servers.add(mongoHostPortPair);
            return this;
        }

        public Builder addServer(final MongoHostPortPair mongoHostPortPair) {
            validatePort(mongoHostPortPair.getPort());
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
}
