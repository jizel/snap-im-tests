package travel.snapshot.qa.manager.jboss.impl;

import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.domain.DomainClient;
import travel.snapshot.qa.manager.api.container.ContainerManagerException;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;

class ManagementClientBuilder {

    public static class Standalone {

        private final JBossManagerConfiguration configuration;

        private final ModelControllerClient modelControllerClient;

        public Standalone(JBossManagerConfiguration configuration, ModelControllerClient modelControllerClient) {
            this.configuration = configuration;
            this.modelControllerClient = modelControllerClient;
        }

        public ManagementClient build() {
            try {
                return new ManagementClient(modelControllerClient,
                        configuration.getManagement().getManagementAddress(),
                        configuration.getManagement().getManagementPort(),
                        configuration.getManagement().getManagementProtocol());
            } catch (Exception ex) {
                throw new ContainerManagerException("Unable to build management client. ", ex);
            }
        }
    }

    public static class Domain {

        private final DomainClient domainClient;

        public Domain(DomainClient domainClient) {
            this.domainClient = domainClient;
        }

        public org.jboss.as.arquillian.container.domain.ManagementClient build() {
            return new org.jboss.as.arquillian.container.domain.ManagementClient(domainClient, null, 0);
        }
    }
}
