package travel.snapshot.qa.manager.tomcat.api.response;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TomcatResponseParser {

    private final Pattern headerPattern = Pattern.compile("^(OK|FAIL) - (.*)");

    /**
     * Parses response from Tomcat server to model classes.
     *
     * @param lines returned lines from Tomcat server to filter
     * @return parsed Tomcat response
     * @throws IllegalStateException if parsing was unsuccessful
     */
    public TomcatResponse parse(List<String> lines) throws IllegalStateException {

        if (lines.isEmpty()) {
            throw new IllegalStateException("No lines from Tomcat, can not construct a response.");
        }

        String header = lines.get(0);

        TomcatResponse commandResponse = new TomcatResponse(parseHeader(header));

        if (lines.size() > 0) {
            List<String> body = lines.subList(1, lines.size());
            commandResponse.setResponseBody(parseBody(body));
        }

        return commandResponse;
    }

    private TomcatResponseHeader parseHeader(String headerLine) {
        Matcher headerMatcher = headerPattern.matcher(headerLine);

        if (!headerMatcher.matches()) {
            throw new IllegalStateException(String.format("Header returned back from Tomcat can not be parsed: %s.", headerLine));
        }

        TomcatResponseStatus reponseStatus = TomcatResponseStatus.valueOf(headerMatcher.group(1));
        TomcatResponseReason responseReason = new TomcatResponseReason(headerMatcher.group(2));

        return new TomcatResponseHeader(reponseStatus, responseReason);
    }

    private TomcatResponseBody parseBody(List<String> bodyLines) {
        TomcatResponseBody responseBody = new TomcatResponseBody();
        responseBody.setBody(bodyLines);
        return responseBody;
    }
}
