package travel.snapshot.qa.manager.tomcat.impl;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.jboss.shrinkwrap.api.Archive;
import travel.snapshot.qa.manager.tomcat.api.ContainerDeploymentException;
import travel.snapshot.qa.manager.tomcat.api.ContainerManagerCommand;
import travel.snapshot.qa.manager.tomcat.api.ContainerManagerConfigurationException;
import travel.snapshot.qa.manager.tomcat.api.ContainerManagerException;
import travel.snapshot.qa.manager.tomcat.api.response.TomcatResponse;
import travel.snapshot.qa.manager.tomcat.command.deployment.TomcatDeployCommand;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;
import travel.snapshot.qa.manager.tomcat.util.ShrinkWrapUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Executor which deploys an archive to running container instance.
 */
public class TomcatArchiveDeployCommandExecutor extends AbstractCommandExecutor {

    private Archive<?> deployment;

    public TomcatArchiveDeployCommandExecutor(TomcatManagerConfiguration configuration) throws ContainerManagerConfigurationException {
        super(configuration);
    }

    @Override
    public TomcatResponse execute(ContainerManagerCommand command) throws ContainerManagerException {

        if (!(command instanceof TomcatDeployCommand)) {
            throw new IllegalStateException("Command passed to execute method is not of TomcatDeployCommand instance.");
        }

        deployment = ((TomcatDeployCommand) command).getDeployment();

        final String archiveName = "/" + normalizeArchiveName(deployment.getName());
        final URL archiveURL = ShrinkWrapUtil.toURL(deployment);

        URLConnection connection;

        try {
            connection = archiveURL.openConnection();
        } catch (IOException ex) {
            throw new ContainerManagerException(new ContainerDeploymentException(ex));
        }

        final int contentLength = connection.getContentLength();
        InputStream stream;

        try {
            stream = new BufferedInputStream(connection.getInputStream());
        } catch (IOException ex) {
            throw new ContainerManagerException(new ContainerDeploymentException(ex));
        }

        final StringBuilder commandBuilder = new StringBuilder(command.getCommand());

        try {
            commandBuilder.append(URLEncoder.encode(archiveName, configuration.getUrlCharset().toString()));
        } catch (final UnsupportedEncodingException ex) {
            throw new ContainerManagerException(new ContainerDeploymentException("Unable to construct path for Tomcat manager.", ex));
        }

        final HttpPut httpPut = new HttpPut(configuration.getManagerUrl() + normalizeCommand(commandBuilder.toString()));

        return super.execute(httpPut, stream, ContentType.APPLICATION_OCTET_STREAM, contentLength);
    }

}
