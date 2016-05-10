package travel.snapshot.qa.manager.jboss;

import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.domain.DomainClient;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import travel.snapshot.qa.category.UnitTest;
import travel.snapshot.qa.manager.api.container.ContainerManagerConfigurationException;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;
import travel.snapshot.qa.manager.jboss.impl.ManagementClientFactory;
import travel.snapshot.qa.manager.jboss.impl.ModelControllerClientBuilder;

@Category(UnitTest.class)
public class JBossManagerConstructionTestCase {

    // standalone

    private final JBossManagerConfiguration validStandaloneConfiguration = new JBossManagerConfiguration.Builder().build();

    private ModelControllerClient modelControllerClient = new ModelControllerClientBuilder.Standalone(validStandaloneConfiguration).build();

    private ManagementClient managementClient = new ManagementClientFactory.Standalone(validStandaloneConfiguration).modelControllerClient(modelControllerClient).build();

    private final JBossManagerConfiguration invalidStandaloneConfiguration = new JBossManagerConfiguration.Builder().domain().build();

    // domain

    private final JBossManagerConfiguration validDomainConfiguration = new JBossManagerConfiguration.Builder().domain().build();

    private final DomainClient domainClient = DomainClient.Factory.create(new ModelControllerClientBuilder.Domain(validDomainConfiguration).build());

    private final org.jboss.as.arquillian.container.domain.ManagementClient domainManagementClient = new ManagementClientFactory.Domain().modelControllerClient(domainClient).build();

    private final JBossManagerConfiguration invalidDomainConfiguration = new JBossManagerConfiguration.Builder().build();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void constructValidJBossStandaloneManagerTest() {
        new JBossStandaloneManager();
        new JBossStandaloneManager(validStandaloneConfiguration);
        new JBossStandaloneManager(validStandaloneConfiguration, modelControllerClient);
        new JBossStandaloneManager(validStandaloneConfiguration, managementClient);
    }

    @Test
    public void constructInvalidJBossStandaloneManagerTest() {
        expectedException.expect(ContainerManagerConfigurationException.class);
        expectedException.expectMessage("Provided JBoss manager configuration is 'domain' for standalone manager.");

        new JBossStandaloneManager(invalidStandaloneConfiguration);
    }

    @Test
    public void constructValidJBossDomainManagerTest() {
        new JBossDomainManager();
        new JBossDomainManager(validDomainConfiguration);
        new JBossDomainManager(validDomainConfiguration, domainClient);
        new JBossDomainManager(validDomainConfiguration, domainManagementClient);
    }

    @Test
    public void constructInvalidJBossDomainManagerTest() {
        expectedException.expect(ContainerManagerConfigurationException.class);
        expectedException.expectMessage("Provided JBoss manager configuration is 'standalone' for domain manager.");

        new JBossDomainManager(invalidDomainConfiguration);
    }
}
