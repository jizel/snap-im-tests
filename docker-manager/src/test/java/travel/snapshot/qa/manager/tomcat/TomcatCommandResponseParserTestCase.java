package travel.snapshot.qa.manager.tomcat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static travel.snapshot.qa.manager.tomcat.api.response.TomcatResponseStatus.OK;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import travel.snapshot.qa.category.UnitTest;
import travel.snapshot.qa.manager.tomcat.api.response.TomcatResponse;
import travel.snapshot.qa.manager.tomcat.api.response.TomcatResponseBody;
import travel.snapshot.qa.manager.tomcat.api.response.TomcatResponseHeader;
import travel.snapshot.qa.manager.tomcat.api.response.TomcatResponseParser;
import travel.snapshot.qa.manager.tomcat.api.response.TomcatResponseReason;
import travel.snapshot.qa.manager.tomcat.api.response.TomcatResponseStatus;

import java.util.ArrayList;
import java.util.List;

@Category(UnitTest.class)
public class TomcatCommandResponseParserTestCase {

    private static final List<String> DEPLOYMENTS = new ArrayList<String>() {{
        add("SomeDeployment-1.0:stopped:0:SomeDeployment-1.0");
        add("host-manager:running:0:host-manager");
        add("OtherDeployment-1.0:running:0:OtherDeployment-1.0");
        add("manager:running:1:manager");
    }};

    private static final List<String> LINES = new ArrayList<String>() {{
        add("OK - Listed applications for virtual host localhost");
        addAll(DEPLOYMENTS);
    }};

    private static final List<String> ONLY_HEADER = new ArrayList<String>() {{
        add("OK - Listed applications for virtual host localhost");
    }};

    private static final List<String> NON_MATCHABLE_LINES = new ArrayList<String>() {{
        add("O / Listed applications for virtual host localhost");
        add("SomeDeployment-1.0:stopped:0:SomeDeployment-1.0");
    }};

    private static final List<String> EMPTY_LINES = new ArrayList<>();

    private TomcatResponseParser parser = new TomcatResponseParser();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testResponseParser() {

        TomcatResponse response = parser.parse(LINES);

        assertEquals(OK, response.getResponseHeader().getResponseStatus());
        assertEquals(Boolean.TRUE, response.isOk());
        assertEquals("Listed applications for virtual host localhost", response.getResponseHeader().getResponseReason().getResponseReason());
        assertEquals(4, response.getResponseBody().getBody().size());
    }

    @Test
    public void testResponseParserWithEmptyResponse() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("No lines from Tomcat, can not construct a response.");

        parser.parse(EMPTY_LINES);
    }

    @Test
    public void testResponseParserWithNonMatchableResponse() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Header returned back from Tomcat can not be parsed: O / Listed applications for virtual host localhost.");

        parser.parse(NON_MATCHABLE_LINES);
    }

    @Test
    public void testResponseParserWithHeaderOnly() {
        TomcatResponse response = parser.parse(ONLY_HEADER);

        assertEquals(OK, response.getResponseHeader().getResponseStatus());
        assertTrue(response.getResponseBody().getBody().isEmpty());
    }

    @Test
    public void tomcatResponseTest() {
        TomcatResponseHeader okHeader = new TomcatResponseHeader(TomcatResponseStatus.OK, new TomcatResponseReason("response header"));
        TomcatResponseHeader failHeader = new TomcatResponseHeader(TomcatResponseStatus.FAIL, new TomcatResponseReason("response header"));

        assertTrue(new TomcatResponse(okHeader, new TomcatResponseBody().addLines(DEPLOYMENTS)).isOk());
        assertFalse(new TomcatResponse(failHeader, new TomcatResponseBody().addLines(DEPLOYMENTS)).isOk());
    }
}