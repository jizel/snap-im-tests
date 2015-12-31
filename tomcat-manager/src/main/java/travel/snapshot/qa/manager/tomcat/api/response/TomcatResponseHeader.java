package travel.snapshot.qa.manager.tomcat.api.response;

/**
 * Represents the first line of a Tomcat response which consists of a status and a reason.
 */
public class TomcatResponseHeader {

    private TomcatResponseStatus responseStatus;

    private TomcatResponseReason responseReason;

    public TomcatResponseHeader(TomcatResponseStatus responseStatus, TomcatResponseReason responseReason) {
        this.responseStatus = responseStatus;
        this.responseReason = responseReason;
    }

    public TomcatResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public TomcatResponseReason getResponseReason() {
        return responseReason;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(responseStatus.toString());
        sb.append(" - ");
        sb.append(responseReason.toString());

        return sb.toString();
    }
}
