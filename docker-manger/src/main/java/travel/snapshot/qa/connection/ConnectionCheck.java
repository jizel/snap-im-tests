package travel.snapshot.qa.connection;

import org.arquillian.spacelift.task.Task;

/**
 * Connection check represents a way how to be sure that some underlying service we are depending on is started and
 * considered fully up and running and it is save to proceed to the interaction with it safely.
 */
public class ConnectionCheck {

    private final Task<?, Boolean> checkingTask;

    private final Protocol protocol;

    private final String host;

    private final int port;

    private final long timeout;

    private final long reexecutionInterval;

    private ConnectionCheck(Builder builder) {
        this.protocol = builder.protocol;
        this.host = builder.host;
        this.port = builder.port;
        this.timeout = builder.timeout;
        this.reexecutionInterval = builder.reexecutionInterval;
        this.checkingTask = builder.checkingTask;
    }

    /**
     * Executes built check
     */
    public void execute() {
        new ConnectionCheckExecutor().execute(this);
    }

    public Task<?, Boolean> getCheckingTask() {
        return checkingTask;
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

    public long getReexecutionInterval() {
        return reexecutionInterval;
    }

    public static final class Builder {

        private Task<?, Boolean> checkingTask;

        private Protocol protocol = Protocol.TCP;

        private String host = "127.0.0.1";

        private int port = 8080;

        private long timeout = 60; // in seconds

        private long reexecutionInterval = 3; // in seconds

        public Builder() {
            this(null);
        }

        /**
         * Sets connection task which performs the check. When not set, simple TCP (or UDP) check is executed which
         * simply checks if it is possible to open a socket to the other side. Such connection is considered successful
         * however, in a lot of cases, it is not sure if the service is fully up and running. For example Docker does
         * port binding and it is possible to open a socket there but it does not mean that the service is fully
         * listening. For that reason, given check is service-specific and performs some action against the service
         * which is successful iff service is truly up.
         *
         * @param checkingTask task which performs the check.
         */
        public Builder(Task<?, Boolean> checkingTask) {
            this.checkingTask = checkingTask;
        }

        /**
         * Sets the protocol via which the check will be carried out. When not set, defaults to TCP.
         *
         * @param protocol protocol to perform the check against. When not set, defaults to 127.0.0.1.
         * @return this builder
         */
        public Builder protocol(Protocol protocol) {
            this.protocol = protocol;
            return this;
        }

        /**
         * Sets the host to perform the check against. When not set, defaults to 127.0.0.1.
         *
         * @param host host to perform the check against
         * @return this builder
         */
        public Builder host(String host) {
            this.host = host;
            return this;
        }

        /**
         * Sets the port to perform the check against. When not set, defaults to 8080.
         *
         * @param port port to perform the check against
         * @return this builder
         */
        public Builder port(int port) {
            this.port = port;
            return this;
        }

        /**
         * Sets timeout as a time in seconds after which a check is considered to be failed.
         *
         * @param timeout timeout after which the check is considered unsuccessful
         * @return this builder
         * @throws IllegalArgumentException if {@code timeout} is lower then 1.
         */
        public Builder timeout(long timeout) {
            if (timeout > 0) {
                this.timeout = timeout;
                return this;
            }

            throw new IllegalArgumentException(String.format("Specified timeout was lower then 1: %s", timeout));
        }

        /**
         * Sets reexecution interval as time in seconds after which a check is executed again
         *
         * @param reexecutionInterval interval after which a check is executed again
         * @return this builder
         * @throws IllegalArgumentException if {@code reexecutionInterval} is lower then 1.
         */
        public Builder reexecutionInterval(long reexecutionInterval) {
            if (reexecutionInterval > 0) {
                this.reexecutionInterval = reexecutionInterval;
                return this;
            }

            throw new IllegalArgumentException(String.format("Specified reexecutionInterval was lower then 1: %s", reexecutionInterval));
        }

        /**
         * Builds a check.
         *
         * @return built connection check
         * @throws IllegalStateException if {@code timeout} is lower then or equals to {@code reexecutionInterval}
         */
        public ConnectionCheck build() {

            if (timeout <= reexecutionInterval) {
                throw new IllegalStateException(String.format("Timeout (%s) is lower or equals to reexecution " +
                        "interval (%s).", timeout, reexecutionInterval));
            }

            return new ConnectionCheck(this);
        }
    }
}
