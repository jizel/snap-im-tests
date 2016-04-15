package travel.snapshot.qa.manager.activemq.configuration;

import travel.snapshot.qa.manager.api.configuration.AbstractConfigurationBuilder;
import travel.snapshot.qa.manager.api.configuration.Configuration;

public class ActiveMQManagerConfiguration implements Configuration {

    private final int startupTimeoutInSeconds;

    private final String brokerAddress;

    private final int brokerPort;

    private final String username;

    private final String password;

    private ActiveMQManagerConfiguration(final Builder builder) {
        this.startupTimeoutInSeconds = builder.startupTimeoutInSeconds;
        this.brokerAddress = builder.brokerAddress;
        this.brokerPort = builder.brokerPort;
        this.username = builder.username;
        this.password = builder.password;
    }

    public int getStartupTimeoutInSeconds() {
        return startupTimeoutInSeconds;
    }

    public String getBrokerAddress() {
        return brokerAddress;
    }

    public int getBrokerPort() {
        return brokerPort;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static class Builder extends AbstractConfigurationBuilder {

        private int startupTimeoutInSeconds = 60;

        private String brokerAddress = "127.0.0.1";

        private int brokerPort = 61616;

        private String username = "admin";

        private String password = "admin";

        public Builder() {
            this.brokerAddress = resolveHostIp();
        }

        /**
         * @param brokerAddress address where broker is listening, defauls to {@literal 127.0.0.1} when not set.
         * @return this
         */
        public Builder brokerAddress(final String brokerAddress) {
            this.brokerAddress = brokerAddress;
            return this;
        }

        /**
         * @param brokerPort port where ActiveMQ broker is bound, defaults to {@literal 61616} when not set.
         * @return this
         */
        public Builder brokerPort(final int brokerPort) {
            validatePort(brokerPort);
            this.brokerPort = brokerPort;
            return this;
        }

        /**
         * @param startupTimeoutInSeconds time after which connection attempt is considered to be failed
         * @return this
         */
        public Builder startupTimeoutInSeconds(final int startupTimeoutInSeconds) {
            this.startupTimeoutInSeconds = startupTimeoutInSeconds;
            return this;
        }

        /**
         * Sets username to use during connection establishment. When not set, defaults to {@literal admin}.
         *
         * @param username user name to use during connection establishment
         * @return this
         */
        public Builder username(String username) {
            this.username = username;
            return this;
        }

        /**
         * Sets password to use during connection establishment. When not set, defaults to {@literal admin}.
         *
         * @param password password name to use during connection establishment
         * @return this
         */
        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public ActiveMQManagerConfiguration build() {
            return new ActiveMQManagerConfiguration(this);
        }
    }
}
