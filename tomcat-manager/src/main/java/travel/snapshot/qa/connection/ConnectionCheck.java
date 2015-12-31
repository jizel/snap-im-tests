package travel.snapshot.qa.connection;

public class ConnectionCheck {

    private final Protocol protocol;

    private final String host;

    private final int port;

    private final long timeout;

    private ConnectionCheck(Builder builder) {
        this.protocol = builder.protocol;
        this.host = builder.host;
        this.port = builder.port;
        this.timeout = builder.timeout;
    }

    public void execute() {
        new ConnectionCheckExecutor().execute(this);
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public long getTimeout() {
        return timeout;
    }

    public static final class Builder {

        private Protocol protocol = Protocol.TCP;

        private String host = "127.0.0.1";

        private int port = 8080;

        private long timeout = 60; // in seconds

        public Builder protocol(Protocol protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        /**
         * @param timeout timeout after which the check is considered unsuccessful
         * @return this builder
         * @throws IllegalArgumentException if {@code timeout} is lower then 1.
         */
        public Builder timeout(long timeout) {
            if (timeout > 0) {
                this.timeout = timeout;
                return this;
            }

            throw new IllegalArgumentException("Specified timeout was lower then 1");
        }

        public ConnectionCheck build() {
            return new ConnectionCheck(this);
        }
    }
}
