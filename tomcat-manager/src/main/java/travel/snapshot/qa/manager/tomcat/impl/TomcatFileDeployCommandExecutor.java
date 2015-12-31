package travel.snapshot.qa.manager.tomcat.impl;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import travel.snapshot.qa.manager.tomcat.api.ContainerManagerCommand;
import travel.snapshot.qa.manager.tomcat.api.ContainerManagerException;
import travel.snapshot.qa.manager.tomcat.api.response.TomcatResponse;
import travel.snapshot.qa.manager.tomcat.command.deployment.TomcatDeployCommand;
import travel.snapshot.qa.manager.tomcat.command.deployment.TomcatFileDeployCommand;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;

import java.io.File;

public class TomcatFileDeployCommandExecutor extends AbstractCommandExecutor {

    public TomcatFileDeployCommandExecutor(TomcatManagerConfiguration configuration) {
        super(configuration);
    }

    @Override
    public TomcatResponse execute(ContainerManagerCommand command) throws ContainerManagerException {

        if (!(command instanceof TomcatFileDeployCommand)) {
            throw new IllegalStateException("Command passed to execute method is not of TomcatFileDeployCommand instance.");
        }

        final String archivePath = ((TomcatFileDeployCommand) command).getFileName();

        final Archive<?> archive = ShrinkWrap.createFromZipFile(WebArchive.class, new File(archivePath));

        return new TomcatArchiveDeployCommandExecutor(configuration).execute(new TomcatDeployCommand(archive));
    }
}
