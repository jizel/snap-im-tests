package travel.snapshot.qa.manager.tomcat.impl;

import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.SocketException;

public class TomcatRetryRequestHandler implements HttpRequestRetryHandler {

    private static final int MAX_RETRIES = 5;

    @Override
    public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
        return !(exception instanceof NoHttpResponseException)
                && !(exception instanceof SocketException)
                && executionCount < MAX_RETRIES;
    }
}
