package travel.snapshot.qa.connection;

import org.apache.commons.io.IOUtils;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.execution.ExecutionCondition;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.task.Task;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class ConnectionCheckExecutor {

    private static final ExecutionCondition<Boolean> CONNECTION_ESTABLISHED_CONDITION = new ConnectionCheckExecutor.ConnectionEstablishedCondition();

    private static final long REEXECUTION_INTERVAL = 3;

    public void execute(ConnectionCheck check) throws ConnectionCheckException {

        final Class<? extends Task<ConnectionCheck, Boolean>> checkTask = getCheckTask(check.getProtocol());

        try {
            Spacelift.task(check, checkTask)
                    .execute()
                    .reexecuteEvery(REEXECUTION_INTERVAL, TimeUnit.SECONDS)
                    .until(check.getTimeout(), TimeUnit.SECONDS, CONNECTION_ESTABLISHED_CONDITION);
        } catch (ExecutionException ex) {
            throw new ConnectionCheckException(String.format("Unable to connect to %s:%s on %s in %s seconds.",
                    check.getHost(), check.getPort(), check.getProtocol(), check.getTimeout()),
                    ex);
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

    private static class ConnectionEstablishedCondition implements ExecutionCondition<Boolean> {

        @Override
        public boolean satisfiedBy(Boolean connected) throws ExecutionException {
            return connected;
        }
    }
}
