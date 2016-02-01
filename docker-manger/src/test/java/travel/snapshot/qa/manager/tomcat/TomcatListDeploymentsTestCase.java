package travel.snapshot.qa.manager.tomcat;

import org.arquillian.spacelift.Spacelift;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import travel.snapshot.qa.category.TomcatTest;
import travel.snapshot.qa.manager.tomcat.api.DeploymentRecord;
import travel.snapshot.qa.manager.tomcat.api.DeploymentState;
import travel.snapshot.qa.manager.tomcat.api.Deployments;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;
import travel.snapshot.qa.manager.tomcat.spacelift.TomcatStarter;
import travel.snapshot.qa.manager.tomcat.spacelift.TomcatStopper;

import java.util.UUID;

@Category(TomcatTest.class)
public class TomcatListDeploymentsTestCase {

    private static final String DEPLOYMENT_FILE = "test.war";

    private static final String DEPLOYMENT_CONTEXT = "/test";

    private TomcatManager manager;

    private Archive<?> archive = ShrinkWrap.create(WebArchive.class, DEPLOYMENT_FILE).setWebXML("web.xml");

    @Before
    public void startContainer() {
        final TomcatManagerConfiguration configuration = new TomcatManagerConfiguration.Builder()
                .setBindAddress("127.0.0.1")
                .build();

        manager = Spacelift.task(configuration, TomcatStarter.class).execute().await();
        manager.deploy(archive);
    }

    @After
    public void stopContainer() {
        manager.undeploy(archive);
        Spacelift.task(manager, TomcatStopper.class).execute().await();
    }

    @Test
    public void listDeployments() {
        Deployments deployments = manager.listDeployments();

        Assert.assertTrue(String.format("deployment with context '%s' should be found.", DEPLOYMENT_CONTEXT),
                deployments.contains(DEPLOYMENT_CONTEXT));

        // manager is the first deployment, test archive the second
        Assert.assertTrue(deployments.size() > 0);

        DeploymentRecord deploymentRecord = deployments.getDeployment(DEPLOYMENT_CONTEXT);

        Assert.assertNotNull(String.format("deployemnt with context '%s' should be found", DEPLOYMENT_CONTEXT),
                deploymentRecord);

        Assert.assertEquals(DEPLOYMENT_CONTEXT, deploymentRecord.getContextPath());
        Assert.assertEquals(DeploymentState.RUNNING, deploymentRecord.getDeploymentState());
        Assert.assertTrue(deploymentRecord.isRunning());

        Assert.assertTrue(manager.isDeployed(DEPLOYMENT_CONTEXT));
        Assert.assertFalse(manager.isDeployed(UUID.randomUUID().toString()));
    }

}
