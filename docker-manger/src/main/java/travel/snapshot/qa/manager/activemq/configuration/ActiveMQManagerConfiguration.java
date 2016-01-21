package travel.snapshot.qa.manager.activemq.configuration;

import travel.snapshot.qa.manager.api.Configuration;

public class ActiveMQManagerConfiguration implements Configuration {

    private final int startupTimeoutInSeconds;

    private final String brokerAddress;

    private final int brokerPort;

    private ActiveMQManagerConfiguration(final Builder builder) {
        this.brokerAddress = builder.brokerAddress;
        this.brokerPort = builder.brokerPort;
        this.startupTimeoutInSeconds = builder.startupTimeoutInSeconds;
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

    public static class Builder {

        private String brokerAddress = "127.0.0.1";

        private int brokerPort = 5672;

        private int startupTimeoutInSeconds = 60;

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

        public ActiveMQManagerConfiguration build() {
            return new ActiveMQManagerConfiguration(this);
        }
    }
}
