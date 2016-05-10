package travel.snapshot.qa.manager.jboss;

import static org.junit.Assert.assertNotNull;

import org.jboss.as.controller.client.ModelControllerClient;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import travel.snapshot.qa.category.UnitTest;
import travel.snapshot.qa.manager.api.container.ContainerManagerException;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;
import travel.snapshot.qa.manager.jboss.impl.ManagementClientBuilder;
import travel.snapshot.qa.manager.jboss.impl.ModelControllerClientBuilder;

@Category(UnitTest.class)
public class ManagementClientBuilderTestCase {

    private JBossManagerConfiguration configuration = new JBossManagerConfiguration.Builder().build();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void standaloneManagementClientBuilderTest() {
        ModelControllerClient modelControllerClient = new ModelControllerClientBuilder.Standalone(configuration).build();
        assertNotNull(new ManagementClientBuilder.Standalone(configuration, modelControllerClient).build());
    }

    @Test
    public void absentModelControllerClientForstandaloneManagementClientBuilderTest() {

        expectedException.expect(ContainerManagerException.class);
        expectedException.expectMessage("Unable to build management client.");

        assertNotNull(new ManagementClientBuilder.Standalone(configuration, null).build());
    }
}
