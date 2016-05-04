package travel.snapshot.qa.manager.jboss;

import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.as.controller.client.helpers.domain.DomainClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import travel.snapshot.qa.category.UnitTest;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;
import travel.snapshot.qa.manager.jboss.impl.ArchiveDeployer;
import travel.snapshot.qa.manager.jboss.impl.ModelControllerClientBuilder;

import java.util.Collections;

@Category(UnitTest.class)
public class ArchiveDeployerTestCase {

    private final JBossManagerConfiguration configuration = new JBossManagerConfiguration.Builder().domain().build();

    private DomainClient domainClient;

    private ArchiveDeployer archiveDeployer;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        domainClient = DomainClient.Factory.create(new ModelControllerClientBuilder.Domain(configuration).build());
        archiveDeployer = new ArchiveDeployer(domainClient.getDeploymentManager());
    }

    @Test
    public void emptyServerGroupsUponUndeployment() throws Exception {
        expectedException.expect(DeploymentException.class);
        expectedException.expectMessage("No target server groups to undeploy from.");

        archiveDeployer.undeploy("someDeploymentName", Collections.emptySet());
    }
}
