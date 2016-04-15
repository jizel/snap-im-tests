package travel.snapshot.qa.manager.tomcat.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.manager.api.configuration.Validate;
import travel.snapshot.qa.manager.api.container.ContainerManagerConfigurationException;
import travel.snapshot.qa.manager.api.container.ContainerManagerException;
import travel.snapshot.qa.manager.tomcat.api.CommandExecutor;
import travel.snapshot.qa.manager.tomcat.api.response.TomcatResponse;
import travel.snapshot.qa.manager.tomcat.api.response.TomcatResponseParser;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Encapsulates core communication primitives to Tomcat container instance.
 */
public abstract class AbstractCommandExecutor implements CommandExecutor {

    private static final Logger logger = LoggerFactory.getLogger(AbstractCommandExecutor.class);

    private static final String TOMCAT_MANAGER_USER_AGENT = "Snapshot-Tomcat-Manager/1.0";

    private static final String ROOT_WAR = "ROOT.war";

    private static final String EMPTY_STRING = "";

    protected TomcatManagerConfiguration configuration;

    private final List<Header> defaultHeaders = new ArrayList<>();

    public AbstractCommandExecutor(TomcatManagerConfiguration configuration) throws ContainerManagerConfigurationException {
        Validate.notNull(configuration, "Configuration must not be a null object!");
        this.configuration = configuration;

        defaultHeaders.add(new BasicHeader(HttpHeaders.USER_AGENT, TOMCAT_MANAGER_USER_AGENT));
        defaultHeaders.add(new BasicHeader(HttpHeaders.ACCEPT, ContentType.TEXT_PLAIN.toString()));

        if (!Validate.isNullOrEmpty(configuration.getUser())) {
            defaultHeaders.add(new BasicHeader(HttpHeaders.AUTHORIZATION, constructHttpBasicAuthHeader()));
        }
    }

    /**
     * This method executes requests which do not sent some entity in its body.
     *
     * @param request request to execute
     * @return TomcatResponse response from Tomcat container
     */
    protected TomcatResponse execute(final HttpRequestBase request) {
        return execute(request, null, null, -1);
    }

    /**
     * Executes requests which need to send something in its body.
     *
     * @param request       request to execute
     * @param inputStream   stream of entity which gets send in request body
     * @param contentType   type of content to be sent
     * @param contentLength length of content to be sent
     * @return TomcatResponse response from Tomcat container
     */
    protected TomcatResponse execute(final HttpRequestBase request,
                                     final InputStream inputStream,
                                     final ContentType contentType,
                                     final int contentLength) {
        if (inputStream != null && contentType == null) {
            throw new IllegalArgumentException("You have to specify content type parameter when passing non-null " +
                    "input stream to the execute method.");
        }

        if (contentType != null) {
            request.setHeader(HttpHeaders.CONTENT_TYPE, contentType.toString());
        }

        if (inputStream != null && request instanceof HttpEntityEnclosingRequestBase) {
            InputStreamEntity inputStreamEntity = new InputStreamEntity(inputStream, contentLength, contentType);
            ((HttpEntityEnclosingRequestBase) request).setEntity(inputStreamEntity);
        }

        final CloseableHttpClient client = getClient();

        CloseableHttpResponse httpResponse = null;
        TomcatResponse tomcatResponse = null;

        try {
            httpResponse = executeRequest(client, request);
            checkResponse(httpResponse, request);
            tomcatResponse = processResponse(httpResponse);
        } catch (UnknownHostException ex) {
            throw new ContainerManagerException(ex);
        } finally {
            IOUtils.closeQuietly(httpResponse);
            IOUtils.closeQuietly(client);
            IOUtils.closeQuietly(inputStream);
        }

        return tomcatResponse;
    }

    protected String normalizeArchiveName(File file) {
        return normalizeArchiveName(file.getName());
    }

    protected String normalizeArchiveName(final String name) {

        Validate.notNullOrEmpty(name, "Archive name must not be a null object.");

        if (ROOT_WAR.equals(name)) {
            return EMPTY_STRING;
        }

        return StringUtils.substringBeforeLast(name, ".");
    }

    protected String normalizeCommand(String context) {
        if (!context.startsWith("/")) {
            context = "/" + context;
        }

        return context;
    }

    private CloseableHttpResponse executeRequest(final CloseableHttpClient client,
                                                 final HttpRequestBase request)
            throws ContainerManagerException, UnknownHostException {

        CloseableHttpResponse response;

        try {
            response = client.execute(request);
        } catch (UnknownHostException ex) {
            throw new UnknownHostException(String.format("Unable to connect to Tomcat instance at %s.",
                    configuration.getManagerUrl()));
        } catch (IOException ex) {
            throw new ContainerManagerException(String.format("Unable to execute %s request against %s",
                    request.getMethod(),
                    request.getURI().toString()));
        }

        return response;
    }

    private TomcatResponse processResponse(final CloseableHttpResponse response) {
        final HttpEntity entity = response.getEntity();

        if (entity == null) {
            throw new ContainerManagerException("Response entity is a null object.");
        }

        try (final InputStreamReader inputStreamReader = new InputStreamReader(entity.getContent(),
                StandardCharsets.ISO_8859_1.toString());
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            final List<String> lines = new ArrayList<>();
            String line = bufferedReader.readLine();

            while (line != null) {
                lines.add(line);
                line = bufferedReader.readLine();
            }

            TomcatResponse commandResponse = new TomcatResponseParser().parse(lines);

            logger.debug(commandResponse.toString());

            return commandResponse;
        } catch (IOException ex) {
            throw new ContainerManagerException("Unable to process entity from the response object.", ex);
        }
    }

    private void checkResponse(final CloseableHttpResponse response, final HttpRequestBase request) {

        final int httpResponseCode = response.getStatusLine().getStatusCode();
        final String httpResponsePhrase = response.getStatusLine().getReasonPhrase();
        final String uriString = request.getURI().toString();
        final String method = request.getMethod();

        // this logic was implemented based on org.apache.http.HttpStatus class

        if (httpResponseCode >= HttpStatus.SC_BAD_REQUEST && httpResponseCode < HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            final String exceptionMessage = String.format("Unable to connect to Tomcat manager. The server command " +
                            "%s on %s failed with response code %s and response phrase %s. " +
                            "Please make sure that you provided correct credentials to an user which is able to access " +
                            " Tomcat manager application.These credentials can be specified in container configuration as " +
                            "\"user\" and \"password\" properties invoked on a configuration builder.%n" +
                            "The user must have apropriate role specified in tomcat-users.xml file as well.%n",
                    method, uriString, httpResponseCode, httpResponsePhrase);
            throw new ContainerManagerException(exceptionMessage);
        } else if (httpResponseCode >= 300) {
            final String exceptionMessage = String.format("The server command %s on %s failer with response code %s " +
                    "and response phrase %s.", method, uriString, httpResponseCode, httpResponsePhrase);
            throw new ContainerManagerException(exceptionMessage);
        }
    }

    private CloseableHttpClient getClient() {
        return HttpClients.custom()
                .setDefaultHeaders(defaultHeaders)
                .disableAuthCaching()
                .setRetryHandler(new TomcatRetryRequestHandler())
                .build();
    }

    private String constructHttpBasicAuthHeader() {
        final String credentials = configuration.getUser() + ":" + configuration.getPassword();
        final String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes(configuration.getUrlCharset()));
        return "Basic " + encodedCredentials;
    }
}

