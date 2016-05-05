package travel.snapshot.qa.connection;

import static org.junit.Assert.assertTrue;
import static travel.snapshot.qa.connection.Protocol.TCP;
import static travel.snapshot.qa.connection.Protocol.UDP;

import org.apache.commons.io.IOUtils;
import org.arquillian.spacelift.Spacelift;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Before
    public void setup() throws Exception {
        tcpServer = new TCPServer();
        tcpServer.start();

        udpServer = new UDPServer(HOST, UDP_SERVER_PORT);
        udpServer.start();
    }

    @After
    public void teardown() throws Exception {
        tcpServer.close();
        udpServer.close();
    }

    @Test
    public void testTCPConnection() throws Exception {
        new ConnectionCheck.Builder()
                .host(HOST)
                .port(TCP_SERVER_PORT)
                .protocol(TCP)
                .timeout(10)
                .reexecutionInterval(5)
                .build()
                .execute();
    }

    @Test
    public void testUDPConnection() throws Exception {
        new ConnectionCheck.Builder()
                .host(HOST)
                .port(UDP_SERVER_PORT)
                .protocol(UDP)
                .timeout(10)
                .reexecutionInterval(5)
                .build()
                .execute();
    }

    @Test
    public void testValidTCPConnectionCheckTask() throws Exception {
        ConnectionCheck tcpConnectionCheck = new ConnectionCheck.Builder()
                .host(HOST)
                .port(TCP_SERVER_PORT)
                .protocol(TCP)
                .build();

        assertTrue(Spacelift.task(ConnectionCheckExecutor.TCPConnectionCheckTask.class)
                .connectionCheck(tcpConnectionCheck)
                .execute()
                .await());
    }

    @Test
    public void testValidUDPConnectionCheckTask() throws Exception {
        ConnectionCheck udpConnectionCheck = new ConnectionCheck.Builder()
                .host(HOST)
                .port(UDP_SERVER_PORT)
                .protocol(UDP)
                .build();

        assertTrue(Spacelift.task(ConnectionCheckExecutor.UDPConnectionCheckTask.class)
                .connectionCheck(udpConnectionCheck)
                .execute()
                .await());
    }

    @Test
    public void testInvalidUDPConnection() throws Exception {
        expectedException.expect(ConnectionCheckException.class);
        expectedException.expectMessage(String.format("Unable to connect in %s seconds.", 5));

        new ConnectionCheck.Builder()
                .host(HOST)
                .port(INACTIVE_PORT)
                .protocol(UDP)
                .timeout(5)
                .build().execute();
    }

    @Test
    public void testInvalidTCPConnection() throws Exception {
        expectedException.expect(ConnectionCheckException.class);
        expectedException.expectMessage(String.format("Unable to connect in %s seconds.", 5));

        new ConnectionCheck.Builder()
                .host(HOST)
                .port(INACTIVE_PORT)
                .protocol(TCP)
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
                close();
            }
        }

        public void close() {
            IOUtils.closeQuietly(serverSocket);
        }
    }

    private static final class UDPServer extends Thread {

        private static final Logger logger = LoggerFactory.getLogger(UDPServer.class);

        private DatagramSocket serverSocket;

        final byte[] buffer = new byte[256];

        final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        public UDPServer(String host, int port) throws IOException {
            serverSocket = new DatagramSocket(port, InetAddress.getByName(host));
        }

        @Override
        public void run() {
            try {
                while (true) {
                    receive(packet);
                    send(packet);
                }

            } catch (IOException ex) {
                close();
            }
        }

        public void close() {
            IOUtils.closeQuietly(serverSocket);
        }

        private void receive(DatagramPacket packet) throws IOException {
            serverSocket.receive(packet);
            logger.info("UDP server received a packet from {}:{}", packet.getAddress(), packet.getPort());
        }

        private void send(DatagramPacket packet) throws IOException {
            DatagramPacket clientPacket = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());
            logger.info("UDP server sending a packet to {}:{}", clientPacket.getAddress(), clientPacket.getPort());
            serverSocket.send(clientPacket);
        }
    }
}
