package travel.snapshot.qa.connection;

import org.apache.commons.io.IOUtils;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.task.Task;
import travel.snapshot.qa.manager.api.BasicWaitingCondition;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Executes {@link ConnectionCheck}, possibly with given checking task. Connection check is executed periodically up to
 * {@link ConnectionCheck#getTimeout()} seconds, repeating check every {@link ConnectionCheck#getReexecutionInterval()}
 * seconds.
 */
public class ConnectionCheckExecutor {

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

    private Class<? extends Task<ConnectionCheck, Boolean>> getCheckTask(Protocol protocol) {
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

        @Override
        protected Boolean process(ConnectionCheck connectionCheck) throws Exception {

            Socket tcpClient = null;

            try {
                tcpClient = new Socket(connectionCheck.getHost(), connectionCheck.getPort());
                return true;
            } catch (IOException ex) {
                return false;
            } finally {
                IOUtils.closeQuietly(tcpClient);
            }
        }
    }

    /**
     * Checks "connection" (even UDP is connection-less in its nature) by trying to sent a DatagramPacket to the other
     * side.
     */
    public static final class UDPConnectionCheckTask extends Task<ConnectionCheck, Boolean> {

        @Override
        protected Boolean process(ConnectionCheck connectionCheck) throws Exception {

            final DatagramSocket udpClient = new DatagramSocket();

            try {
                final byte[] data = "hello".getBytes(StandardCharsets.UTF_8.toString());
                final InetAddress address = InetAddress.getByName(connectionCheck.getHost());
                final DatagramPacket packet = new DatagramPacket(data, data.length, address, connectionCheck.getPort());
                udpClient.send(packet);
                return true;
            } catch (IOException ex) {
                return false;
            } finally {
                IOUtils.closeQuietly(udpClient);
            }
        }
    }

    public static final class ConnectionCheckException extends RuntimeException {
        public ConnectionCheckException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
