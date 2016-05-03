package travel.snapshot.qa.manager.jboss.impl;

import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.domain.DomainClient;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;

public class ManagementClientFactory {

    public static class Standalone {

        private final JBossManagerConfiguration configuration;

        private ModelControllerClient modelControllerClient;

        public Standalone(final JBossManagerConfiguration configuration) {
            this.configuration = configuration;
        }

        public Standalone modelControllerClient(ModelControllerClient modelControllerClient) {
            this.modelControllerClient = modelControllerClient;
            return this;
        }

        public ManagementClient build() {

            if (modelControllerClient == null) {
                modelControllerClient = new ModelControllerClientBuilder.Standalone(configuration).build();
            }

            return new ManagementClientBuilder.Standalone(configuration, modelControllerClient).build();
        }
    }

    public static class Domain {

        private DomainClient modelControllerClient;

        public Domain modelControllerClient(DomainClient modelControllerClient) {
            this.modelControllerClient = modelControllerClient;
            return this;
        }

        public org.jboss.as.arquillian.container.domain.ManagementClient build() {
            return new ManagementClientBuilder.Domain(modelControllerClient).build();
        }
    }
}