package travel.snapshot.qa.manager.tomcat.impl;

import org.apache.http.client.methods.HttpGet;
import travel.snapshot.qa.manager.tomcat.api.ContainerManagerCommand;
import travel.snapshot.qa.manager.tomcat.api.ContainerManagerConfigurationException;
import travel.snapshot.qa.manager.tomcat.api.ContainerManagerException;
import travel.snapshot.qa.manager.tomcat.api.response.TomcatResponse;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;

public class TomcatCommandExecutor extends AbstractCommandExecutor {

    public TomcatCommandExecutor(TomcatManagerConfiguration configuration) throws ContainerManagerConfigurationException {
        super(configuration);
    }

    @Override
    public TomcatResponse execute(ContainerManagerCommand command) throws ContainerManagerException {

        final HttpGet request = new HttpGet(configuration.getManagerUrl() + normalizeCommand(command.getCommand()));

        return super.execute(request);
    }
}
