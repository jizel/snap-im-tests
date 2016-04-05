package travel.snapshot.qa.manager.jboss.impl;

import org.jboss.as.arquillian.container.Authentication;
import org.jboss.as.controller.client.ModelControllerClient;
import travel.snapshot.qa.manager.api.container.ContainerManagerException;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;

import java.net.UnknownHostException;

public class ModelControllerClientBuilder {

    private static final String EMPTY_STRING = "";

    public static class Standalone {

        private final JBossManagerConfiguration configuration;

        public Standalone(JBossManagerConfiguration configuration) {
            this.configuration = configuration;
        }

        public ModelControllerClient build() {

            ModelControllerClient modelControllerClient;

            Authentication.username = configuration.getUser();
            Authentication.password = configuration.getPassword();

            try {
                modelControllerClient = ModelControllerClient.Factory.create(
                        configuration.getManagementProtocol(),
                        configuration.getManagementAddress(),
                        configuration.getManagementPort(),
                        Authentication.getCallbackHandler());
            } catch (UnknownHostException ex) {
                Authentication.username = EMPTY_STRING;
                Authentication.password = EMPTY_STRING;
                throw new ContainerManagerException("Unable to construct model controller client: ", ex);
            }

            return modelControllerClient;
        }
    }

    public static class Domain {

        private final JBossManagerConfiguration configuration;

        public Domain(JBossManagerConfiguration configuration) {
            this.configuration = configuration;
        }

        public ModelControllerClient build() {
            ModelControllerClient modelControllerClient;

            org.jboss.as.arquillian.container.domain.Authentication.username = configuration.getUser();
            org.jboss.as.arquillian.container.domain.Authentication.password = configuration.getPassword();

            try {

                modelControllerClient = ModelControllerClient.Factory.create(
                        configuration.getManagementAddress(),
                        configuration.getManagementPort(),
                        org.jboss.as.arquillian.container.domain.Authentication.getCallbackHandler());
            } catch (UnknownHostException ex) {
                org.jboss.as.arquillian.container.domain.Authentication.username = EMPTY_STRING;
                org.jboss.as.arquillian.container.domain.Authentication.password = EMPTY_STRING;
                throw new ContainerManagerException("Unable to construct domain model controller client: ", ex);
            }

            return modelControllerClient;
        }
    }
}