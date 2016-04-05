package travel.snapshot.qa.manager.jboss;

import org.arquillian.spacelift.Spacelift;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import travel.snapshot.qa.category.JBossTest;
import travel.snapshot.qa.manager.jboss.configuration.ContainerType;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;
import travel.snapshot.qa.manager.jboss.spacelift.JBossStarter;
import travel.snapshot.qa.manager.jboss.spacelift.JBossStopper;
import travel.snapshot.qa.manager.jboss.util.TestUtils;

@Category(JBossTest.class)
public class JBossLifecycleTestCase {

    private String JBOSS_HOME = TestUtils.getJBossHome();

    private ContainerType containerType = TestUtils.getContainerType();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void startAndStopJBossContainerStandalone() {

        Spacelift.task(JBossStarter.class)
                .configuration(new JBossManagerConfiguration().setJBossHome(JBOSS_HOME).setContainerType(containerType))
                .then(JBossStopper.class)
                .execute()
                .await();
    }

    @Test
    public void startAndStopJBossContainerDomain() {

        Spacelift.task(JBossStarter.class)
                .configuration(new JBossManagerConfiguration().setJBossHome(JBOSS_HOME).setContainerType(containerType).domain())
                .then(JBossStopper.class)
                .execute()
                .await();
    }

    @Test
    @Ignore("run this only if getenv on JBOSS_HOME is null and jboss.home system property is null as well")
    public void startAndStopJBossContainerWithoutJBossHome() {

        expectedException.expect(Exception.class);

        Spacelift.task(JBossStarter.class).then(JBossStopper.class).execute().await();
    }


}
