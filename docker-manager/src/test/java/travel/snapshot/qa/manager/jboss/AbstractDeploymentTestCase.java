package travel.snapshot.qa.manager.jboss;

import org.apache.commons.io.IOUtils;
import org.arquillian.spacelift.Spacelift;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import travel.snapshot.qa.manager.jboss.configuration.ContainerType;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;
import travel.snapshot.qa.manager.jboss.configuration.JVM;
import travel.snapshot.qa.manager.jboss.configuration.Util;
import travel.snapshot.qa.manager.jboss.spacelift.JBossDomainStarter;
import travel.snapshot.qa.manager.jboss.spacelift.JBossStandaloneStarter;
import travel.snapshot.qa.manager.jboss.spacelift.JBossStopper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbstractDeploymentTestCase {

    {
        testingArchive.deleteOnExit();
    }

    private static JVM jvm = new JVM.Builder().setJBossHome(Util.getJBossHome()).build();

    private static ContainerType containerType = Util.getContainerType();

    private static JBossManagerConfiguration configuration;

    private static JBossManagerConfiguration domainConfiguration;

    protected static JBossStandaloneManager manager;

    protected static JBossDomainManager domainManager;

    protected static final String DEPLOYMENT_NAME = "test.war";

    protected static final Archive<WebArchive> archive = ShrinkWrap.create(WebArchive.class, DEPLOYMENT_NAME).setWebXML("web.xml");

    protected static final File testingArchive = new File(DEPLOYMENT_NAME);

    public static void setup() throws Exception {
        configuration = new JBossManagerConfiguration.Builder()
                .setContainerType(containerType)
                .setJVM(jvm)
                .build();

        domainConfiguration = new JBossManagerConfiguration.Builder()
                .setContainerType(containerType)
                .domain()
                .setJVM(jvm)
                .build();

        createArchive();
    }

    public static void startInternalStandalone() {
        manager = Spacelift.task(configuration, JBossStandaloneStarter.class).execute().await();
    }

    public static void stopInternalStandalone() {
        Spacelift.task(manager, JBossStopper.class).execute().await();
    }

    public static void startInternalDomain() {
        domainManager = Spacelift.task(domainConfiguration, JBossDomainStarter.class).execute().await();
    }

    public static void stopInternalDomain() {
        Spacelift.task(domainManager, JBossStopper.class).execute().await();
    }

    private static void createArchive() throws Exception {
        final InputStream is = JBossStandaloneDeploymentTestCase.class.getClassLoader().getResourceAsStream(DEPLOYMENT_NAME);
        Assert.assertNotNull(is);

        testingArchive.delete();

        final OutputStream os = new FileOutputStream(testingArchive);

        IOUtils.copy(is, os);
        IOUtils.closeQuietly(is);
        IOUtils.closeQuietly(os);
    }
}
