package travel.snapshot.qa.manager.tomcat;

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
import travel.snapshot.qa.category.TomcatTest;
import travel.snapshot.qa.manager.api.container.ContainerManagerException;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;
import travel.snapshot.qa.manager.tomcat.spacelift.TomcatStarter;
import travel.snapshot.qa.manager.tomcat.spacelift.TomcatStopper;

@Category(TomcatTest.class)
public class AlreadyStartedTomcatTestCase {

    private static TomcatManager manager;

    private static final TomcatManagerConfiguration configuration = new TomcatManagerConfiguration.Builder()
            .setBindAddress("127.0.0.1")
            .build();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void startContainer() {
        manager = Spacelift.task(configuration, TomcatStarter.class).execute().await();
    }

    @AfterClass
    public static void stopContainer() {
        Spacelift.task(manager, TomcatStopper.class).execute().await();
    }

    @Test
    public void alreadyStartedTomcatContainerTest() {

        expectedException.expect(ExecutionException.class);
        expectedException.expectCause(is(instanceOf(ContainerManagerException.class)));

        Spacelift.task(configuration, TomcatStarter.class).execute().await();
    }
}
