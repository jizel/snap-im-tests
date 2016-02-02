package travel.snapshot.qa.manager.tomcat.api.response;

/**
 * Represents a reason string in the first line of the response from a Tomcat container.
 */
public class TomcatResponseReason {

    private String responseReason;

    public TomcatResponseReason(String responseReason) {
        this.responseReason = responseReason;
    }

    public String getResponseReason() {
        return responseReason;
    }

    @Override
    public String toString() {
        return responseReason;
    }
}
