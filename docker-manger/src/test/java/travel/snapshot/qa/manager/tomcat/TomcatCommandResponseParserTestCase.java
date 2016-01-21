package travel.snapshot.qa.manager.tomcat;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import travel.snapshot.qa.manager.tomcat.api.response.TomcatResponse;
import travel.snapshot.qa.manager.tomcat.api.response.TomcatResponseParser;
import travel.snapshot.qa.manager.tomcat.api.response.TomcatResponseStatus;

import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class TomcatCommandResponseParserTestCase {

    @Test
    public void testResponseParser() {

        List<String> lines = new ArrayList<String>() {{
            add("OK - some - response");
            add("deployment1");
            add("some othere string");
        }};

        TomcatResponseParser parser = new TomcatResponseParser();
        TomcatResponse response = parser.parse(lines);

        Assert.assertEquals(TomcatResponseStatus.OK, response.getResponseHeader().getResponseStatus());
        Assert.assertEquals(Boolean.TRUE, response.isOk());
        Assert.assertEquals("some - response", response.getResponseHeader().getResponseReason().getResponseReason());
        Assert.assertEquals(2, response.getResponseBody().getBody().size());
    }
}
