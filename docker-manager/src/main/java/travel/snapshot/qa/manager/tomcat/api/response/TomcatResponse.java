package travel.snapshot.qa.manager.tomcat.api.response;

/**
 * Encapsulates whole response from Tomcat container after some command is executed against it.
 */
public class TomcatResponse {

    private TomcatResponseHeader responseHeader;

    private TomcatResponseBody responseBody = new TomcatResponseBody();

    public TomcatResponse(TomcatResponseHeader responseHeader) {
        this.responseHeader = responseHeader;
    }

    public TomcatResponse(TomcatResponseHeader responseHeader, TomcatResponseBody responseBody) {
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public TomcatResponseHeader getResponseHeader() {
        return responseHeader;
    }

    public TomcatResponseBody getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(TomcatResponseBody responseBody) {
        this.responseBody = responseBody;
    }

    public boolean isOk() {
        return responseHeader.getResponseStatus() == (TomcatResponseStatus.OK);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(responseHeader.toString());
        sb.append("\n");
        sb.append(responseBody.toString());

        return sb.toString();
    }
}
