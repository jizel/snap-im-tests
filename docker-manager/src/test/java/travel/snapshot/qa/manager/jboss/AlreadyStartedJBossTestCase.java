package travel.snapshot.qa.manager.jboss;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.execution.ExecutionException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import travel.snapshot.qa.category.JBossTest;
import travel.snapshot.qa.manager.api.container.ContainerManagerException;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;
import travel.snapshot.qa.manager.jboss.spacelift.JBossStarter;
import travel.snapshot.qa.manager.jboss.spacelift.JBossStopper;
import travel.snapshot.qa.manager.jboss.util.TestUtils;

@Category(JBossTest.class)
public class AlreadyStartedJBossTestCase {

    private static JBossManager manager;

    private static JBossManagerConfiguration configuration;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void startContainer() {

        configuration = new JBossManagerConfiguration().setContainerType(TestUtils.getContainerType()).setJBossHome(TestUtils.getJBossHome());

        manager = Spacelift.task(configuration, JBossStarter.class).execute().await();
    }

    @AfterClass
    public static void stopContainer() {
        Spacelift.task(manager, JBossStopper.class).execute().await();
    }

    @Test
    public void alreadyStartedJBossContainerTest() {

        expectedException.expect(ExecutionException.class);
        expectedException.expectCause(is(instanceOf(ContainerManagerException.class)));

        Spacelift.task(configuration, JBossStarter.class).execute().await();
    }
}
