package travel.snapshot.qa.manager.tomcat.impl;

import org.apache.http.client.methods.HttpGet;
import org.jboss.shrinkwrap.api.Archive;
import travel.snapshot.qa.manager.api.container.ContainerDeploymentException;
import travel.snapshot.qa.manager.api.container.ContainerManagerConfigurationException;
import travel.snapshot.qa.manager.api.container.ContainerManagerException;
import travel.snapshot.qa.manager.tomcat.api.ContainerManagerCommand;
import travel.snapshot.qa.manager.tomcat.api.response.TomcatResponse;
import travel.snapshot.qa.manager.tomcat.command.deployment.TomcatUndeployCommand;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Executor which undeploys an archive from running container instance.
 */
public class TomcatArchiveUndeployCommandExecutor extends AbstractCommandExecutor {

    private Archive<?> undeployment;

    public TomcatArchiveUndeployCommandExecutor(TomcatManagerConfiguration configuration) throws ContainerManagerConfigurationException {
        super(configuration);
    }

    @Override
    public TomcatResponse execute(ContainerManagerCommand command) throws ContainerManagerException {

        if (!(command instanceof TomcatUndeployCommand)) {
            throw new IllegalStateException("Command passed to execute method is not of TomcatUndeployCommand instance.");
        }

        undeployment = ((TomcatUndeployCommand) command).getDeployment();

        final String archiveName = "/" + normalizeArchiveName(undeployment.getName());

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
