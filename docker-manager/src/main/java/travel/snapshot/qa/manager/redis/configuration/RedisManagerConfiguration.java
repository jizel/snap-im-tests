package travel.snapshot.qa.manager.redis.configuration;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import travel.snapshot.qa.manager.api.configuration.AbstractConfigurationBuilder;
import travel.snapshot.qa.manager.api.configuration.Configuration;

public class RedisManagerConfiguration implements Configuration {

    private final String bindAddress;

    private final int bindPort;

    private final int connectionTimeout;

    private final int soTimeout;

    private final String password;

    private final int database;

    private final String clientName;

    private final JedisPoolConfig jedisPoolConfig;

    private final long startupTimeoutInSeconds;

    private RedisManagerConfiguration(Builder builder) {
        this.bindAddress = builder.bindAddress;
        this.bindPort = builder.bindPort;
        this.connectionTimeout = builder.connectionTimeout;
        this.soTimeout = builder.soTimeout;
        this.password = builder.password;
        this.database = builder.database;
        this.clientName = builder.clientName;
        this.jedisPoolConfig = builder.jedisPoolConfig;
        this.startupTimeoutInSeconds = builder.startupTimeoutInSeconds;
    }

    public String getBindAddress() {
        return bindAddress;
    }

    public int getBindPort() {
        return bindPort;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public String getPassword() {
        return password;
    }

    public int getDatabase() {
        return database;
    }

    public String getClientName() {
        return clientName;
    }

    public JedisPoolConfig getJedisPoolConfig() {
        return jedisPoolConfig;
    }

    public long getStartupTimeoutInSeconds() {
        return startupTimeoutInSeconds;
    }

    public static class Builder extends AbstractConfigurationBuilder {

        private String bindAddress;

        private int bindPort = Protocol.DEFAULT_PORT;

        private int connectionTimeout = Protocol.DEFAULT_TIMEOUT;

        private int soTimeout = Protocol.DEFAULT_TIMEOUT;

        private String password;

        private int database = Protocol.DEFAULT_DATABASE;

        private String clientName;

        public JedisPoolConfig jedisPoolConfig;

        public long startupTimeoutInSeconds = 30;

        public Builder() {
            bindAddress = resolveHostIp();
            jedisPoolConfig = new JedisPoolConfig();
        }

        public void setBindAddress(String bindAddress) {
            this.bindAddress = bindAddress;
        }

        public Builder setConnectionTimeout(int connectionTimeout) {
            validateTimeout(connectionTimeout);
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public Builder setSoTimeout(int soTimeout) {
            validateTimeout(soTimeout);
            this.soTimeout = soTimeout;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setDatabase(int database) {
            this.database = database;
            return this;
        }

        public Builder setClientName(String clientName) {
            this.clientName = clientName;
            return this;
        }

        public Builder setJedisPoolConfig(JedisPoolConfig jedisPoolConfig) {
            this.jedisPoolConfig = jedisPoolConfig;
            return this;
        }

        public Builder getStartupTimeoutInSeconds(long startupTimeoutInSeconds) {
            validateTimeout(startupTimeoutInSeconds);
            this.startupTimeoutInSeconds = startupTimeoutInSeconds;
            return this;
        }

        private void validateTimeout(long connectionTimeout) {
            if (connectionTimeout <= 0) {
                throw new IllegalArgumentException("Value for timeout has to be bigger than 0.");
            }
        }

        public RedisManagerConfiguration build() {
            return new RedisManagerConfiguration(this);
        }
    }
}
