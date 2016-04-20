package travel.snapshot.qa.manager.generic.configuration;

import travel.snapshot.qa.connection.Protocol;
import travel.snapshot.qa.manager.api.configuration.AbstractConfigurationBuilder;
import travel.snapshot.qa.manager.api.configuration.Configuration;
import travel.snapshot.qa.manager.api.configuration.ConfigurationException;

/**
 * Configuration for generic services which does not offer any API to client to call and whole interaction with this
 * container will be solely based just on start and stop of a container.
 */
public class GenericConfiguration implements Configuration {

    private final String bindAddress;

    private final int bindPort;

    private final long startupTimeoutInSeconds;

    private final Protocol protocol;

    private GenericConfiguration(final Builder builder) {
        this.bindAddress = builder.bindAddress;
        this.bindPort = builder.bindPort;
        this.startupTimeoutInSeconds = builder.startupTimeoutInSeconds;
        this.protocol = builder.protocol;
    }

    /**
     * @return address where some service is bound
     */
    public String getBindAddress() {
        return bindAddress;
    }

    /**
     * @return port where some service is bound
     */
    public int getBindPort() {
        return bindPort;
    }

    /**
     * @return startup timeout in seconds this service has to fully initialize itself
     */
    public long getStartupTimeoutInSeconds() {
        return startupTimeoutInSeconds;
    }

    /**
     * @return protocol some generic service is running on
     */
    public Protocol getProtocol() {
        return protocol;
    }

    public static class Builder extends AbstractConfigurationBuilder {

        private String bindAddress = "127.0.0.1";

        private int bindPort = 8080;

        private long startupTimeoutInSeconds = 60;

        private Protocol protocol = Protocol.TCP;

        public Builder() {
            bindAddress = resolveHostIp();
        }

        /**
         * @param bindAddress sets bind address of some generic service, defaults to "127.0.0.1"
         * @return this
         */
        public Builder setBindAddress(String bindAddress) {
            this.bindAddress = bindAddress;
            return this;
        }

        /**
         * @param bindPort sets bind port of some generic service, defaults to 8080.
         * @return this
         */
        public Builder setBindPort(int bindPort) {
            this.bindPort = bindPort;
            return this;
        }

        /**
         * @param startupTimeoutInSeconds startup timeout in seconds to set, defaults to 60 seconds.
         * @return this
         */
        public Builder setStartupTimeoutInSeconds(long startupTimeoutInSeconds) {
            this.startupTimeoutInSeconds = startupTimeoutInSeconds;
            return this;
        }

        /**
         * @param protocol protocol some generic service is running on
         * @return this
         */
        public Builder setProtocol(Protocol protocol) {
            this.protocol = protocol;
            return this;
        }

        /**
         * Builds generic configuration.
         *
         * @return generic configuration
         * @throws ConfigurationException in case bind port is out of range or startup timeout  is lower than 0.
         */
        public GenericConfiguration build() throws ConfigurationException {
            validatePort(bindPort);

            if (startupTimeoutInSeconds <= 0) {
                throw new ConfigurationException(
                        String.format("Startup timeout in seconds (%s) can not be lower than 0.", startupTimeoutInSeconds));
            }

            return new GenericConfiguration(this);
        }
    }
}
