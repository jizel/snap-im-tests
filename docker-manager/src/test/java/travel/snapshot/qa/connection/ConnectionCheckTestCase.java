package travel.snapshot.qa.connection;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import travel.snapshot.qa.category.UnitTest;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

@Category(UnitTest.class)
public class ConnectionCheckTestCase {

    private static final int TCP_SERVER_PORT = 8081;

    private static final int UDP_SERVER_PORT = 8082;

    private static final int INACTIVE_PORT = 8083;

    private static final String HOST = "127.0.0.1";

    private static TCPServer tcpServer;

    private static UDPServer udpServer;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void setup() {
        tcpServer = new TCPServer();
        tcpServer.start();

        udpServer = new UDPServer();
        udpServer.start();
    }

    @AfterClass
    public static void teardown() throws Exception {
        tcpServer.close();
        udpServer.close();
    }

    @Test
    public void testTCPConnection() throws Exception {

        new ConnectionCheck.Builder()
                .host(HOST)
                .port(TCP_SERVER_PORT)
                .protocol(Protocol.TCP)
                .build()
                .execute();
    }

    @Test
    public void testUDPConnection() throws Exception {
        new ConnectionCheck.Builder()
                .host(HOST)
                .port(UDP_SERVER_PORT)
                .protocol(Protocol.UDP)
                .build().execute();
    }

    @Test
    public void invalidTCPConnection() throws Exception {
        expectedException.expect(ConnectionCheckException.class);
        expectedException.expectMessage(String.format("Unable to connect in %s seconds.", 5));

        new ConnectionCheck.Builder()
                .host(HOST)
                .port(INACTIVE_PORT)
                .protocol(Protocol.TCP)
                .timeout(5)
                .build().execute();
    }

    private static final class TCPServer extends Thread {

        private ServerSocket serverSocket = null;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket();
                serverSocket.bind(new InetSocketAddress(Inet4Address.getByName(HOST), TCP_SERVER_PORT));

                serverSocket.accept();
            } catch (IOException ex) {
                // intentionally empty
            } finally {
                IOUtils.closeQuietly(serverSocket);
            }
        }

        public void close() {
            IOUtils.closeQuietly(serverSocket);
        }
    }

    private static final class UDPServer extends Thread {

        private DatagramSocket serverSocket;

        @Override
        public void run() {
            try {
                serverSocket = new DatagramSocket(UDP_SERVER_PORT, InetAddress.getByName(HOST));
                final byte[] buffer = new byte[256];
                final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                serverSocket.receive(packet);
            } catch (IOException ex) {
                // intentionally empty
            } finally {
                IOUtils.closeQuietly(serverSocket);
            }
        }

        public void close() {
            IOUtils.closeQuietly(serverSocket);
        }
    }
}
