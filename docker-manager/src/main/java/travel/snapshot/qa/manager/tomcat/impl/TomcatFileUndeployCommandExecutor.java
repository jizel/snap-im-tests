package travel.snapshot.qa.manager.tomcat.impl;

import org.apache.http.client.methods.HttpGet;
import travel.snapshot.qa.manager.api.container.ContainerDeploymentException;
import travel.snapshot.qa.manager.api.container.ContainerManagerConfigurationException;
import travel.snapshot.qa.manager.api.container.ContainerManagerException;
import travel.snapshot.qa.manager.tomcat.api.ContainerManagerCommand;
import travel.snapshot.qa.manager.tomcat.api.response.TomcatResponse;
import travel.snapshot.qa.manager.tomcat.command.deployment.TomcatFileUndeployCommand;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TomcatFileUndeployCommandExecutor extends AbstractCommandExecutor {

    public TomcatFileUndeployCommandExecutor(TomcatManagerConfiguration configuration) throws ContainerManagerConfigurationException {
        super(configuration);
    }

    @Override
    public TomcatResponse execute(ContainerManagerCommand command) throws ContainerManagerException {

        if (!(command instanceof TomcatFileUndeployCommand)) {
            throw new IllegalStateException("Command passed to execute method is not of TomcatFileUndeployCommand instance.");
        }

        final String archivePath = ((TomcatFileUndeployCommand) command).getFileName();
        final String archiveName = "/" + normalizeArchiveName(new File(archivePath));
        final StringBuilder commandBuilder = new StringBuilder(command.getCommand());

        try {
            commandBuilder.append(URLEncoder.encode(archiveName, configuration.getUrlCharset().toString()));
        } catch (final UnsupportedEncodingException ex) {
            throw new ContainerManagerException(new ContainerDeploymentException("Unable to construct path for Tomcat manager", ex));
        }

        final HttpGet request = new HttpGet(configuration.getManagerUrl() + normalizeCommand(commandBuilder.toString()));

        return super.execute(request);
    }
}
