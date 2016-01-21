package travel.snapshot.qa.manager.tomcat;

import static org.hamcrest.core.StringContains.containsString;

import org.apache.commons.io.IOUtils;
import org.arquillian.spacelift.Spacelift;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import travel.snapshot.qa.manager.tomcat.api.DeploymentState;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;
import travel.snapshot.qa.manager.tomcat.spacelift.TomcatStarter;
import travel.snapshot.qa.manager.tomcat.spacelift.TomcatStopper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * You can run these tests against already started Tomcat container, e.g. against Tomcat in Docker container when you
 * set "remote" system property to true.
 *
 * You execute this like:
 *
 * gradle clean build -Dremote=true -DremoteHost=127.0.0.1 -Dtest.single=TomcatDeploymentTestCase test
 */
@RunWith(JUnit4.class)
public class TomcatDeploymentTestCase {

    {
        testingArchive.deleteOnExit();
    }

    private TomcatManager manager;

    private static final String DEPLOYMENT_NAME = "test.war";

    private static final String NONSENSE_DEPLOYMENT_NAME = "nonsense";

    private static final String DEPLOYMENT_CONTEXT = "test";

    private static final File testingArchive = new File(DEPLOYMENT_NAME);

    private static final boolean remote = Boolean.parseBoolean(System.getProperty("remote"));

    private static final String remoteHost = System.getProperty("remote.host", "127.0.0.1");

    private Archive<WebArchive> archive = ShrinkWrap.create(WebArchive.class, DEPLOYMENT_NAME).setWebXML("web.xml");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void beforeClass() throws Exception {

        final InputStream is = TomcatDeploymentTestCase.class.getClassLoader().getResourceAsStream(DEPLOYMENT_NAME);
        Assert.assertNotNull(is);
        final OutputStream os = new FileOutputStream(testingArchive);

        IOUtils.copy(is, os);
        IOUtils.closeQuietly(is);
        IOUtils.closeQuietly(os);
    }

    @Before
    public void setup() {
        if (!remote) {
            manager = Spacelift.task(TomcatStarter.class).execute().await();
        } else {
            manager = new TomcatManager(new TomcatManagerConfiguration.Builder().setBindAddress(remoteHost).remote().build());
        }
    }

    @After
    public void teardown() {
        if (!remote) {
            Spacelift.task(manager, TomcatStopper.class).execute().await();
        }
    }

    @Test
    public void testDeploymentAndUndeployment() throws Exception {
        manager.deploy(archive);
        manager.undeploy(archive);
    }

    @Test
    public void testFileDeploymentAndUndeployment() throws Exception {
        manager.deploy(testingArchive.getAbsolutePath());

        Assert.assertTrue(manager.listDeployments().contains("test"));
        Assert.assertTrue(manager.isDeployed("test"));

        manager.undeploy(testingArchive.getAbsolutePath());
    }

    @Test
    public void testNewTomcatManager() throws Exception {

        final TomcatManagerConfiguration.Builder builder = new TomcatManagerConfiguration.Builder();

        if (remote) {
            builder.setBindAddress(remoteHost).remote();
        }

        final TomcatManager manager = new TomcatManager(builder.build());

        manager.deploy(archive);
        manager.undeploy(archive);
    }

    @Test
    public void controlDeploymentLifecycleTest() throws Exception {
        manager.deploy(archive);

        Assert.assertTrue(manager.listDeployments(DeploymentState.RUNNING).contains(DEPLOYMENT_CONTEXT));

        manager.stopDeployment(DEPLOYMENT_CONTEXT);

        Assert.assertTrue(manager.listDeployments(DeploymentState.STOPPED).contains(DEPLOYMENT_CONTEXT));

        manager.startDeployment(DEPLOYMENT_CONTEXT);

        Assert.assertTrue(manager.listDeployments(DeploymentState.RUNNING).contains(DEPLOYMENT_CONTEXT));

        manager.reloadDeployment(DEPLOYMENT_CONTEXT);

        manager.undeploy(archive);

        Assert.assertFalse(manager.listDeployments().contains(DEPLOYMENT_CONTEXT));
    }

    @Test
    public void invalidDeploymentTest() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(containsString("File for import does not exist"));
        manager.deploy(NONSENSE_DEPLOYMENT_NAME);
    }
}
