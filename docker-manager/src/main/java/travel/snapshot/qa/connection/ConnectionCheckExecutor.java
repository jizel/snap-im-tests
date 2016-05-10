package travel.snapshot.qa.connection;

import org.apache.commons.io.IOUtils;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.manager.api.BasicWaitingCondition;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Executes {@link ConnectionCheck}, possibly with given checking task. Connection check is executed periodically up to
 * {@link ConnectionCheck#getTimeout()} seconds, repeating check every {@link ConnectionCheck#getReexecutionInterval()}
 * seconds.
 */
public class ConnectionCheckExecutor {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionCheckExecutor.class);

    /**
     * Executes the connection check.
     *
     * @param check check to execute
     * @throws ConnectionCheckException if it is not possible to perform connection check in a successful manner
     */
    public void execute(final ConnectionCheck check) throws ConnectionCheckException {

        final Task<?, Boolean> setCheckingTask = check.getCheckingTask();

        if (setCheckingTask != null) {
            execute(setCheckingTask, check.getTimeout(), check.getReexecutionInterval());
        } else {
            execute(Spacelift.task(check, getCheckTask(check.getProtocol())), check.getTimeout(), check.getReexecutionInterval());
        }
    }

    private void execute(final Task<?, Boolean> checkingTask, final long timeout, final long reexecutionInterval) {
        try {
            checkingTask.execute()
                    .reexecuteEvery(reexecutionInterval, TimeUnit.SECONDS)
                    .until(timeout, TimeUnit.SECONDS, new BasicWaitingCondition());
        } catch (ExecutionException ex) {
            throw new ConnectionCheckException(String.format("Unable to connect in %s seconds.", timeout), ex);
        }
    }

    private Class<? extends Task<ConnectionCheck, Boolean>> getCheckTask(final Protocol protocol) {
        switch (protocol) {
            case TCP:
                return TCPConnectionCheckTask.class;
            case UDP:
                return UDPConnectionCheckTask.class;
            default:
                throw new IllegalStateException("Unable to resolve connection check task.");
        }
    }

    /**
     * Checks connection by trying to open a socket to the other side.
     */
    public static final class TCPConnectionCheckTask extends Task<ConnectionCheck, Boolean> {

        private ConnectionCheck connectionCheck;

        public TCPConnectionCheckTask() {
            if (getExecutionService() == null) {
                setExecutionService(Spacelift.service());
            }
        }

        /**
         * Sets connection check. In case there is some connection check which is chained, that connection check will be
         * used instead of this one.
         *
         * @param connectionCheck connection check to set when not chained
         * @return this
         */
        public TCPConnectionCheckTask connectionCheck(final ConnectionCheck connectionCheck) {
            this.connectionCheck = connectionCheck;
            return this;
        }

        @Override
        protected Boolean process(final ConnectionCheck connectionCheck) throws Exception {

            ConnectionCheck resolvedConnectionCheck;

            if (connectionCheck == null) {
                resolvedConnectionCheck = this.connectionCheck;
            } else {
                resolvedConnectionCheck = connectionCheck;
            }

            if (resolvedConnectionCheck == null) {
                throw new IllegalStateException("Connection check must not be a null object.");
            }

            Socket tcpClient = null;

            try {
                tcpClient = new Socket(resolvedConnectionCheck.getHost(), resolvedConnectionCheck.getPort());
                return true;
            } catch (IOException ex) {
                logger.debug("Unable to make TCP connection to {}:{}", resolvedConnectionCheck.getHost(), resolvedConnectionCheck.getPort());
                return false;
            } finally {
                IOUtils.closeQuietly(tcpClient);
            }
        }
    }

    /**
     * Checks "connection" (even UDP is connection-less in its nature) by trying to sent a DatagramPacket to the other
     * side. After packet is sent, this check wait for any response.
     *
     * This check can be used only in connection with UDP services which do return something back to client.
     */
    public static final class UDPConnectionCheckTask extends Task<ConnectionCheck, Boolean> {

        private ConnectionCheck connectionCheck;

        public UDPConnectionCheckTask() {
            if (getExecutionService() == null) {
                setExecutionService(Spacelift.service());
            }
        }

        /**
         * Sets connection check. In case there is some connection check which is chained, that connection check will be
         * used instead of this one.
         *
         * @param connectionCheck connection check to set when not chained
         * @return this
         */
        public UDPConnectionCheckTask connectionCheck(final ConnectionCheck connectionCheck) {
            this.connectionCheck = connectionCheck;
            return this;
        }

        @Override
        protected Boolean process(final ConnectionCheck connectionCheck) throws Exception {

            ConnectionCheck resolvedConnectionCheck;

            if (connectionCheck == null) {
                resolvedConnectionCheck = this.connectionCheck;
            } else {
                resolvedConnectionCheck = connectionCheck;
            }

            if (resolvedConnectionCheck == null) {
                throw new IllegalStateException("Connection check must not be a null object.");
            }

            final DatagramSocket udpClient = new DatagramSocket();

            try {
                // send data to server
                final byte[] data = "hello".getBytes(StandardCharsets.UTF_8.toString());
                final InetAddress address = InetAddress.getByName(resolvedConnectionCheck.getHost());
                final DatagramPacket packet = new DatagramPacket(data, data.length, address, resolvedConnectionCheck.getPort());

                logger.debug("Request to server {}:{} sent.", address.getHostName(), resolvedConnectionCheck.getPort());
                udpClient.send(packet);

                // wait for response until timout
                udpClient.setSoTimeout(3000);
                byte[] buffer = new byte[256];
                udpClient.receive(new DatagramPacket(buffer, buffer.length));

                logger.debug("Request from server {}:{} received.", address.getHostName(), resolvedConnectionCheck.getPort());

                return true;
            } catch (SocketTimeoutException ex) {
                logger.info("Response was not received before timeout.");
                return false;
            } catch (IOException ex) {
                logger.info("Unable to make UDP connection to {}:{}. {}", resolvedConnectionCheck.getHost(), resolvedConnectionCheck.getPort());
                return false;
            } finally {
                IOUtils.closeQuietly(udpClient);
            }
        }
    }
}
