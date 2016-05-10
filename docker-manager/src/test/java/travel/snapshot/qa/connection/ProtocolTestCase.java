package travel.snapshot.qa.connection;

import static org.junit.Assert.assertEquals;
import static travel.snapshot.qa.connection.Protocol.TCP;
import static travel.snapshot.qa.connection.Protocol.UDP;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import travel.snapshot.qa.category.UnitTest;

@Category(UnitTest.class)
public class ProtocolTestCase {

    @Test
    public void protocolTest() {
        assertEquals("tcp", TCP.toString());
        assertEquals("udp", UDP.toString());
    }
}
