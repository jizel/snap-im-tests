package travel.snapshot.qa.manager.jboss;

import static org.junit.Assert.assertNotNull;

import org.jboss.as.controller.client.ModelControllerClient;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import travel.snapshot.qa.category.UnitTest;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;
import travel.snapshot.qa.manager.jboss.impl.ManagementClientFactory.Standalone;
import travel.snapshot.qa.manager.jboss.impl.ModelControllerClientBuilder;

@Category(UnitTest.class)
public class ManagementClientFactoryTestCase {

    private final JBossManagerConfiguration configuration = new JBossManagerConfiguration.Builder().build();

    @Test
    public void managementClientFactoryTest() {
        Standalone standaloneManagementClientFactory = new Standalone(configuration);
        assertNotNull(standaloneManagementClientFactory.build());
    }

    @Test
    public void managementClientFactorySetManagementClientTest() {
        ModelControllerClient modelControllerClient = new ModelControllerClientBuilder.Standalone(configuration).build();
        Standalone standaloneManagementClientFactory = new Standalone(configuration).modelControllerClient(modelControllerClient);
        assertNotNull(standaloneManagementClientFactory.build());
    }
}
